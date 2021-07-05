package demos.drift2;

import com.tesladodger.neat.Genome;
import com.tesladodger.neat.utils.functions.ActivationFunction;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import static java.lang.Math.sin;
import static java.lang.Math.cos;


/**
 * Basically copied from the previous NEAT implementation. The code is terrible, particularly
 * updateSensors(), but I'm afraid to touch it. There's no reason to parallelize track collision
 * calculations either, because cars are already updated in a parallel stream.
 */
class Car implements Comparable<Car> {

    Genome genome;

    PApplet applet;
    PImage img;

    /* Geometry related values */

    // Width and height of the rectangle.
    private static final int w = 16;
    private static final int h = 30;
    // Arm from the center to the corners
    private static final double d = 17;
    // Angle from the car's center-line to its diagonal
    private static final double a = 0.4899573263;

    /* Physics related values */

    private static final double maxVel = 5;      // Maximum velocity
    private static final double friction = 0.95; // Friction (to stop the car)
    // Coefficient for drifting. The lower the coefficient the lower the
    // friction.
    // A coefficient of 1 means the angle is equal to the desired angle, which
    // means no drifting.
    //private static final double driftCoefficient = 0.2;

    private final PVector pos;         // Position
    private double vel;  // Velocity
    private double acc;  // Acceleration
    private double ang;  // Angle of movement
    double desAng;       // Desired angle (for drifting)

    // Array with the coordinates of the tips of the 'sensor' lines.
    private final double[][] sensorCoordinates = new double[10][2];

    /* Neuro evolution related values */

    private boolean alive;

    private final double[] sensors;

    // Total distance covered before it died.
    private double distance;
    // Distance to kill it if not moving.
    private double dist;
    // Time alive.
    private long time;
    // Time to kill it if not moving.
    private long t;

    Car (Genome genome, PApplet applet) {
        this.genome = genome;
        this.applet = applet;

        sensors = new double[10];

        alive = true;
        img = applet.loadImage("assets/car.png");
        pos = new PVector(-365, -45);
        vel = 0;
        acc = 0;
        ang = 0;
        desAng = 0;
        distance = 0;
        dist = -20;
        time = t = System.currentTimeMillis();
    }

    public boolean isAlive () {
        return alive;
    }

    private void die () {
        alive = false;
        double fitness;
        time = System.currentTimeMillis() - time;
        if (distance > 5000) fitness = distance + (distance / (time * .001));
        else fitness = distance;
        genome.setFitness(fitness);
    }

    public void update (int[][] iC, int[][] oC, ActivationFunction f) {
        updateSensors(iC, oC);
        double[] inputs = new double[12];
        System.arraycopy(sensors, 0, inputs, 0, 10);
        inputs[10] = vel;
        inputs[11] = 1; // bias
        double[] output = genome.calculateOutput(inputs, f);
        move(output);
    }

