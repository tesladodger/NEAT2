package com.tesladodger.neat;

import com.tesladodger.neat.utils.InnovationHistory;
import com.tesladodger.neat.utils.Parameters;
import com.tesladodger.neat.utils.exceptions.IllegalTopologyException;
import com.tesladodger.neat.utils.structures.ConnectionHashTable;
import com.tesladodger.neat.utils.structures.NodeList;
import com.tesladodger.neat.utils.functions.ActivationFunction;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;


/**
 * Represents the genome topology: a list of {@link Node}s and {@link Connection}s.
 *
 * <p>The nodes have layers, starting at 0 for the input nodes and incrementing up to the outputs.
 * This helps ordering the nodes for feed-forward: the process of propagating the input across
 * the network.
 *
 * <p>After feeding forward, the values of the nodes are cleared. Since this genome supports
 * recursive connections - from a node in a higher layer to a node in a lower one, or from a node
 * to itself - the values of nodes stemming from recursive connections is saved, and will be
 * added in the next feed-forward.
 *
 * <p>The actual topology choices are not a responsibility of this class. For example, using a
 * Bias node is a choice the client has to make, always taking care to set its input to 1.
 *
 * @see Node
 * @see Connection
 * @author tesla
 */
public class Genome implements Cloneable, Comparable<Genome> {

    /* Genes in this genome, organized in their respective data structures. */
    private final NodeList nodes;
    private final ConnectionHashTable connections;

    /* Counters for number of nodes of each kind in this genome */
    private int inputNum;
    private int outputNum;
    private int hiddenNum;

    /** Fitness of this genome, which is the comparative value that influences evolution. */
    private double fitness;

    /**
     * Default constructor.
     */
    public Genome () {
        nodes = new NodeList();
        connections = new ConnectionHashTable(10);
        inputNum = 0;
        outputNum = 0;
        hiddenNum = 0;
        fitness = 0;
    }

    /**
     * @param node to add to this genome;
     */
    public void addNode (Node node) {
        nodes.add(node);
        switch (node.getType()) {
            case INPUT -> inputNum++;
            case OUTPUT -> outputNum++;
            case HIDDEN -> hiddenNum++;
        }
    }

    /**
     * @param nodes to add to this genome;
     *
     * @return this genome, for easy chaining;
     */
    public Genome addNodes (Node... nodes) {
        for (Node n : nodes) {
            addNode(n);
        }
        return this;
    }

    /**
     * @param connection to add to this genome;
     */
    public void addConnection (Connection connection) {
        connections.addConnection(connection);
    }

    /**
     * @param connections to add to this genome;
     *
     * @return this genome, for easy chaining;
     */
    public Genome addConnections (Connection... connections) {
        for (Connection c : connections) {
            addConnection(c);
        }
        return this;
    }

    /**
     * Performs propagation of the inputs through the network.
     *
     * <p>The value of the connections leading to a node is summed, according to the weight of
     * those connections (and only if they are enabled), an activation function is applied to the
     * node, and the resulting value is fed to the nodes said node is connected to.
     *
     * <p>The {@link ActivationFunction} is a simply function that accepts a double and returns a
     * double. A method reference or a lambda expression will work, if you don't want to
     * implement the interface. Some functions are already available in the
     * {@link com.tesladodger.neat.utils.functions} package, which have some parameters you can
     * use to tune the function.
     *
     * <p>After a node's output is fed to all subsequent nodes, the value of that node is set to
     * 0. This means that, if there are recursive connections, their value is preserved until the
     * next call to this method. In other words, progressive connections are calculated in have
     * an effect in a call to this method, recursive connections will have an effect in the next
     * call to this method.
     *
     * @param input array of inputs;
     * @param function activation function;
     *
     * @return array with outputs, ordered by node id;
     * @throws IllegalArgumentException if the length of input array doesn't correspond to the
     * number of input nodes in this genome;
     * @see Genome#calculateRawOutput(double[], ActivationFunction)
     */
    public double[] calculateOutput (double[] input, ActivationFunction function) {
        double[] result = calculateRawOutput(input, function);
        for (int i = 0; i < result.length; i++) {
            result[i] = function.apply(result[i]);
        }
        return result;
    }

