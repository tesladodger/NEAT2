package com.tesladodger.neat;

import com.tesladodger.neat.utils.InnovationHistory;
import com.tesladodger.neat.utils.functions.ActivationFunction;


/**
 * Represents a connection between two {@link Node}s, from an in-node to an out-node. Has a
 * weight, which represents the strength of the connection.
 *
 * @author tesla
 * @version 1.0
 */
public class Connection implements Cloneable, Comparable<Connection> {

    /**
     * Historical marking to facilitate crossover.
     *
     * @see InnovationHistory
     */
    private final int innovationNumber;

    /** Id of the input node. */
    private final int inNodeId;

    /** Id of the output node. */
    private final int outNodeId;

    /** Strength of the connection. */
    private double weight;

    /**
     * Whether or not this connection is enabled. A disabled connection doesn't send a value to
     * the out-node.
     */
    private boolean enabled;

    /**
     * Construct a connection with 0.0 weight.
     *
     * @param innovationNumber see {@link InnovationHistory};
     * @param inNodeId {@link Node#getId()} of the input node;
     * @param outNodeId {@link Node#getId()} of the output node;
     */
    public Connection (int innovationNumber, int inNodeId, int outNodeId) {
        this(innovationNumber, inNodeId, outNodeId, 0, true);
    }

    /**
     * Constructor for a connection.
     *
     * @param innovationNumber see {@link InnovationHistory};
     * @param inNodeId {@link Node#getId()} of the input node;
     * @param outNodeId {@link Node#getId()} of the input node;
     * @param weight of the new connection;
     */
    public Connection (int innovationNumber, int inNodeId, int outNodeId, double weight) {
        this(innovationNumber, inNodeId, outNodeId, weight, true);
    }

    /**
     * Constructor for a connection.
     *
     * <p>A disabled connection doesn't count in the calculation of the output of a genome.
     *
     * @param innovationNumber see {@link InnovationHistory};
     * @param inNodeId {@link Node#getId()} of the input node;
     * @param outNodeId {@link Node#getId()} of the input node;
     * @param weight weight of this connection;
     * @param enabled whether this connection is expressed in the genome or not;
     */
    public Connection (int innovationNumber, int inNodeId, int outNodeId, double weight,
                       boolean enabled) {
        this.innovationNumber = innovationNumber;
        this.inNodeId = inNodeId;
        this.outNodeId = outNodeId;
        this.weight = weight;
        this.enabled = enabled;
    }

    /**
     * @return innovation number of this connection;
     * @see InnovationHistory
     * @see com.tesladodger.neat.evolution.Crossover
     */
    public int getInnovationNumber () {
        return innovationNumber;
    }

    /**
     * @return id of the input node of this connection;
     */
    public int getInNodeId () {
        return inNodeId;
    }

    /**
     * @return id of the output node of this connection;
     */
    public int getOutNodeId () {
        return outNodeId;
    }

    /**
     * @return weight value of this connection;
     * @see Genome#calculateOutput(double[], ActivationFunction)
     */
    public double getWeight () {
        return weight;
    }

    /**
     * @param weight set the weight of this connection to {@code weight};
     */
    public void setWeight (double weight) {
        this.weight = weight;
    }

    /**
     * Set this connection as enabled.
     *
     * @see Genome#calculateOutput(double[], ActivationFunction)
     */
    public void enable () {
        enabled = true;
    }

    /**
     * Set this connection as disabled.
     *
     * @see Genome#calculateOutput(double[], ActivationFunction)
     */
    public void disable () {
        enabled = false;
    }

    /**
     * @return true if this connection is enabled, false otherwise;
     */
    public boolean isEnabled () {
        return enabled;
    }

    @Override
    public Connection clone () {
        try {
            return (Connection) super.clone();
        } catch (CloneNotSupportedException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    @Override
    public int compareTo (Connection o) {
        return o.innovationNumber - innovationNumber;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection x = (Connection) o;
        return innovationNumber == x.innovationNumber &&
                inNodeId == x.inNodeId &&
                outNodeId == x.outNodeId &&
                weight == x.weight &&
                enabled == x.enabled;
    }

    @Override
    public String toString () {
        return "Connection{" +
                "innovNum=" + innovationNumber +
                ", in=" + inNodeId +
                ", out=" + outNodeId +
                ", weight=" + weight +
                ", enabled=" + enabled +
                '}';
    }
}
