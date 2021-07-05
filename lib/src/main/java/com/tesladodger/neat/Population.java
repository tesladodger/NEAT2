package com.tesladodger.neat;

import com.tesladodger.neat.evolution.Crossover;
import com.tesladodger.neat.evolution.Mutation;
import com.tesladodger.neat.utils.InnovationHistory;
import com.tesladodger.neat.utils.Parameters;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


/**
 * A population contains the methods to create the initial group of {@link Genome}s and generate
 * a new generation from the current one.
 *
 * <p>When {@link Population#nextGeneration(List, InnovationHistory)} is called, the provided
 * generation is separated into species, the fitness of each species is calculated and a
 * potential number of offspring is assigned to it.
 *
 * @author tesla
 */
public class Population {

    /** Parameters for this population. */
    private final Parameters params;

    /** List of species in this population. */
    private final LinkedList<Species> species;

    /** Historical highest fitness of the entire population. */
    private double highestFitness;

    /** Highest fitness of the last generation. */
    private double lastHighestFitness;

    /** {@link Genome} with highest fitness from last generation. */
    private Genome lastChampion;

    /** Counts the generations where {@link Population#highestFitness} hasn't improved. */
    private int generationsWithoutImprovement;

    /** Number of generations created by this population. */
    private int generation;

    /**
     * Calculation time of the last call to
     * {@link Population#nextGeneration(List, InnovationHistory)}
     */
    private long lastComputationTime;

    /**
     * Construct a species.
     *
     * <p>You should use {@link Population#spawn(Genome, int, Random)} to create a list of genomes,
     * run the simulation an them (setting their fitness), and call
     * {@link Population#nextGeneration(List, InnovationHistory)} to create a new generation.
     * Rinse and repeat.
     *
     * <p>The provided {@link Parameters} will be the ones used for every generation. You can
     * change them in the middle of the simulation and the changes will be applied in the next
     * generation.
     *
     * @param parameters for this population;
     */
    public Population (Parameters parameters) {
        params = parameters;
        species = new LinkedList<>();
        highestFitness = 0;
        generationsWithoutImprovement = 0;
        generation = 0;
    }

    /**
     * Create a group of {@link Genome}s, generated from a given template.
     *
     * <p>The topology is the same (same number of nodes and connections), but the weights of the
     * connections will be randomly assigned in the range given by
     * {@link Parameters#weightLowerBound} and {@link Parameters#weightUpperBound}.
     *
     * <p>{@link ThreadLocalRandom} is used to set the weights of the connections of the created
     * genomes.
     *
     * @param template genome in whose structure the returned genomes will be based;
     * @param size number of genomes to return;
     *
     * @return LinkedList with the created genomes;
     */
    public LinkedList<Genome> spawn (Genome template, int size) {
        return spawn(template, size, ThreadLocalRandom.current());
    }

    /**
     * Create a group of {@link Genome}s, generated from a given template.
     *
     * <p>The topology is the same (same number of nodes and connections), but the weights of the
     * connections will be randomly assigned in the range given by
     * {@link Parameters#weightLowerBound} and {@link Parameters#weightUpperBound}.
     *
     * @param template genome in whose structure the returned genomes will be based;
     * @param size number of genomes to return;
     * @param rand random instance, to set the random weights of the genomes' connections;
     *
     * @return LinkedList with the created genomes;
     */
    public LinkedList<Genome> spawn (Genome template, int size, Random rand) {
        LinkedList<Genome> result = new LinkedList<>();
        result.add(template);

        double tempParam = params.newRandomWeightValueProbability;
        params.newRandomWeightValueProbability = 1;

        while (--size > 0) {
            Genome g = template.clone();
            // since the probability of perturbing weights is 0, it's ok to call this with null
            // history and power function
            Mutation.mutateWeights(g, null, params, rand);
            result.add(g);
        }

        params.newRandomWeightValueProbability = tempParam;

        return result;
    }

    /**
     * Create the next generation of a group of genomes.
     *
     * <p>The returned genomes are independent from the provided ones.
     *
     * <p>The generation will be subjected to speciation, population culling of the resulting
     * species, culling of species that haven't improved in a number of generations, and finally
     * mutation and crossover.
     *
     * <p>{@link ThreadLocalRandom} will be used for evolution probabilities.
     *
     * @param previousGeneration list of genomes of the previous generation;
     * @param history innovation history of this population;
     *
     * @return new generation;
     */
    public LinkedList<Genome> nextGeneration (final List<Genome> previousGeneration,
                                              InnovationHistory history) {
        return nextGeneration(previousGeneration, history, ThreadLocalRandom.current());
    }