    /**
     * Performs propagation of the inputs through the network.
     *
     * <p>Unlike {@link Genome#calculateOutput(double[], ActivationFunction)}, the activation
     * function isn't applied to the output nodes. Thus the raw output values can be passed to a
     * multi-dimensional normalization function, like the
     * {@link com.tesladodger.neat.tools.SoftmaxFunction}.
     *
     * @param input array of inputs;
     * @param function activation function;
     *
     * @return array with outputs, ordered by node id;
     * @throws IllegalArgumentException if the length of the input array doesn't correspond to
     * the number of input nodes in this genome;
     * @see Genome#calculateOutput(double[], ActivationFunction)
     * @since v1.1
     */
    public double[] calculateRawOutput (double[] input, ActivationFunction function) {
        if (input.length != inputNum) {
            throw new IllegalArgumentException("Length of input array [" + input.length + "] does" +
                    " not correspond to number of input nodes [" + inputNum + "].");
        }

        int in = 0;
        int out = 0;
        double[] result = new double[outputNum];
        for (Node node : nodes) {
            switch (node.getType()) {
                case INPUT -> {
                    node.addInput(input[in++]);
                    propagateFromNode(node, function);
                }
                case HIDDEN -> propagateFromNode(node, function);
                case OUTPUT -> {
                    result[out++] = node.getOutput(x -> x);
                    // propagate from the outputs, because there might be backward connections
                    propagateFromNode(node, function);
                }
            }
        }
        return result;
    }

    /**
     * Feed all nodes that have a connection departing from a given node. Also resets the node
     * afterwards.
     *
     * @param node the connections depart from;
     * @param function {@link ActivationFunction};
     */
    private void propagateFromNode (Node node, ActivationFunction function) {
        double output = node.getOutput(function);

        // if the node has a connection to itself, it must be restored after the node is reset
        Connection conToSelf = null;

        for (Connection con : connections.getConnectionsFrom(node.getId())) {
            if (con.isEnabled()) {
                if (con.getInNodeId() == con.getOutNodeId()) {
                    conToSelf = con;
                }
                double input = output * con.getWeight();
                nodes.get(con.getOutNodeId()).addInput(input);
            }
        }

        // reset the node. if there's a connection to itself, save and restore it
        if (conToSelf != null) {
            double input = output * conToSelf.getWeight();
            node.reset();
            node.addInput(input);
        } else {
            node.reset();
        }
    }

    /**
     * Calculate compatibility between two genomes. Used in speciation. The equation is {@code
     * (c1*E + c2*D)/N + c3*W}, where:
     *
     * <p><code>c1</code> - {@link Parameters#excessGenesCompatibilityCoefficient};
     * <p><code>E</code>  - number of excess genes with the other genome;
     * <p><code>c2</code> - {@link Parameters#disjointGenesCompatibilityCoefficient};
     * <p><code>D</code>  - number of disjoint genes with the other genome;
     * <p><code>N</code>  - number of genes in the larger genome, or 1 if both of them have less
     * genes than the value of {@link Parameters#largeGenomeNormalizerThreshold};
     * <p><code>c3</code> - {@link Parameters#averageWeightDifferenceCompatibilityCoefficient};
     * <p><code>W</code>  - average weight difference of matching genes between this and the
     * other genome;
     *
     * @param a first genome;
     * @param b second genome;
     * @param p parameters;
     *
     * @return compatibility value;
     */
    public static float compatibilityBetween (Genome a, Genome b, Parameters p) {
        // Number of excess genes
        float E = excessGenesBetween(a, b);

        // Number of disjoint genes
        float D = disjointGenesBetween(a, b);

        // Average weight difference of matching genes
        float W = averageWeightDifferenceBetween(a, b);

        float N = 1;
        if (a.connections.size() > p.largeGenomeNormalizerThreshold
                && b.connections.size() > p.largeGenomeNormalizerThreshold) {
            N = Math.max(a.connections.size(), b.connections.size());
        }

        float x1 = p.excessGenesCompatibilityCoefficient * E / N;
        float x2 = p.disjointGenesCompatibilityCoefficient * D / N;
        float x3 = p.averageWeightDifferenceCompatibilityCoefficient * W;

        return x1 + x2 + x3;
    }

