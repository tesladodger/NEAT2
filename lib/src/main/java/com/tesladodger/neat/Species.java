package com.tesladodger.neat;

import com.tesladodger.neat.utils.Parameters;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Random;


/**
 * Group of {@link Genome}s with similar topology.
 *
 * <p>Dividing the population into species serves the purpose of allowing new topologies to
 * develop, competing only with similar topologies and not with the entire population. This way,
 * if a mutation occurs that initially decreases the fitness of a genome but, in the long term,
 * is an improvement towards solving the problem, it isn't discarded too soon and has time to
 * develop.
 *
 * <p>The threshold is given by {@link Parameters#compatibilityThreshold}, and the similarity
 * between genomes is calculated with {@link Genome#compatibilityBetween(Genome, Genome, Parameters)}.
 *
 * @author tesla
 */
public class Species implements Comparable<Species> {

    /** Parameters for this species. */
    private final Parameters params;

    /** List of genomes in this species. Ordered by fitness. */
    private final LinkedList<Genome> genomes;

    /** Random genome from the previous generation that potential genomes will be compared with. */
    private Genome representative;

    /**
     * Sum of the fitness of the genomes in this species divided by the total number of genomes.
     */
    private double adjustedFitness;

    /** Historical highest fitness of this species. */
    private double highestFitness;

    /** Counts the generations where {@link Species#highestFitness} hasn't improved. */
    private int generationsWithoutImprovement;

    /**
     * Number of genomes in a new generation that will stem from this species.
     *
     * @see Population#calculateAssignedOffspring(int)
     */
    private int assignedOffspring;

    /**
     * Default constructor. Creates an empty species, without genomes nor representative.
     *
     * @param parameters for this species (same for every species in a population);
     */
    public Species (Parameters parameters) {
        this(parameters, null);
    }

    /**
     * Creates a new species with a representative. The representative is automatically added to
     * the list of genomes, there's no need to call {@link Species#addGenome(Genome)}.
     *
     * @param parameters for this species (same for every species in a population);
     * @param representative of this species;
     */
    public Species (Parameters parameters, Genome representative) {
        params = parameters;
        genomes = new LinkedList<>();
        adjustedFitness = 0;
        highestFitness = 0.0;
        generationsWithoutImprovement = 0;
        if (representative != null) {
            setRepresentative(representative);
            addGenome(representative);
        }
    }

    /**
     * Check whether a genome is compatible with this species.
     *
     * @param genome to check;
     *
     * @return true if genome is compatible, false otherwise.
     * @throws NullPointerException if this species' representative hasn't been set;
     * @see Parameters#compatibilityThreshold
     */
    public boolean isCompatible (Genome genome) {
        Objects.requireNonNull(representative, "The representative of this species has not been " +
                "set.");
        return Genome.compatibilityBetween(genome, representative, params) <= params.compatibilityThreshold;
    }

    /**
     * Adds a genome to this species.
     *
     * <p>The genomes in this species are ordered by fitness at insertion, highest fitness first.
     * The method {@link Species#getGenomes()} always returns the members in that order.
     *
     * @param genome to add;
     */
    public void addGenome (Genome genome) {
        ListIterator<Genome> it = genomes.listIterator();
        while (true) {
            if (!it.hasNext()) {
                it.add(genome);
                return;
            }
            Genome g = it.next();
            if (g.getFitness() < genome.getFitness()) {
                it.previous();
                it.add(genome);
                return;
            }
        }
    }

    /**
     * @param rand random instance;
     *
     * @return a random genome in this species;
     */
    public Genome getRandomGenome (Random rand) {
        int r = rand.nextInt(genomes.size());
        return genomes.get(r);
    }

    /**
     * Remove the worst half of this population.
     *
     * <p>I called this method 'Thanos' in the previous version of this library :)
     */
    public void cull () {
        if (genomes.size() > 2) {
            genomes.subList((genomes.size() + 1) >> 1, genomes.size()).clear();
        }
    }

    /**
     * Choose a random representative from the members of this species, to represent it in the
     * next generation.
     *
     * <p>If the species has no genomes available, the previous representative will be maintained.
     *
     * @param rand random instance;
     *
     * @return the chosen genome or the previous representative if the species is empty;
     */
    public Genome chooseRepresentative (Random rand) {
        return genomes.isEmpty() ?
                representative :
                genomes.get(rand.nextInt(genomes.size()));
    }

    /**
     * Calculate the adjusted fitness of this species, which is the sum of the fitness of its
     * members divided by the number of members.
     *
     * <p>Also updates the species' highest fitness, and the number of generations without
     * improvement.
     *
     * @return adjusted fitness of this species;
     */
    public double calculateAdjustedFitness () {
        double sum = genomes.parallelStream().mapToDouble(Genome::getFitness).sum();

        // increment the generationsWithoutImprovement counter
        if (!genomes.isEmpty()) {
            double currentHighestFitness = genomes.get(0).getFitness();
            if (currentHighestFitness > highestFitness) {
                highestFitness = currentHighestFitness;
                generationsWithoutImprovement = 0;
            } else {
                generationsWithoutImprovement++;
            }
        } else {
            generationsWithoutImprovement++;
        }

        return adjustedFitness = genomes.isEmpty() ? 0.0 : sum / genomes.size();
    }

    /**
     * @return adjusted fitness of this species;
     *
     * @see Species#calculateAdjustedFitness()
     */
    public double getAdjustedFitness () {
        return adjustedFitness;
    }

    /**
     * @return the member {@link Genome}s of this species, in an immutable list. The returned list
     * as always ordered, since genomes are order at insertion.
     * @see Species#addGenome(Genome)
     */
    public List<Genome> getGenomes () {
        return Collections.unmodifiableList(genomes);
    }

    /**
     * Clear the list of genomes in this species. This will not clear the parameters nor the
     * representative;
     */
    public void clearGenomes () {
        genomes.clear();
    }

    /**
     * @return number of {@link Genome}s on this species;
     */
    public int size () {
        return genomes.size();
    }

    /**
     * @return the representative (the genome other are compared with to enter this species);
     */
    public Genome getRepresentative () {
        return representative;
    }

    /**
     * @param representative set the representative to {@code representative};
     */
    public void setRepresentative (Genome representative) {
        this.representative = representative;
    }

    /**
     * @return number of assigned offspring;
     */
    public int getAssignedOffspring () {
        return assignedOffspring;
    }

    /**
     * @param assignedOffspring set assigned offspring to {@code assignedOffspring};
     */
    public void setAssignedOffspring (int assignedOffspring) {
        this.assignedOffspring = assignedOffspring;
    }

    /**
     * @return historical highest fitness of this species;
     */
    public double getHighestFitness () {
        return highestFitness;
    }

    /**
     * @return number of generation where the highest fitness hasn't increased for this species;
     */
    public int getGenerationsWithoutImprovement () {
        return generationsWithoutImprovement;
    }

    @Override
    public int compareTo (Species o) {
        return Double.compare(o.adjustedFitness, adjustedFitness);
    }
}
