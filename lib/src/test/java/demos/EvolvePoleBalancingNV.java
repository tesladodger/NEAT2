package demos;

import com.tesladodger.neat.Genome;
import com.tesladodger.neat.Population;
import com.tesladodger.neat.utils.InnovationHistory;
import com.tesladodger.neat.utils.Parameters;
import com.tesladodger.neat.GenomeBuilder;
import com.tesladodger.neat.utils.functions.ActivationFunction;
import com.tesladodger.neat.utils.functions.SigmoidActivationFunction;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * Single Pole Balancing Problem without velocity information.
 */
public class EvolvePoleBalancingNV {

    /** Number of time steps for one evaluation (equivalent to around 30 minutes at 50Hz). */
    static final int SIMULATION_TIME = 100_000;

    /** Step of force calculation (in seconds, equivalent to 50Hz). */
    static final double CART_FORCE_TIME_STEP = 0.02;

    /** Number of physics calculations between every force calculation. */
    static final double PHYSICS_CALCULATION_RESOLUTION = 2;

    /** Time step of physics calculation. */
    static final double PHYSICS_TIME_STEP = CART_FORCE_TIME_STEP / PHYSICS_CALCULATION_RESOLUTION;

    /** Limit of cart x position (4.8 meters). */
    static final double TRACK_LIMIT = 2.4;

    /** A pole is balanced if its angle to vertical is less than this value. */
    static final double MAXIMUM_ALLOWED_INCLINATION = .209;

    /**
     * The fitness function is just a linear function of the operation time (time before failure
     * limits are reached).
     *
     * @param time before failure;
     *
     * @return fitness;
     */
    static double fitnessFunction (double time) {
        return time / 1000;
    }

    /**
     * Map a number between {@code min} and {@code max} to [-1, 1].
     *
     * @param x number to map;
     * @param min minimum of range;
     * @param max maximum of range;
     *
     * @return mapped number;
     */
    static double mapToUnit (double x, double min, double max) {
        return (x - min) / (max - min) * 2 - 1;
    }

    /**
     * A Cart with two poles of different lengths and weights.
     *
     * <p>Contains a genome, whose output controls the force exerted by the wheels of the cart.
     */
    static class CartSPNV {

        private final Genome genome;

        private final ActivationFunction function;

        private boolean isAlive;
        private int timeAlive;

        /*
         * Physical variables
         */

        private double theta;       // pole angle
        private double thetaDot;    // pole angular velocity

        private double x;           // cart position
        private double xDot;        // cart velocity

        /*
         * Physical constants
         */

        private static final double mc = 1;    // mass of cart
        private static final double mp = 0.1;  // mass of the pole
        private static final double l = .5;    // length from pole pivot to its center of mass

        private static final double g = -9.81; // gravitational acceleration
        private static final double maximumForce = 10;
        private static final double initialPoleAngle = 0.1;

        CartSPNV (Genome genome, ActivationFunction function) {
            this.genome = genome;
            this.function = function;
            theta = initialPoleAngle;
            isAlive = true;
        }

        /**
         * Update steps:
         *
         * <ul>
         *     <li>Check for failure. If the cart failed, set it to dead and return.</li>
         *     <li>Calculate the output of the genome, according to current state;</li>
         *     <li>Calculate the next state, according to the output of the genome;</li>
         * </ul>
         */
        void update () {
            if (x < -TRACK_LIMIT || x > TRACK_LIMIT
                    || theta < - MAXIMUM_ALLOWED_INCLINATION
                    || theta > MAXIMUM_ALLOWED_INCLINATION) {
                isAlive = false;
                genome.setFitness(fitnessFunction(timeAlive));
                return;
            }

            timeAlive++;

            double[] cartState = new double[] {
                    1, // bias
                    mapToUnit(x, -TRACK_LIMIT / 2, TRACK_LIMIT),
                    mapToUnit(theta, - MAXIMUM_ALLOWED_INCLINATION, MAXIMUM_ALLOWED_INCLINATION),
            };

            double output = genome.calculateOutput(cartState, function)[0];

            for (int i = 0; i < PHYSICS_CALCULATION_RESOLUTION; i++) {

                // force to be applied given the output of the genome
                double force = (output * maximumForce * 2) - maximumForce;

                // calculate pole's angular acceleration
                double x1 = (-force - mp * l * Math.pow(thetaDot, 2) * Math.sin(theta)) / (mc + mp);
                double x2 = ((4.0 / 3.0) - (mp * Math.pow(Math.cos(theta), 2)) / (mc + mp));
                // pole angular acceleration
                double thetaDotDot = (g * Math.sin(theta) + Math.cos(theta) * x1) / (l * x2);

                // calculate cart's acceleration
                double x3 = (Math.pow(thetaDot, 2) * Math.sin(theta)) - (thetaDotDot * Math.cos(theta));
                // cart acceleration
                double xDotDot = (force + mp * l * x3) / (mc + mp);

                // and pole's angular velocity and angle
                thetaDot += PHYSICS_TIME_STEP * thetaDotDot;
                theta += PHYSICS_TIME_STEP * thetaDot;

                // and cart's velocity and position
                xDot += PHYSICS_TIME_STEP * xDotDot;
                x += PHYSICS_TIME_STEP * xDot;
            }
        }

        public boolean isAlive () {
            return isAlive;
        }
    }

    public static void main (String[] args) {
        InnovationHistory history = new InnovationHistory();
        Genome template = new GenomeBuilder(history)
                .setNumberOfNodes(3, 1)
                .build();
        Parameters parameters = new Parameters();
        ActivationFunction function = new SigmoidActivationFunction();
        Population population = new Population(parameters);
        LinkedList<Genome> genomes = population.spawn(template, 150);
        LinkedList<CartSPNV> carts = new LinkedList<>();
        for (Genome g : genomes) {
            carts.add(new CartSPNV(g, function));
        }

        try {
            while (true) {
                // do the required number of steps for every cart
                AtomicInteger aliveCount = new AtomicInteger(-1);
                for (int i = 0; i < SIMULATION_TIME; i++) {
                    if (aliveCount.get() == 0) break;
                    else aliveCount.set(0);
                    carts.parallelStream().filter(CartSPNV::isAlive).forEach((cart) -> {
                        cart.update();
                        aliveCount.getAndIncrement();
                    });
                }

                if (aliveCount.get() > 0) {
                    // solution was found
                    System.out.println("Solutions found [" + aliveCount.get() + "]:");
                    Collection<Genome> solutions = carts.parallelStream()
                            .filter(CartSPNV::isAlive)
                            .map(cart -> cart.genome)
                            .collect(Collectors.toList());
                    solutions.forEach(System.out::println);
                    break;
                } else {
                    // no solution found yet
                    genomes = population.nextGeneration(genomes, history);
                    System.out.println("Generation: " + population.getGeneration());
                    System.out.println("Species: " + population.getSpecies().size());
                    System.out.println("Highest Fitness: " + population.getHighestFitness());
                    System.out.println();
                    carts.clear();
                    for (Genome g : genomes) {
                        carts.add(new CartSPNV(g, function));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