    /**
     * Create the next generation of a group of genomes.
     *
     * <p>The returned genomes are independent from the provided ones.
     *
     * <p>The generation will be subjected to speciation, population culling of the resulting
     * species, culling of species that haven't improved in a number of generations, and finally
     * mutation and crossover.
     *
     * @param previousGeneration list of genomes of the previous generation;
     * @param history innovation history of this population;
     * @param rand random instance, influences all evolution probabilities;
     *
     * @return new generation;
     */
    public LinkedList<Genome> nextGeneration (final List<Genome> previousGeneration,
                                              InnovationHistory history, Random rand) {
        long startTime = System.nanoTime();

        speciate(previousGeneration);

        // calculate adjusted fitness, remove species that haven't improved in a number of
        // generations
        ListIterator<Species> it = species.listIterator();
        while (it.hasNext()) {
            Species s = it.next();
            s.calculateAdjustedFitness();
            if (s.getGenerationsWithoutImprovement() >= params.maxSpeciesGenerationsWithoutImprovement) {
                it.remove();
            }
        }

        // order species according to adjusted fitness
        species.sort(Species::compareTo);

        // get current champion and highest fitness
        Optional<Genome> optionalChampion = previousGeneration
                .parallelStream()
                .max(Genome::compareTo);
        if (optionalChampion.isPresent()) {
            lastChampion = optionalChampion.get();
            lastHighestFitness = lastChampion.getFitness();
        } else {
            throw new RuntimeException("Current highest fitness returned an empty double, " +
                    "something went wrong.");
        }

        // if fitness hasn't improved for many generations, only allow top two species to proceed
        if (lastHighestFitness <= highestFitness) {
            if (++generationsWithoutImprovement == params.maxGenerationsWithoutImprovement) {
                species.subList(2, species.size()).clear();
            }
        } else {
            this.highestFitness = lastHighestFitness;
            generationsWithoutImprovement = 0;
        }

        // remove less fit 50% from each species
        species.forEach(Species::cull);

        // calculate the number of potential offspring from each species
        calculateAssignedOffspring(previousGeneration.size());

        // generate the next generation according to the assigned offspring
        LinkedList<Genome> result = generateNextGeneration(history, rand);

        // choose a representative for each species for the next generation and clear them
        species.forEach(s -> {
            s.chooseRepresentative(rand);
            s.clearGenomes();
        });

        generation++;

        lastComputationTime = System.nanoTime() - startTime;
        return result;
    }

    /**
     * Separate a generation of {@link Genome}s into species.
     *
     * <p>If this method has not been called before, new species will be created. Otherwise the
     * genomes will be distributed according to their compatibility with the existing species.
     *
     * <p>The resulting list of species will be ordered by adjusted fitness.
     *
     * <p>A new representative for the species will also be randomly chosen, to represent it in
     * the next speciation.
     *
     * @param previousGeneration group of genomes to speciate;
     *
     * @see Species#calculateAdjustedFitness()
     * @see Species#chooseRepresentative(Random)
     */
    void speciate (final List<Genome> previousGeneration) {
        // separate genomes into species
        outerLoop:
        for (Genome g : previousGeneration) {
            for (Species s : species) {
                if (s.isCompatible(g)) {
                    s.addGenome(g);
                    continue outerLoop;
                }
            }
            species.add(new Species(params, g));
        }
    }

    /**
     * Calculates the potential number of offspring that will be created from the members of each
     * species in this population.
     *
     * <p>The value is proportional to the sum of shared fitness of the members of the species:
     *
     * <p>{@code potential_offspring = (shared_fitness / sum(shared_fitness)) * population_size}
     *
     * @param populationSize number of elements in the population;
     */
    void calculateAssignedOffspring (int populationSize) {
        double sum = species.parallelStream()
                .mapToDouble(Species::getAdjustedFitness)
                .sum();
        int offspringSum = 0;
        for (Species s : species) {
            int potentialOffspring = (int) ((s.getAdjustedFitness() / sum) * populationSize);
            offspringSum += potentialOffspring;
            s.setAssignedOffspring(potentialOffspring);
        }

        // since casting to int drops the decimal point, there might be fewer assigned offspring
        // than the population size: add the missing value to the fittest species
        int s0off = species.get(0).getAssignedOffspring();
        while (offspringSum++ < populationSize) {
            s0off++;
        }
        species.get(0).setAssignedOffspring(s0off);
    }