    /**
     * Count the number of excess genes between two genomes.
     *
     * @param a first genome;
     * @param b second genome;
     *
     * @return number of excess genes;
     */
    public static int excessGenesBetween (Genome a, Genome b) {
        List<Connection> cons1 = a.connections.asOrderedList();
        List<Connection> cons2 = b.connections.asOrderedList();

        // is one of them has no genes, return the number of genes of the other one
        if (cons1.isEmpty() || cons2.isEmpty()) {
            return cons1.size() + cons2.size();
        }

        // if the last genes are equal, there are no excess genes
        ListIterator<Connection> it1 = cons1.listIterator(cons1.size());
        ListIterator<Connection> it2 = cons2.listIterator(cons2.size());
        int innov1 = it1.previous().getInnovationNumber();
        int innov2 = it2.previous().getInnovationNumber();
        if (innov1 == innov2) {
            return 0;
        }

        // Make it1 is the one with excess genes
        if (innov2 > innov1) {
            it1 = it2;
            innov2 = innov1;
        }

        int result = 1;
        while (it1.hasPrevious() && innov2 < it1.previous().getInnovationNumber()) {
            result++;
        }

        return result;
    }

    /**
     * Count the number of disjoint genes between two genomes.
     *
     * @param a first genome;
     * @param b second genome;
     *
     * @return number of disjoint genes;
     */
    public static int disjointGenesBetween (Genome a, Genome b) {
        List<Connection> cons1 = a.connections.asOrderedList();
        List<Connection> cons2 = b.connections.asOrderedList();

        if (cons1.isEmpty() || cons2.isEmpty()) {
            return 0;
        }

        Iterator<Connection> it1 = cons1.iterator();
        Iterator<Connection> it2 = cons2.iterator();

        int result = 0;
        Connection current1 = it1.next();
        Connection current2 = it2.next();
        while (true) {
            if (current1.getInnovationNumber() > current2.getInnovationNumber()) {
                result++;
                if (it2.hasNext()) {
                    current2 = it2.next();
                } else break;
            } else if (current1.getInnovationNumber() < current2.getInnovationNumber()) {
                result++;
                if (it1.hasNext()) {
                    current1 = it1.next();
                } else break;
            } else {
                if (!(it1.hasNext() && it2.hasNext())) break;
                else {
                    current1 = it1.next();
                    current2 = it2.next();
                }
            }
        }
        return result;
    }

    /**
     * Calculate the average weight difference between matching genes of this genome and another.
     *
     * @param a first genome;
     * @param b second genome;
     *
     * @return average weight difference;
     */
    public static float averageWeightDifferenceBetween (Genome a, Genome b) {
        List<Connection> cons1 = a.connections.asOrderedList();
        List<Connection> cons2 = b.connections.asOrderedList();

        if (cons1.isEmpty() || cons2.isEmpty()) {
            return 0;
        }

        Iterator<Connection> it1 = cons1.iterator();
        Iterator<Connection> it2 = cons2.iterator();

        float sum = 0f;
        int count = 0;
        Connection c1 = it1.next();
        Connection c2 = it2.next();
        while (true) {
            if (c1.getInnovationNumber() == c2.getInnovationNumber()) {
                sum += Math.abs(c1.getWeight() - c2.getWeight());
                count++;
                if (it1.hasNext() && it2.hasNext()) {
                    c1 = it1.next();
                    c2 = it2.next();
                } else break;
            } else if (c1.getInnovationNumber() > c2.getInnovationNumber()) {
                if (it2.hasNext()) {
                    c2 = it2.next();
                } else break;
            } else {
                if (it1.hasNext()) {
                    c1 = it1.next();
                } else break;
            }
        }

        return count == 0 ? 0 : sum / count;
    }

