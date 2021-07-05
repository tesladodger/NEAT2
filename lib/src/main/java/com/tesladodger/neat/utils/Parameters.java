package com.tesladodger.neat.utils;

import com.tesladodger.neat.Genome;
import com.tesladodger.neat.evolution.Crossover;
import com.tesladodger.neat.evolution.Mutation;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;


/**
 * Context for all parameters that influence the way a NEAT instance works.
 *
 * <p>There are no checks for the validity of the parameters throughout the library, any values
 * set outside the documented ranges might fail unexpectedly.
 *
 * <p>This class is {@link Serializable}, so you can save the parameters of an experiment and
 * reuse them later.
 *
 * @author tesla
 * @version 1.0
 */
public class Parameters implements Serializable {

    /*
     * Crossover parameters.
     */

    /**
     * Probability of disabling a gene if it is disabled in either parent.
     *
     * <p>Should be in range [0,1], but nothing will break if it isn't.
     */
    public double disableGeneProbability = 0.75;

    /**
     * Probability of re-enabling a gene that is disabled on both parents or is a disabled
     * disjoint gene.
     *
     * <p>Should be in range [0,1], but nothing will break if it isn't.
     */
    public double reEnableGeneProbability = 0.01;

    /**
     * If the difference in {@link Genome#getFitness()} is less than this tolerance, the genomes
     * are considered equivalent in fitness.
     *
     * <p>This value is used in {@link Crossover}, to assert which parent has the highest fitness
     * and, therefore, highest priority at donating genes.
     *
     * <p>The value should depend on the range of fitness of the particular problem, and values
     * bellow 0 will lead to unexpected results;
     */
    public double fitnessTolerance = 0.0;

    /**
     * Probability between 0 and 50% of matching genes being inherited from the fittest parent.
     * If set to 0%, both parent have the same chance. If set to 50% (0.5), matching genes will
     * always be inherited by the fittest parent. Negative values in the same range will give the
     * advantage to the less fit parent.
     *
     * <p>Values should be in [-0.5,0.5], otherwise the probability will always be 1.
     */
    public double fittestParentBias = 0.2;

    /*
     * Mutation parameters.
     */

    /**
     * Probability of altering the weights of the genome, when calling
     * {@link Mutation#mutate(Genome, InnovationHistory, Parameters, Random)}.
     *
     * <p>The sum of the parameters {@link Parameters#newNodeMutationProbability},
     * {@link Parameters#newConnectionMutationProbability} and this one should be less than or
     * equal to 1, otherwise it might cause unexpected results.
     */
    public double connectionWeightsMutationProbability = 0.8;

    /**
     * Probability of assigning a new random value to a weight. Otherwise it will be uniformly
     * perturbed, according to {@link Parameters#weightMutationPower}.
     *
     * <p>Should be in range [0,1], but nothing will break if it isn't.
     */
    public double newRandomWeightValueProbability = 0.1;

    /**
     * Probability of {@link Mutation#addNodeMutation(Genome, InnovationHistory, Random)}.
     *
     * <p>The sum of the parameters {@link Parameters#connectionWeightsMutationProbability},
     * {@link Parameters#newConnectionMutationProbability} and this one should be less than or
     * equal to 1, otherwise it might cause unexpected results.
     */
    public double newNodeMutationProbability = 0.03;

    /**
     * Probability of
     * {@link Mutation#addConnectionMutation(Genome, InnovationHistory, Parameters, Random)}.
     *
     * <p>The sum of the parameters {@link Parameters#newNodeMutationProbability},
     * {@link Parameters#connectionWeightsMutationProbability} and this one should be less than or
     * equal to 1, otherwise it might cause unexpected results.
     */
    public double newConnectionMutationProbability = 0.05;

    /**
     * Probability of creating a recursive connection when adding a connection to a genome.
     *
     * <p>Should be in range [0,1], but nothing will break if it isn't.
     */
    public double recursiveConnectionProbability = 0.2;

    /**
     * How much the connection weights are perturbed by mutation: a perturbation is a random
     * value in a gaussian distribution with a standard deviation equal to this value.
     *
     * <p>It has to be experimentally derived, but the value should decrease with a larger
     * population.
     *
     * <p>Another option is to decrease this value the closer the system is to a solution,
     * assuming only finer tuning is needed to reach it.
     */
    public double weightMutationPower = 1.0;

    /** When assigning a new random weight to a connection, this is the minimum value. */
    public double weightLowerBound = -10;

    /** When assigning a new random weight to a connection, this is the maximum value. */
    public double weightUpperBound = 10;

    /**
     * Still unimplemented TODO
     *
     * <p>When mutating the weights of a genome, bias towards more recent genes (higher
     * innovation number).
     */
    public double mutateRecentGenesBias = 0;

    /**
     * Still unimplemented TODO
     *
     * <p>Only apply {@link Parameters#mutateRecentGenesBias} to genomes with an equal or larger
     * number of connections than this value.
     */
    public int mutateRecentGenesThreshold = 10;

    /*
     * Genome parameters.
     */

    /**
     * How much excess genes influence the compatibility value between two genomes.
     *
     * @see Genome#compatibilityBetween(Genome, Genome, Parameters)
     */
    public float excessGenesCompatibilityCoefficient = 1.0f;