    /**
     * Creates the list containing the next generation.
     *
     * <p>Uses the assigned offspring to create the next generation of genomes;
     *
     * @param history {@link InnovationHistory} of the population;
     *
     * @return created population;
     */
    LinkedList<Genome> generateNextGeneration (InnovationHistory history, Random rand) {
        LinkedList<Genome> result = new LinkedList<>();

        for (Species s : species) {
            int offspring = s.getAssignedOffspring();
            if (offspring <= 0) {
                continue;
            }

            if (s.size() > params.copyFittestWithoutMutationThreshold) {
                result.add(s.getGenomes().get(0).clone());
                offspring--;
            }

            // create assigned offspring for current species
            while (offspring-- > 0) {
                double r = rand.nextDouble();

                if (r <= params.mutationWithoutCrossoverProbability) {
                    Genome g = s.getRandomGenome(rand).clone();
                    Mutation.mutate(g, history, params, rand);
                    result.add(g);
                } else {
                    double rIS = rand.nextDouble();
                    Genome parent1 = s.getRandomGenome(rand);
                    Genome parent2;

                    if (rIS <= params.interSpeciesMatingRate) {
                        // choose another random parent from another random species (I know the
                        // random species might be the same as the current one, but does it
                        // really matter?)
                        Species other;
                        do {
                            int i = rand.nextInt(species.size());
                            other = species.get(i);
                        } while (other.getGenomes().isEmpty());
                        parent2 = other.getRandomGenome(rand);
                    } else {
                        // choose another random parent from the same species
                        parent2 = s.getRandomGenome(rand);
                    }

                    Genome child = Crossover.mate(parent1, parent2, params, rand);

                    double rMut = rand.nextDouble();
                    if (rMut <= params.mutateChildFromCrossoverProbability) {
                        Mutation.mutate(child, history, params, rand);
                    }

                    result.add(child);
                }
            }
        }
        return result;
    }

    /**
     * @return list of species in this population;
     */
    public LinkedList<Species> getSpecies () {
        return species;
    }

    /**
     * @return historically highest fitness from this population;
     * @throws IllegalStateException if
     * {@link Population#nextGeneration(List, InnovationHistory)} hasn't been called before;
     */
    public double getHighestFitness () {
        if (generation == 0) {
            throw new IllegalStateException("Next generation must be computed at least once.");
        }
        return highestFitness;
    }

    /**
     * @return highest fitness from the last generation;
     * @throws IllegalStateException if
     * {@link Population#nextGeneration(List, InnovationHistory)} hasn't been called before;
     */
    public double getLastHighestFitness () {
        if (generation == 0) {
            throw new IllegalStateException("Next generation must be computed at least once.");
        }
        return lastHighestFitness;
    }

    /**
     * @return {@link Genome} with highest fitness from the last generation;
     * @throws IllegalStateException if
     * {@link Population#nextGeneration(List, InnovationHistory)} hasn't been called before;
     */
    public Genome getLastChampion () {
        if (generation == 0) {
            throw new IllegalStateException("Next generation must be computed at least once.");
        }
        return lastChampion;
    }

    /**
     * @return number of generations computed by this population;
     */
    public int getGeneration () {
        return generation;
    }

    /**
     * @return number of generations where {@link Population#getHighestFitness()} hasn't improved.
     */
    public int getGenerationsWithoutImprovement () {
        return generationsWithoutImprovement;
    }

    /**
     * @return calculation time of the last call to
     * {@link Population#nextGeneration(List, InnovationHistory)}, in nanoseconds;
     * @throws IllegalStateException if
     * {@link Population#nextGeneration(List, InnovationHistory)} hasn't been called before;
     */
    public long getLastComputationTime () {
        if (generation == 0) {
            throw new IllegalStateException("Next generation must be computed at least once.");
        }
        return lastComputationTime;
    }
}