    public void updateSensors (int[][] iC, int[][] oC) {
        double[][] car = getCoordinates();
        double uA, uB;
        double x1, x2, x3, x4, y1, y2, y3, y4;

        /* Collision with the track limits. */
        // Loop the points on the track.
        for (int i = 0; i < 41; i++) {
            x3 = iC[i  ][0];
            y3 = iC[i  ][1];
            x4 = iC[i+1][0];
            y4 = iC[i+1][1];
            // Loop the points of the car.
            for (int j = 0; j < 3; j++) {
                x1 = car[j  ][0];
                y1 = car[j  ][1];
                x2 = car[j+1][0];
                y2 = car[j+1][1];

                uA = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3)) /
                        ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));
                uB = ((x2-x1)*(y1-y3) - (y2-y1)*(x1-x3)) /
                        ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));

                if (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1) {
                    // Collision detected, murder the car.
                    die();
                }
            }
        }
        for (int i = 0; i < 41; i++) {
            x3 = oC[i  ][0];
            y3 = oC[i  ][1];
            x4 = oC[i+1][0];
            y4 = oC[i+1][1];
            // Loop the points of the car.
            for (int j = 0; j < 3; j++) {
                x1 = car[j  ][0];
                y1 = car[j  ][1];
                x2 = car[j+1][0];
                y2 = car[j+1][1];

                uA = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3)) /
                        ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));
                uB = ((x2-x1)*(y1-y3) - (y2-y1)*(x1-x3)) /
                        ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));

                if (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1) {
                    // Collision detected, murder the car.
                    die();
                }
            }
        }

        /* Update sensor information */
        resetSensors();
        // Check intersections between the sensors and the track limits.
        x1 = pos.x;
        y1 = pos.y;
        for (int j = 0; j < 10; j++) {
            x2 = sensorCoordinates[j][0];
            y2 = sensorCoordinates[j][1];
            // Loop the track points.
            for (int i = 0; i < iC.length - 1; i++) {
                x3 = iC[i  ][0];
                y3 = iC[i  ][1];
                x4 = iC[i+1][0];
                y4 = iC[i+1][1];

                uA = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3)) /
                        ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));
                uB = ((x2-x1)*(y1-y3) - (y2-y1)*(x1-x3)) /
                        ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));

                // If there is a collision and the value is smaller, update it.
                if (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1  && uA < sensors[j]) {
                    sensors[j] = uA;
                }
            }
            for (int i = 0; i < oC.length - 1; i++) {
                x3 = oC[i  ][0];
                y3 = oC[i  ][1];
                x4 = oC[i+1][0];
                y4 = oC[i+1][1];

                uA = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3)) /
                        ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));
                uB = ((x2-x1)*(y1-y3) - (y2-y1)*(x1-x3)) /
                        ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));

                // If there is a collision and the value is smaller, update it.
                if (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1  && uA < sensors[j]) {
                    sensors[j] = uA;
                }
            }
        }
    }

    public void move (double[] controls) {
        double force;
        if (controls[0] > .5) force = -.25f;
        else force = .25f;

        // Kill it if it's not moving.
        if (System.currentTimeMillis() - t > 2000) {
            if (distance - dist < 20) {
                die();
                return;
            }
            t = System.currentTimeMillis();
            dist = distance;
        }

        // Update the angle.
        // Drift coefficient is inversely proportional to the car's speed.
        double driftCoefficient = PApplet.map((float) -vel, 0,(float) maxVel, 0.2f, 0.1f);
        ang += (desAng - ang) * driftCoefficient;
        // Reset the angle when the car stops.
        if (Math.abs(vel) < 1) {
            ang += desAng - ang;
        }

        // Update the velocity.
        if (vel < maxVel && vel > -maxVel) {
            acc = force;
            vel += acc;
        }
        vel *= friction;

        // Update the distance covered.
        if (vel < 0) {
            distance += Math.sqrt( (vel*cos((float)ang)*vel*cos((float)ang)) + (vel*sin((float)ang)*vel*sin((float)ang)) );
        } else {
            distance -= Math.sqrt( (vel*cos((float)ang)*vel*cos((float)ang)) + (vel*sin((float)ang)*vel*sin((float)ang)) );
        }

        // Move.
        pos.y += vel*cos((float)ang);
        pos.x += vel*sin((float)ang);

        if (controls[1] > .5) turn(controls[1] * .1);
        if (controls[2] > .5) turn(-controls[2] * .1);
    }

    private void turn (double steer) {
        desAng -= steer * (vel*.3);
    }

    void render () {
        applet.pushMatrix();
        applet.translate(pos.x, pos.y);
        applet.rotate((float)-desAng);
        applet.image(img, -w/2f, -h/2f, w, h);
        //rect(-w/2, -h/2, w, h);
        applet.popMatrix();
    }

    private double[][] getCoordinates () {
        double oX = pos.x;
        double oY = pos.y;

        double x1 = oX - d * sin(a+desAng);
        double y1 = oY - d * cos(a+desAng);

        double x2 = oX + d * sin(a-desAng);
        double y2 = oY - d * cos(a-desAng);

        double x3 = oX - d * sin(a-desAng);
        double y3 = oY + d * cos(a-desAng);

        double x4 = oX - d * sin(-a-desAng);
        double y4 = oY + d * cos(-a-desAng);

        return new double[][] { {x1, y1}, {x2, y2}, {x3, y3}, {x4, y4} };
    }

    private void resetSensors () {
        sensorCoordinates[0][0] = pos.x + 240 * sin(-desAng     );  // Front
        sensorCoordinates[0][1] = pos.y - 240 * cos( desAng     );
        sensorCoordinates[1][0] = pos.x + 150 * sin( desAng     );  // Back
        sensorCoordinates[1][1] = pos.y + 150 * cos( desAng     );

        sensorCoordinates[2][0] = pos.x +  80 * cos( desAng     );  // Right
        sensorCoordinates[2][1] = pos.y -  80 * sin( desAng     );
        sensorCoordinates[3][0] = pos.x -  80 * cos( desAng     );  // Left
        sensorCoordinates[3][1] = pos.y -  80 * sin(-desAng     );

        sensorCoordinates[4][0] = pos.x + 220 * sin(-desAng+0.25);  // FFront-Right
        sensorCoordinates[4][1] = pos.y - 220 * cos( desAng-0.25);
        sensorCoordinates[5][0] = pos.x + 220 * sin(-desAng-0.25);  // FFront-Left
        sensorCoordinates[5][1] = pos.y - 220 * cos( desAng+0.25);

        sensorCoordinates[6][0] = pos.x + 180 * sin(-desAng+0.6 );  // Front-RRight
        sensorCoordinates[6][1] = pos.y - 180 * cos( desAng-0.6 );
        sensorCoordinates[7][0] = pos.x + 180 * sin(-desAng-0.6 );  // Front-LLeft
        sensorCoordinates[7][1] = pos.y - 180 * cos( desAng+0.6 );

        sensorCoordinates[8][0] = pos.x + 120 * sin( desAng+0.6 );  // Back-Right
        sensorCoordinates[8][1] = pos.y + 120 * cos( desAng+0.6 );
        sensorCoordinates[9][0] = pos.x + 120 * sin( desAng-0.6 );  // Back-Left
        sensorCoordinates[9][1] = pos.y + 120 * cos( desAng-0.6 );

        for (int i = 0; i < 10; i++) sensors[i] = 1f;
    }

    @Override
    public int compareTo (Car o) {
        return Double.compare(distance, o.distance);
    }
}
