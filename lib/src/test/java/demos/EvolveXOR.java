package demos;

import com.tesladodger.neat.Genome;
import com.tesladodger.neat.Population;
import com.tesladodger.neat.utils.InnovationHistory;
import com.tesladodger.neat.utils.Parameters;
import com.tesladodger.neat.GenomeBuilder;
import com.tesladodger.neat.utils.functions.ActivationFunction;
import com.tesladodger.neat.utils.functions.SigmoidActivationFunction;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;


public class EvolveXOR {

    public static final double[][] inputPatterns = new double[][] {
            {0, 0, 1},
            {0, 1, 1},
            {1, 0, 1},
            {1, 1, 1}
    };

    public static final double[] solution = new double[] {0, 1, 1, 0};

    /**
     * Checks if any genome in a population correctly solves XOR.
     *
     * @param genomes to check;
     * @param f activation function;
     *
     * @return true if there are solutions in {@code genomes};
     */
    public static boolean containsSolution (LinkedList<Genome> genomes, ActivationFunction f) {
        AtomicInteger answerCounter = new AtomicInteger();
        genomes.parallelStream().forEach(genome -> {
            int correct = 0;
            double diffSum = 0.0;
            for (int i = 0; i < 4; i++) {
                double output = genome.calculateOutput(inputPatterns[i], f)[0];
                diffSum += Math.abs(output - solution[i]);
                if (Math.round(output) == solution[i]) {
                    correct++;
                }
            }
            if (correct == 4) {
                System.out.println("Solution found: \n" + genome);
                answerCounter.getAndIncrement();
            }
            diffSum = 4 - diffSum;
            genome.setFitness(diffSum * diffSum);
        });
        return answerCounter.get() > 0;
    }

    public static void main (String[] args) {
        // create an empty innovation history
        InnovationHistory history = new InnovationHistory();

        // create a template genome using the genome builder
        Genome template = new GenomeBuilder(history)
                .setNumberOfNodes(3, 1)
                .setFullyConnected(true)
                .build();

        // create a parameters instance
        Parameters parameters = new Parameters();
        // change the probability of recursive connections to 0
        parameters.recursiveConnectionProbability = 0.0;

        // create a population using the template genome
        Population population = new Population(parameters);
        LinkedList<Genome> genomes = population.spawn(template, 150);

        // instantiate an activation function
        ActivationFunction f = new SigmoidActivationFunction();

        try {
            while (!containsSolution(genomes, f)) {
                // while a solution isn't found, keep creating new generations from the previous
                // one
                genomes = population.nextGeneration(genomes, history);
                System.out.println("Generation: " + population.getGeneration());
                System.out.println("Species: " + population.getSpecies().size());
                System.out.println("Highest Fitness: " + population.getHighestFitness());
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
