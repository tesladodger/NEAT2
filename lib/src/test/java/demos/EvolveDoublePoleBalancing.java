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
 * Double Pole Balancing Problem, with velocity information.
 */
public class EvolveDoublePoleBalancing {

    /** Number of time steps for one evaluation (equivalent to around 30 minutes at 50Hz). */
    static final int SIMULATION_TIME = 100_000;

    /** Step of Runge-Kutta evaluation (in seconds, equivalent to 100Hz). */
    static final double RUNGE_KUTTA_STEP_SIZE = 0.01;

    /** Step of force calculation (in seconds, equivalent to 50Hz). */
    static final double CART_FORCE_STEP_SIZE = 0.02;

    /** Length of the track. */
    static final double TRACK_LENGTH = 4.8;

    /** A pole is balanced if its angle to vertical is less than this value. */
    static final double MAXIMUM_ALLOWED_INCLINATION = 36;

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
    static class CartDPV {

        private final Genome genome;

        private final ActivationFunction function;

        private boolean isAlive;
        private int timeAlive;

        /*
         * Physical variables
         */

        private double shortPoleAngle;
        private double longPoleAngle;
        private double shortPoleAngularVelocity;
        private double longPoleAngularVelocity;
        private double shortPoleAngularAcceleration;
        private double longPoleAngularAcceleration;

        private double cartPosition;
        private double cartVelocity;
        private double cartAcceleration;

        /*
         * Physical constants
         */

        private static final double cartMass = 5;
        private static final double shortPoleMass = 0.1;
        private static final double longPoleMass = 1;
        private static final double shortPoleLength = .1;
        private static final double longPoleLength = 1;

        private static final double gravitationalAcceleration = 9.81;
        private static final double maximumForce = 10;
        private static final double longPoleInitialAngle = 0.1;

        private static final double maximumVelocity = (maximumForce / cartMass) *
                Math.sqrt((2 * TRACK_LENGTH * cartMass) / maximumForce);

        CartDPV (Genome genome, ActivationFunction function) {
            this.genome = genome;
            this.function = function;
            longPoleAngle = longPoleInitialAngle;
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
            if (cartPosition < - TRACK_LENGTH / 2 || cartPosition > TRACK_LENGTH / 2
                    || shortPoleAngle < - MAXIMUM_ALLOWED_INCLINATION
                    || shortPoleAngle > MAXIMUM_ALLOWED_INCLINATION
                    || longPoleAngle < - MAXIMUM_ALLOWED_INCLINATION
                    || longPoleAngle > MAXIMUM_ALLOWED_INCLINATION) {
                isAlive = false;
                genome.setFitness(fitnessFunction(timeAlive));
                return;
            }

            timeAlive++;

            double[] cartState = new double[] {
                    1, // bias
                    mapToUnit(cartPosition, - TRACK_LENGTH / 2, TRACK_LENGTH),
                    mapToUnit(cartVelocity, -maximumVelocity, maximumVelocity),
                    mapToUnit(shortPoleAngle, - MAXIMUM_ALLOWED_INCLINATION, MAXIMUM_ALLOWED_INCLINATION),
                    mapToUnit(longPoleAngle, - MAXIMUM_ALLOWED_INCLINATION, MAXIMUM_ALLOWED_INCLINATION),
                    shortPoleAngularVelocity,
                    longPoleAngularAcceleration
            };

            double output = genome.calculateOutput(cartState, function)[0];

            // force to be applied given the output of the genome
            double force = (output * maximumForce * 2) - maximumForce;

            // TODO

        }

        public boolean isAlive () {
            return isAlive;
        }
    }

    public static void main (String[] args) {
        InnovationHistory history = new InnovationHistory();
        Genome template = new GenomeBuilder(history)
                .setNumberOfNodes(7, 1)
                .build();
        Parameters parameters = new Parameters();
        ActivationFunction function = new SigmoidActivationFunction();
        Population population = new Population(parameters);
        LinkedList<Genome> genomes = population.spawn(template, 150);
        LinkedList<CartDPV> carts = new LinkedList<>();
        for (Genome g : genomes) {
            carts.add(new CartDPV(g, function));
        }

        try {
            while (true) {
                // do the required number of steps for every cart
                AtomicInteger aliveCount = new AtomicInteger(-1);
                for (int i = 0; i < SIMULATION_TIME; i++) {
                    if (aliveCount.get() == 0) break;
                    else aliveCount.set(0);
                    carts.parallelStream().filter(CartDPV::isAlive).forEach((cart) -> {
                        cart.update();
                        aliveCount.getAndIncrement();
                    });
                }

                if (aliveCount.get() > 0) {
                    // solution was found
                    System.out.println("Solutions found:");
                    Collection<Genome> solutions = carts.parallelStream()
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
                        carts.add(new CartDPV(g, function));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
