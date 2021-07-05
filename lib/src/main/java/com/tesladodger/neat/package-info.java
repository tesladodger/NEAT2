/**
 * This is an implementation of NeuroEvolution of Augmenting Topologies (NEAT), as described in
 * Stanley and Miikkulainen's Evolving Neural Networks through Augmenting Topologies, 2002.
 *
 * <p>It provides a neural network ({@link com.tesladodger.neat.Genome}, with support for
 * recursive connections) and all the capabilities for evolution described in the paper.
 *
 * <p>You would normally use this library in the following way:
 *
 * <ul>
 *     <li>Create a starting topology. You are advised in the paper to start minimally: create
 *     the simplest genome possible, with only inputs and outputs fully connected. You can use the
 *     {@link com.tesladodger.neat.GenomeBuilder}, which will give you a starting genome
 *     that respects the requirements of the rest of the library. You can also create a
 *     starting genome manually, creating the nodes and connections separately and adding them
 *     to the template genome.
 *
 *     <li>Tweak the values in {@link com.tesladodger.neat.utils.Parameters}. The default
 *     parameters are general purpose and should be changed according to population size, genome
 *     size and, ultimately, the requirements of the problem.
 *
 *     <li>Generate the first population. You can use the method
 *     {@link com.tesladodger.neat.Population#spawn(com.tesladodger.neat.Genome, int)}, supplying
 *     the created template genome, which will return a list of genomes with the same topology
 *     and random weights.
 *
 *     <li>Run the simulation as you like with the provided genomes. The output of the neural
 *     network is calculated in the method
 * {@link com.tesladodger.neat.Genome#calculateOutput(double[], com.tesladodger.neat.utils.functions.ActivationFunction)}.
 *     At the end of the simulation, set the fitness of each genome according to the results of
 *     the simulation ({@link com.tesladodger.neat.Genome#setFitness(double)}).
 *
 *     <li>Generate the next generation, using
 *     {@link com.tesladodger.neat.Population#nextGeneration(java.util.List, com.tesladodger.neat.utils.InnovationHistory)}.
 *
 *     <li>Repeat the last two steps until a solution is achieved or you give up.
 * </ul>
 *
 * There is also provided in this library a class to draw a genome using {@link javax.swing}, for
 * visual debugging, and a class to save and load genomes or entire populations into a file (see
 * the {@link com.tesladodger.neat.tools} package).
 *
 * @author tesla
 */
package com.tesladodger.neat;