    /**
     * How much disjoint genes influence the compatibility value between two genomes.
     *
     * @see Genome#compatibilityBetween(Genome, Genome, Parameters)
     */
    public float disjointGenesCompatibilityCoefficient = 1.0f;

    /**
     * How much the average weight difference of the matching genes between two genomes influence
     * the compatibility value between them.
     *
     * @see Genome#compatibilityBetween(Genome, Genome, Parameters)
     */
    public float averageWeightDifferenceCompatibilityCoefficient =  0.4f;

    /**
     * @see Genome#compatibilityBetween(Genome, Genome, Parameters)
     */
    public float largeGenomeNormalizerThreshold = 20;

    /*
     * Population parameters.
     */

    /**
     * When the fitness of the population doesn't increase for more than this number of
     * generations, only the top two species will reproduce, refocusing the search into the most
     * promising spaces.
     */
    public int maxGenerationsWithoutImprovement = 20;

    /**
     * Probability of a {@link Genome} in the new generation is the result of mutation without
     * crossover.
     */
    public double mutationWithoutCrossoverProbability = 0.25;

    /** Probability of a child from {@link Crossover} being mutated after its creation. */
    public double mutateChildFromCrossoverProbability = 0.5;

    /**
     * When applying {@link Crossover}, the probability of the
     * two genomes being from different species.
     */
    public double interSpeciesMatingRate = 0.001;

    /**
     * Copy the fittest genome without mutation in every species with more than this number of
     * genomes.
     */
    public int copyFittestWithoutMutationThreshold = 5;

    /*
     * Species parameters.
     */

    /**
     * If a {@link Genome}'s compatibility with the representative of this species is less than
     * or equal to this threshold, it is added to this species.
     *
     * @see Genome#compatibilityBetween(Genome, Genome, Parameters)
     */
    public float compatibilityThreshold = 3.0f;

    /**
     * When a species' highest fitness value doesn't increase for more than this number of
     * generations, its members are not allowed to reproduce.
     */
    public float maxSpeciesGenerationsWithoutImprovement = 15;

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameters that = (Parameters) o;
        return Double.compare(that.disableGeneProbability, disableGeneProbability) == 0 &&
                Double.compare(that.reEnableGeneProbability, reEnableGeneProbability) == 0 &&
                Double.compare(that.fitnessTolerance, fitnessTolerance) == 0 &&
                Double.compare(that.fittestParentBias, fittestParentBias) == 0 &&
                Double.compare(that.connectionWeightsMutationProbability,
                        connectionWeightsMutationProbability) == 0 &&
                Double.compare(that.newRandomWeightValueProbability,
                        newRandomWeightValueProbability) == 0 &&
                Double.compare(that.newNodeMutationProbability, newNodeMutationProbability) == 0 &&
                Double.compare(that.newConnectionMutationProbability,
                        newConnectionMutationProbability) == 0 &&
                Double.compare(that.recursiveConnectionProbability,
                        recursiveConnectionProbability) == 0 &&
                Double.compare(that.weightMutationPower, weightMutationPower) == 0 &&
                Double.compare(that.weightLowerBound, weightLowerBound) == 0 &&
                Double.compare(that.weightUpperBound, weightUpperBound) == 0 &&
                Float.compare(that.excessGenesCompatibilityCoefficient,
                        excessGenesCompatibilityCoefficient) == 0 &&
                Float.compare(that.disjointGenesCompatibilityCoefficient,
                        disjointGenesCompatibilityCoefficient) == 0 &&
                Float.compare(that.averageWeightDifferenceCompatibilityCoefficient,
                        averageWeightDifferenceCompatibilityCoefficient) == 0 &&
                Float.compare(that.largeGenomeNormalizerThreshold,
                        largeGenomeNormalizerThreshold) == 0 &&
                maxGenerationsWithoutImprovement == that.maxGenerationsWithoutImprovement &&
                Double.compare(that.mutationWithoutCrossoverProbability,
                        mutationWithoutCrossoverProbability) == 0 &&
                Double.compare(that.mutateChildFromCrossoverProbability,
                        mutateChildFromCrossoverProbability) == 0 &&
                Double.compare(that.interSpeciesMatingRate, interSpeciesMatingRate) == 0 &&
                copyFittestWithoutMutationThreshold == that.copyFittestWithoutMutationThreshold &&
                Float.compare(that.compatibilityThreshold, compatibilityThreshold) == 0 &&
                Float.compare(that.maxSpeciesGenerationsWithoutImprovement,
                        maxSpeciesGenerationsWithoutImprovement) == 0;
    }

    @Override
    public int hashCode () {
        return Objects.hash(disableGeneProbability, reEnableGeneProbability, fitnessTolerance,
                fittestParentBias, connectionWeightsMutationProbability,
                newRandomWeightValueProbability, newNodeMutationProbability,
                newConnectionMutationProbability, recursiveConnectionProbability,
                weightMutationPower, weightLowerBound, weightUpperBound,
                excessGenesCompatibilityCoefficient, disjointGenesCompatibilityCoefficient,
                averageWeightDifferenceCompatibilityCoefficient, largeGenomeNormalizerThreshold,
                maxGenerationsWithoutImprovement, mutationWithoutCrossoverProbability,
                mutateChildFromCrossoverProbability, interSpeciesMatingRate,
                copyFittestWithoutMutationThreshold, compatibilityThreshold,
                maxSpeciesGenerationsWithoutImprovement);
    }
}
