package com.tesladodger.neat;

import com.tesladodger.neat.utils.functions.ActivationFunction;


/**
 * Represents an element of a {@link Genome}.
 *
 * <p>A {@link Connection} into this node will add to its input. This node will then calculate
 * its activation value, according to its input and the {@link ActivationFunction} being used,
 * and the resulting value will be forwarded to other nodes this one is connected to.
 *
 * <p>There are three types of node, according to the role they play in the genome they belong
 * to: inputs, hidden nodes and outputs.
 *
 * @author tesla
 * @version 1.0
 */
public class Node implements Cloneable, Comparable<Node> {

    /** Types of node. */
    public enum Type {

        /**
         * Input nodes are the entry point of the genome network, its bottom most layer.
         *
         * <p>No connections between nodes of this type are permitted, except from a node to
         * itself.
         */
        INPUT,

        /**
         * Nodes in the central layers (between inputs and outputs) of the genome are hidden
         * nodes.
         *
         * <p>It's advised to start a topology without any hidden nodes, so that a minimal
         * structure is obtained.
         */
        HIDDEN,

        /**
         * Output nodes contain the result of network's evaluation, corresponding to the top most
         * layer of the genome.
         *
         * <p>No connections between outputs are permitted, except from an output to itself.
         */
        OUTPUT}

    /** Unique identifier in the genome. */
    private final int id;

    /** {@link Type} of this node. */
    private final Type type;

    /** Topological layer, to facilitate feed-forward and overcomplicate crossover. */
    private int layer;

    /** Holds the sum of this node's inputs. */
    private double input;

    /**
     * Constructor with no layer specification.
     *
     * <p>If the {@code type} is {@link Type#INPUT} the layer will be set to 0. If it is an
     * {@link Type#OUTPUT} node, it will be set to 1. If more layers are needed, for example,
     * when creating an initial topology with initial hidden nodes (not advisable, as explained
     * in K.O.Stanley and R.Miikkulainen's paper: Evolving Neural Networks through Augmenting
     * Topologies), they should be added in ascending order, from inputs to outputs.
     *
     * <p>Thus when creating a non-default topology, with hidden nodes, the layer specification
     * is a responsibility of the client. If the convention before described is not respected
     * something might unexpectedly break, because it's way too complex to check for it.
     *
     * @param id unique identifier for this node;
     * @param type of node;
     *
     * @throws IllegalArgumentException if the {@code id} is less than 0;
     */
    public Node (int id, Type type) {
        if (id < 0) {
            throw new IllegalArgumentException("Negative IDs aren't permitted: " + id);
        }
        this.id = id;
        this.type = type;
        if (type == Type.INPUT) {
            layer = 0;
        } else if (type == Type.OUTPUT) {
            layer = 1;
        }
    }

    /**
     * Constructor with layer specification.
     *
     * <p>For an explanation on why you would want to specify the layer of a node, see
     * {@link Node#Node(int, Type)}. If creating a default topology, one without hidden nodes at
     * the start, there's no need to specify the layers.
     *
     * @param id unique identifier for this node;
     * @param type of the node;
     * @param layer topological layer, starting at 0 for the inputs and reaching the highest
     *              value at the output nodes;
     *
     * @throws IllegalArgumentException if the {@code id} is less than 0;
     */
    public Node (int id, Type type, int layer) {
        this(id, type);
        this.layer = layer;
    }

    /**
     * Reset the input of this node to 0.
     */
    public void reset () {
        input = 0;
    }

    /**
     * @param input value to add to the input of this node;
     */
    public void addInput (double input) {
        this.input += input;
    }

    /**
     * Apply the activation function to this node and calculate its output.
     *
     * <p>The {@link ActivationFunction} is a simple function that accepts a double and returns a
     * double. A method reference or a lambda expression will work if you don't want to implement
     * the interface. Some functions are already available in the
     * {@link com.tesladodger.neat.utils.functions} package.
     *
     * @param f activation function;
     *
     * @return value of the function;
     */
    public double getOutput (ActivationFunction f) {
        return f.apply(input);
    }

    /**
     * @return unique identifier (in each genome) of this node;
     */
    public int getId () {
        return id;
    }

    /**
     * @return type of this node;
     */
    public Type getType () {
        return type;
    }

    /**
     * @return layer of this node;
     */
    public int getLayer () {
        return layer;
    }

    /**
     * @param layer set the layer of this node to {@code layer};
     */
    public void setLayer (int layer) {
        this.layer = layer;
    }

    /**
     * Simple method used to increment the layer of this node easily, which could be called a lot.
     */
    public void incrementLayer () {
        layer++;
    }

    @Override
    public Node clone () {
        try {
            return (Node) super.clone();
        } catch (CloneNotSupportedException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * Compare to another Node, dictates order in
     * {@link com.tesladodger.neat.utils.structures.NodeList}.
     *
     * <p>Higher layer means lower result. If the layer is the same, higher id means lower result.
     *
     * @param o node to compare to;
     *
     * @return result of comparison;
     */
    @Override
    public int compareTo (Node o) {
        int result = o.layer - layer;
        if (result == 0) {
            result = o.id - id;
        }
        return result;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node x = (Node) o;
        return getId() == x.getId() && getType() == x.getType() && getLayer() == x.getLayer();
    }

    @Override
    public String toString () {
        return "Node{" +
                "id=" + id +
                ", type=" + type +
                ", layer=" + layer +
                '}';
    }
}
