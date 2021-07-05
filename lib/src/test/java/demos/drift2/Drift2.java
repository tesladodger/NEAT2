package demos.drift2;

import com.tesladodger.neat.Genome;
import com.tesladodger.neat.Population;
import com.tesladodger.neat.utils.InnovationHistory;
import com.tesladodger.neat.utils.Parameters;
import com.tesladodger.neat.GenomeBuilder;
import com.tesladodger.neat.utils.functions.ActivationFunction;
import com.tesladodger.neat.utils.functions.SigmoidActivationFunction;

import processing.core.PApplet;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Processing applet that simulates a population of cars evolving to lap around a track.
 *
 * <p>A {@link Car} has 10 sensors, which give the proximity of the car to the limits of the
 * track. This information, along with the car's current velocity, is fed to a {@link Genome} in
 * each car, and the output is used to control throttle and steering.
 *
 * <p>The car will over-steer, so the system must evolve to brake before corners.
 *
 * <p>After the population learns to complete a lap, the fitness function changes and starts to
 * take into consideration lower lap times.
 *
 * @author tesla
 * @since 1.0
 */
public class Drift2 extends PApplet {

    Track track;
    InnovationHistory history;
    Parameters params;
    Population population;
    LinkedList<Genome> genomes;
    List<Car> cars;
    ActivationFunction f;

    // limit the number of rendered cars, to allow a larger population without tanking performance:
    final int renderingCap = 100;

    public void settings () {
        size(1000, 800);
    }

    public void setup () {
        track = new Track();
        history = new InnovationHistory();
        Genome template = new GenomeBuilder(history).setNumberOfNodes(12, 3).build();

        params = new Parameters();
        params.averageWeightDifferenceCompatibilityCoefficient = .75f;
        params.compatibilityThreshold = 4f;
        params.weightMutationPower = .1;

        population = new Population(params);
        genomes = population.spawn(template, 5000);

        f = new SigmoidActivationFunction();

        cars = new LinkedList<>();
        for (Genome g : genomes) {
            cars.add(new Car(g, this));
        }
    }

    public void draw () {
        background(0, 50, 50);
        translate(width/2f-100f, height/2f-30f);
        //System.out.println((mouseX - width/2 +100) + " " + (mouseY - height/2 + 30));

        track.render(this);
        fill(100, 0, 0);

        AtomicInteger aliveCount = new AtomicInteger();
        cars.parallelStream().filter(Car::isAlive).forEach(car -> {
            car.update(track.iC, track.oC, f);
            aliveCount.getAndIncrement();
        });

        int drawnCount = 0;
        for (Car c : cars) {
            if (c.isAlive()) {
                c.render();
                if (++drawnCount > renderingCap) {
                    break;
                }
            }
        }

        if (aliveCount.get() == 0) {
            genomes = population.nextGeneration(genomes, history);
            System.out.println("Generation: " + population.getGeneration());
            System.out.println("Species: " + population.getSpecies().size());
            System.out.println("Highest fitness: " + population.getHighestFitness());
            System.out.println("Last highest Fitness: " + population.getLastHighestFitness());
            System.out.println("Generations without improvement: " + population.getGenerationsWithoutImprovement());
            System.out.println("Calculation time (seconds): " + ((double) population.getLastComputationTime() * 0.000000001));
            System.out.println();
            if (population.getHighestFitness() > 40000) {
                params.weightMutationPower = 0.005;
            } else if (population.getHighestFitness() > 10000) {
                params.weightMutationPower = 0.01;
            } else if (population.getHighestFitness() > 3000) {
                params.weightMutationPower = 0.05;
            }
            cars.clear();
            for (Genome g : genomes) {
                cars.add(new Car(g, this));
            }
        }
    }

    public static void main (String[] args) {
        Drift2 drift2 = new Drift2();
        PApplet.runSketch(new String[] {"demos.drift2.Drift2"}, drift2);
    }
}
