package com.tesladodger.neat.utils;

import com.tesladodger.neat.Connection;
import com.tesladodger.neat.Node;
import com.tesladodger.neat.GenomeBuilder;

import java.util.HashMap;


/**
 * Logs the occurrence of structural mutation (either a new {@link Connection} or a new
 * {@link Node}, thus providing the same id to a mutation that has happened before, or a new id
 * if it hasn't.
 *
 * <p>The same object should be used throughout the entirety of the simulation (every generation),
 * since it's responsible for logging the historical markings of mutation through the generations.
 *
 * <p>It also logs the 'age' of the gene: how many generations ago it appeared. This creates the
 * possibility of biasing mutation towards younger genes, since older genes have been selected
 * for longer and are probably fittest.
 *
 * <p>This class is completely agnostic to the actual topology of the genome, and checking if the
 * inputs actually make sense given the topology is of the responsibility of the client.
 *
 * @author tesla
 * @version 1.0
 */
public class InnovationHistory {

    /**
     * Map from {@link NewNodeMutation}, which represents a mutation, and the integer id that was
     * assigned to the new {@link Node}.
     */
    private final HashMap<Integer, NewNodeMutation> newNodeMutations = new HashMap<>();

    /**
     * Map from {@link NewConnectionMutation}, which represents a mutation, and the integer
     * innovation number that was assigned to the new connection.
     */
    private final HashMap<ConnectionMutationKey, NewConnectionMutation> newConnectionMutations = new HashMap<>();

    /** Highest node id given so far. */
    private int nodeIdCounter;

    /** Highest innovation number given so far. */
    private int innovationNumberCounter;

    /** Value returned by {@link InnovationHistory#getNewNodeMutationId(int)}. */
    private int lastReturnedNodeId;

    /**
     * Value returned by
     * {@link InnovationHistory#getNewConnectionMutationInnovationNumber(int, int)}.
     */
    private int lastReturnedInnovationNumber;

    private int generationCounter;

    /**
     * Default constructor.
     *
     * <p>The first node id to be assigned will be 0, as well as the first innovation number.
     */
    public InnovationHistory () {
        this(-1, -1);
    }

    /**
     * Constructor for a non-empty starting topology. Only use when creating a genome by hand. If
     * using {@link GenomeBuilder}, a default innovation history must be provided, since that
     * class takes care of its details.
     *
     * @param initialHighestNodeId highest node id of the starting topology;
     * @param initialHighestInnovationNumber highest innovation number of the starting topology;
     */
    public InnovationHistory (int initialHighestNodeId, int initialHighestInnovationNumber) {
        nodeIdCounter = initialHighestNodeId;
        innovationNumberCounter = initialHighestInnovationNumber;
        lastReturnedNodeId = -1;
        lastReturnedInnovationNumber = -1;
        generationCounter = 0;
    }

    /**
     * Set the highest node id in the starting topology, so that ids don't repeat.
     *
     * @param nodeId initial highest id;
     */
    public void setInitialHighestNodeId (int nodeId) {
        nodeIdCounter = nodeId;
    }

    /**
     * Set the initial highest innovation number in the starting topology, so that innovation
     * numbers don't repeat.
     *
     * @param innovationNumber initial highest innovation number;
     */
    public void setInitialHighestInnovationNumber (int innovationNumber) {
        innovationNumberCounter = innovationNumber;
    }

    /**
     * When a new node mutation occurs, this method is called to determine the
     * {@link Node#getId()} of the new node. If the mutation has occurred before, the number
     * returned is that of the node it represents.
     *
     * @param connectionInnovationNumber {@link Connection#getInnovationNumber()} of the
     *                                   connection that was broken-up by the new node;
     *
     * @return id of the new node;
     */
    public int getNewNodeMutationId (int connectionInnovationNumber) {
        return lastReturnedNodeId = newNodeMutations.computeIfAbsent(
                connectionInnovationNumber,
                k -> new NewNodeMutation(++nodeIdCounter)
        ).nodeId;
    }

    /**
     * When a new connection mutation occurs, this method is called to determine the
     * {@link Connection#getInnovationNumber()} of the new connection. If the mutation has
     * occurred before, the number returned is that of the connection it represents.
     *
     * @param inNodeId {@link Node#getId()} of the in-node;
     * @param outNodeId {@link Node#getId()} of the out-node;
     *
     * @return innovation number for the new connection;
     */
    public int getNewConnectionMutationInnovationNumber (int inNodeId, int outNodeId) {
        return lastReturnedInnovationNumber = newConnectionMutations.computeIfAbsent(
                new ConnectionMutationKey(inNodeId, outNodeId),
                k -> new NewConnectionMutation(++innovationNumberCounter)
        ).innovationNumber;
    }

