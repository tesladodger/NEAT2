package com.tesladodger.neat;

import com.tesladodger.neat.utils.InnovationHistory;
import com.tesladodger.neat.utils.Parameters;
import com.tesladodger.neat.utils.exceptions.IllegalTopologyException;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Creates a {@link com.tesladodger.neat.Genome}, according to user provided requirements, and in
 * accordance to the requirements of the rest of the program.
 *
 * <p>It's possible to generate multiple different genomes, since the weights (if you choose to
 * generate a genome with connections) are randomly assigned. If you want a new genome with a
 * different topology simply change settings between any new call to
 * {@link GenomeBuilder#build()}. If settings aren't changed, all newly created genomes will
 * have the same topology, only with different weights.
 *
 * <p>You are advised to start minimally, with only input and output nodes, fully connected.
 *
 * @author tesla
 * @version 1.0
 */
public class GenomeBuilder {

    private final InnovationHistory history;

    private final Parameters parameters;

    /** Number of inputs and outputs. */
    private int inNodes, outNodes;

    /** Each index corresponds to a hidden layer, and the value is the number of nodes. */
    private int[] layers;

    /** Create a fully connected genome or just the nodes. */
    private boolean fullyConnected;

    /**
     * Constructs a genome builder.
     *
     * <p>The provided {@link InnovationHistory} should be empty, and its reference should be
     * kept, since it will be needed for mutation.
     *
     * <p>The used {@link Parameters} are {@link Parameters#weightLowerBound} and
     * {@link Parameters#weightUpperBound}, to generate the genome's connections.
     *
     * @param history empty innovation history;
     * @param parameters (weight bounds are used to generate the genome's connections);
     *
     * @throws IllegalArgumentException if {@code history} is {@code null};
     * @see InnovationHistory
     * @see Parameters
     */
    public GenomeBuilder (final InnovationHistory history, final Parameters parameters) {
        if (history == null) {
            throw new IllegalArgumentException("Argument 'history' cannot be null.");
        }
        this.history = history;
        this.parameters = parameters == null ? new Parameters() : parameters;
        fullyConnected = true;
        layers = new int[0];
    }

    /**
     * Constructs a genome builder with default {@link Parameters}.
     *
     * <p>The provided {@link InnovationHistory} should be empty, and its reference should be
     * kept, since it will be needed for mutation.
     *
     * @param history empty innovation history;
     *
     * @see InnovationHistory
     */
    public GenomeBuilder (final InnovationHistory history) {
        this(history, new Parameters());
    }

    /**
     * Generate a {@link Genome} with the provided specifications.
     *
     * <p>{@link ThreadLocalRandom} is used to set random weight values.
     *
     * <p>This method can be called multiple times, and the parameters can be changed between
     * each call.
     *
     * @return resulting genome;
     */
    public Genome build () {
        return build(ThreadLocalRandom.current());
    }

    /**
     * Generate a {@link Genome} with the provided specifications.
     *
     * <p>The random instance is used to generate random weights for the resulting genome's
     * connections. Calling this method with the same <i>seed</i> guarantees equal genomes.
     *
     * <p>This method can be called multiple times, and the parameters can be changed between
     * each call.
     *
     * @param rand random instance;
     *
     * @return resulting genome;
     */
    public Genome build (Random rand) {
        Genome genome = new Genome();

        // Create inputs
        for (int i = 0; i < inNodes; i++) {
            genome.addNode(new Node(i, Node.Type.INPUT, 0));
        }
        // Create outputs
        int l = layers.length + 1;
        for (int i = inNodes; i < inNodes + outNodes; i++) {
            genome.addNode(new Node(i, Node.Type.OUTPUT, l));
        }
        // Create hidden nodes
        int id = inNodes + outNodes;
        for (int i = 0; i < layers.length; i++) {
            for (int j = 0; j < layers[i]; j++) {
                genome.addNode(new Node(id++, Node.Type.HIDDEN, i+1));
            }
        }
        history.setInitialHighestNodeId(--id);

        // Connect starting nodes
        if (fullyConnected) {
            genome.fullyConnect(history, parameters, rand);
        }
        return genome;
    }

    /**
     * Set the number of input and output nodes of the desired genome.
     *
     * <p>Note that the presence of a bias node is entirely the responsibility of the user.
     *
     * <p>If you need a starting topology with hidden layers see
     * {@link GenomeBuilder#setHiddenLayers(int...)}.
     *
     * @param inNodes number of input nodes;
     * @param outNodes number of output nodes;
     *
     * @return this creator, for easy chaining;
     * @throws IllegalTopologyException if the number of inputs or outputs is less than 1;
     */
    public GenomeBuilder setNumberOfNodes (int inNodes, int outNodes) {
        if (inNodes <= 0) {
            throw new IllegalTopologyException("Number of input nodes must be positive: " + inNodes);
        } else if (outNodes <= 0) {
            throw new IllegalTopologyException("Number of output nodes must be positive: " + outNodes);
        }
        this.inNodes = inNodes;
        this.outNodes = outNodes;
        return this;
    }

    /**
     * Set the number of hidden nodes. Each provided argument represents the number of hidden
     * nodes in the layer. The layers must be ordered from just after the input layer to just
     * before the output layer.
     *
     * @param layers array containing the number of nodes in each desired layer;
     *
     * @return this creator, for easy chaining;
     * @throws IllegalTopologyException if the number of nodes in a layer is less than 1;
     */
    public GenomeBuilder setHiddenLayers (int... layers) {
        for (int i = 0; i < layers.length; i++) {
            if (layers[i] <= 0) {
                throw new IllegalTopologyException("Number of nodes in layers must be positive: " +
                        "layer " + (i+1) + "; value " + layers[i]);
            }
        }
        this.layers = layers;
        return this;
    }

    /**
     * Specify if the created {@link Genome} should be have all initial nodes connected between
     * adjacent layers.
     * Nodes in one layer are only connected to the immediately subsequent layer, not to all layers.
     *
     * @param fullyConnected (default = true);
     *
     * @return this creator, for easy chaining;
     */
    public GenomeBuilder setFullyConnected (boolean fullyConnected) {
        this.fullyConnected = fullyConnected;
        return this;
    }
}