    /**
     * Fully connect this genome (kinda). All nodes in each layer will only be connected to all
     * nodes in the immediately subsequent layer, not to all other nodes. Also, no recursive
     * connections will be created.
     *
     * <p>This genome cannot have any connections prior to this method call.
     *
     * @param history innovation history;
     * @param params parameters (used to set the weight of the connections);
     * @param rand random instance, used to set the weights of the new connections;
     *
     * @throws IllegalTopologyException if this genome lacks any inputs or outputs, or if it
     * already has any connection;
     */
    public void fullyConnect (InnovationHistory history, Parameters params, Random rand) {
        if (inputNum == 0 || outputNum == 0) {
            throw new IllegalTopologyException("Attempt at fully connecting a genome without " +
                    "inputs or outputs.");
        } else if (connections.size() != 0) {
            throw new IllegalTopologyException("Attempt at fully connecting a genome with some " +
                    "connections already present.");
        }

        Node[] nA = nodes.asArray();
        int lo = 0;
        int hi = 0;
        while (nA[0].getLayer() == nA[hi].getLayer()) hi++;
        // for every node except outputs
        while (lo < nA.length - outputNum) {
            int l = nA[hi].getLayer();
            int inID = nA[lo].getId();
            int temp = hi;
            for ( ; hi < nA.length && nA[hi].getLayer() == l; hi++) {
                int outID = nA[hi].getId();
                int iNum = history.getNewConnectionMutationInnovationNumber(inID, outID);
                double range = params.weightUpperBound - params.weightLowerBound;
                double weight = rand.nextDouble()
                        * range + params.weightLowerBound;
                Connection c = new Connection(iNum, inID, outID, weight);
                addConnection(c);
            }
            if (++lo != temp) {
                hi = temp;
            }
        }
    }

    /**
     * @return {@link Node}s in this genome;
     */
    public NodeList getNodes () {
        return nodes;
    }

    /**
     * @return {@link Connection}s in this genome;
     */
    public ConnectionHashTable getConnections () {
        return connections;
    }

    /**
     * @return number of {@link Node.Type#INPUT}s in this genome;
     */
    public int numberOfInputs () {
        return inputNum;
    }

    /**
     * @return number of {@link Node.Type#OUTPUT}s in this genome;
     */
    public int numberOfOutputs () {
        return outputNum;
    }

    /**
     * @return number of {@link Node.Type#HIDDEN} nodes in this genome;
     */
    public int numberOfHidden () {
        return hiddenNum;
    }

    /**
     * @return fitness number of this genome.
     *
     * @see Genome#setFitness(double)
     */
    public double getFitness () {
        return fitness;
    }

    /**
     * Set the fitness value of this genome.
     *
     * <p>The fitness is the value by which genomes are compared, which influences the methods
     * related to the evolution of a population. A higher fitness value means this genome is
     * better at solving the problem, therefore has a higher probability of generating offspring.
     *
     * @param fitness value of this genome;
     */
    public void setFitness (double fitness) {
        this.fitness = fitness;
    }

    /**
     * Deep copy of this genome. The returned genome is functionally identical to this one,
     * though completely independent.
     *
     * @return clone of this genome;
     */
    @Override
    public Genome clone () {
        try {
            Genome clone = (Genome) super.clone();

            Field nodesField = Genome.class.getDeclaredField("nodes");
            nodesField.setAccessible(true);
            nodesField.set(clone, new NodeList());
            Field connectionsField = Genome.class.getDeclaredField("connections");
            connectionsField.setAccessible(true);
            connectionsField.set(clone, new ConnectionHashTable(connections.size()));

            clone.inputNum = 0;
            clone.hiddenNum = 0;
            clone.outputNum = 0;

            for (Connection con : connections.asOrderedList()) {
                clone.addConnection(con.clone());
            }

            for (Node n : nodes) {
                clone.addNode(n.clone());
            }

            return clone;
        } catch (CloneNotSupportedException | NoSuchFieldException | IllegalAccessException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genome x = (Genome) o;
        return nodes.equals(x.nodes) && connections.equals(x.connections)
                && inputNum == x.inputNum && hiddenNum == x.hiddenNum && outputNum == x.outputNum;
    }

    @Override
    public int compareTo (Genome o) {
        return Double.compare(fitness, o.fitness);
    }

    @Override
    public String toString () {
        return "Genome{" +
                "\n\tnodes=" + nodes +
                ",\n\tconnections=" + connections +
                "\n}";
    }
}