    /**
     * Increments the age of all the connections in this history by one.
     *
     * <p>The age should be incremented after every generation, since it represents the number of
     * generations a mutation has existed for.
     */
    public void incrementConnectionAges () {
        generationCounter++;
        newConnectionMutations.values().forEach(NewConnectionMutation::incrementAge);
    }

    /**
     * Returns the age a connection has existed for (number of generations, updated by
     * {@link InnovationHistory#incrementConnectionAges()}.
     *
     * <p>If for some reason the connection isn't present in this history, the highest number of
     * generations will be returned.
     *
     * @param inNodeId id of the connection's input node;
     * @param outNodeId id of the connection's output node;
     *
     * @return age of the connection;
     */
    public int getConnectionAge (int inNodeId, int outNodeId) {
        ConnectionMutationKey k = new ConnectionMutationKey(inNodeId, outNodeId);
        NewConnectionMutation mut;
        return (mut = newConnectionMutations.get(k)) == null ? generationCounter : mut.age;
    }

    /**
     * Delete all history and reset the IDs to the given values.
     *
     * @param initialHighestNodeId highest node id of the starting topology;
     * @param initialHighestInnovationNumber highest innovation number of the starting topology;r
     */
    public void reset (int initialHighestNodeId, int initialHighestInnovationNumber) {
        nodeIdCounter = initialHighestNodeId;
        innovationNumberCounter = initialHighestInnovationNumber;
        generationCounter = 0;
        newNodeMutations.clear();
        newConnectionMutations.clear();
    }

    /**
     * Delete all history and reset the IDs to 0.
     */
    public void reset () {
        reset(-1, -1);
    }

    /**
     * @return highest node id ever assigned by this history;
     */
    public int getCurrentHighestNodeId () {
        return nodeIdCounter;
    }

    /**
     * @return highest innovation number ever assigned by this history;
     */
    public int getCurrentHighestInnovationNumber () {
        return innovationNumberCounter;
    }

    /**
     * @return the last value returned by {@link InnovationHistory#getNewNodeMutationId(int)};
     * @throws IllegalStateException if that method has never been called;
     */
    public int getLastReturnedNodeId () {
        if (lastReturnedNodeId == -1) {
            throw new IllegalStateException("A new node id has never been assigned by this " +
                    "history.");
        }
        return lastReturnedNodeId;
    }

    /**
     * @return the last value returned by
     * {@link InnovationHistory#getNewConnectionMutationInnovationNumber(int, int)};
     * @throws IllegalStateException if that method has never been called;
     */
    public int getLastReturnedInnovationNumber () {
        if (lastReturnedInnovationNumber == -1) {
            throw new IllegalStateException("A new innovation number has never been assigned by " +
                    "this history.");
        }
        return lastReturnedInnovationNumber;
    }

    /**
     * Represents a mutation of insertion of a node, represented by the resulting node id.
     */
    private static class NewNodeMutation {

        private final int nodeId;

        private NewNodeMutation (int nodeId) {
            this.nodeId = nodeId;
        }
    }

    /**
     * Key used to identify connection mutations, identified by the in and out node ids.
     */
    private static class ConnectionMutationKey {
        private final int inNodeId;
        private final int outNodeId;

        private ConnectionMutationKey (int inNodeId, int outNodeId) {
            this.inNodeId = inNodeId;
            this.outNodeId = outNodeId;
        }

        @Override
        public boolean equals (Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConnectionMutationKey that = (ConnectionMutationKey) o;
            return inNodeId == that.inNodeId && outNodeId == that.outNodeId;
        }

        @Override
        public int hashCode () {
            int result = 23;
            result = result * 37 + inNodeId;
            result = result * 37 + outNodeId;
            return result;
        }
    }

    /**
     * Represents a new connection mutation, identified by the innovation number.
     *
     * <p>It also tracks the age of the mutation, which is the number of generations it has
     * existed for.
     */
    private static class NewConnectionMutation {

        private final int innovationNumber;

        private int age;

        private NewConnectionMutation (int innovationNumber) {
            this.innovationNumber = innovationNumber;
            age = 0;
        }

        private void incrementAge () {
            age++;
        }
    }
}
