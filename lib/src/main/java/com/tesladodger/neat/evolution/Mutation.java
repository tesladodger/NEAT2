package com.tesladodger.neat.evolution;

import com.tesladodger.neat.Connection;
import com.tesladodger.neat.Genome;
import com.tesladodger.neat.utils.Arrays;
import com.tesladodger.neat.utils.InnovationHistory;
import com.tesladodger.neat.Node;
import com.tesladodger.neat.utils.Parameters;
import com.tesladodger.neat.utils.structures.ConnectionHashTable;
import com.tesladodger.neat.utils.structures.NodeList;

import java.util.Random;
import java.util.function.Function;


/**
 * Contains static methods to mutate a {@link Genome}, both structural mutations and alterations of
 * weights.
 *
 * @author tesla
 */
public class Mutation {

    /**
     * Prevent instantiation.
     */
    private Mutation () {}

    /**
     * Mutate a single {@link Genome}, according to the mutation probabilities.
     *
     * <p>Mutations are mutually exclusive: a genome will only be subjected to weight mutation, a
     * new node or a new connection. The mutation type is selected according to
     * {@link Parameters#connectionWeightsMutationProbability},
     * {@link Parameters#newNodeMutationProbability} and
     * {@link Parameters#newConnectionMutationProbability}.
     *
     * @param genome to mutate;
     * @param history innovation history related to the {@code genome};
     * @param p parameters;
     * @param rand random instance, to determine what mutation will be performed;
     *
     * @return true if the genome undergoes any mutation, false otherwise;
     */
    public static boolean mutate (Genome genome, InnovationHistory history, Parameters p, Random rand) {
        double r = rand.nextDouble();
        if (r <= p.connectionWeightsMutationProbability) {
            return mutateWeights(genome, history, p, rand);
        } else if (r <= p.connectionWeightsMutationProbability + p.newNodeMutationProbability) {
            return addNodeMutation(genome, history, rand);
        } else if (r <= p.connectionWeightsMutationProbability + p.newNodeMutationProbability +
                p.newConnectionMutationProbability) {
            return addConnectionMutation(genome, history, p, rand);
        }
        return false;
    }

    /**
     * Insert a new {@link Node} in the genome, separating a random {@link Connection}.
     *
     * <p>A recursive connection (one back to the same node or one to a one in an inferior layer)
     * is never separated by this method, for the single reason that it would make layer
     * correction and crossover too complex and error prone for me to deal with. Maybe later I'll
     * give it a try (probably not).
     *
     * <p>This method will only fail to add a new node when the genome lacks any connection, or
     * only has recursive connections (which are never selected to be broken).
     *
     * @param genome to mutate;
     * @param history of the genome;
     * @param rand random instance, used to choose the connection;
     *
     * @return true if a new node was inserted, false otherwise;
     */
    public static boolean addNodeMutation (Genome genome, InnovationHistory history, Random rand) {
        ConnectionHashTable connections = genome.getConnections();
        if (connections.isEmpty()) {
            return false;
        }

        Connection[] conArray = connections.asArray();
        // shuffle the array
        Arrays.shuffle(conArray, 0, conArray.length, rand);

        for (Connection con : conArray) {
            // if the connection is disabled, continue
            if (!con.isEnabled()) {
                continue;
            }

            int inLayer = genome.getNodes().get(con.getInNodeId()).getLayer();
            int outLayer = genome.getNodes().get(con.getOutNodeId()).getLayer();

            // if the connection is not recursive, proceed with the mutation and return
            if (inLayer < outLayer) {
                addNodeMutation(genome, con, history);
                return true;
            }
        }
        // no connection was found, return false
        return false;
    }

    /**
     * Insert a new {@link Node} in the {@link Genome}, separating the given {@link Connection}.
     *
     * <p><strong> This will fail if the provided connection is recursive. In normal operation, a
     * recursive connection will never be broken.</strong>
     *
     * @param genome to mutate;
     * @param connection that will be separated by the new node;
     * @param history innovation history of the genome;
     *
     * @throws UnsupportedOperationException if {@code connection} is recursive (the layer of the
     * in-node is greater or equal to the layer of the out-node);
     */
    public static void addNodeMutation (Genome genome, Connection connection,
                                        InnovationHistory history) {
        ConnectionHashTable connections = genome.getConnections();
        NodeList nodes = genome.getNodes();
        Node inNode = nodes.get(connection.getInNodeId());
        Node outNode = nodes.get(connection.getOutNodeId());

        if (inNode.getLayer() >= outNode.getLayer()) {
            throw new UnsupportedOperationException("This method doesn't support breaking up " +
                    "recursive connections.");
        }

        // Get the new node id
        int nodeId = history.getNewNodeMutationId(connection.getInnovationNumber());
        // Create the new node. It's layer should be one above the in-node's layer.
        Node node = new Node(nodeId, Node.Type.HIDDEN, inNode.getLayer()+1);
        // New node and outNode cannot have the same layer. Increment every node's layer that is
        // affected by this new connection if that is the case:
        if (node.getLayer() == outNode.getLayer()) {
            EvolutionUtils.fixLayerIncrementation(genome, outNode);
            nodes.sort();
        }
        genome.addNode(node);

        // Disable the previous connection
        connection.disable();

        // Create a new connection from the in-node to the new node
        int con1InnovationNumber =
                history.getNewConnectionMutationInnovationNumber(inNode.getId(), nodeId);
        Connection con1 = new Connection(con1InnovationNumber, inNode.getId(), node.getId());
        con1.setWeight(1.0);
        connections.addConnection(con1);

        // Create a new connection from the new node to the out-node
        int con2InnovationNumber =
                history.getNewConnectionMutationInnovationNumber(nodeId, outNode.getId());
        Connection con2 = new Connection(con2InnovationNumber, node.getId(), outNode.getId());
        con2.setWeight(connection.getWeight());
        connections.addConnection(con2);
    }

    /**
     * Add a connection between two previously unconnected nodes.
     *
     * <p>There's a chance a recursive connection (to a node in a lower layer or to the same
     * node) is created, given by {@link Parameters#recursiveConnectionProbability}. If the value
     * is zero, no recursive connections will be created.
     *
     * <p>No connections between inputs (input to input) or between outputs (output to output) are
     * ever created, except from a node to itself.
     *
     * <p>Inputs are never connected to themselves by this method, outputs can be.
     *
     * <p>This method will test all possible connections in a random order. If the genome is
     * fully connected (all possible connections are already present), this method will not alter
     * the genome and will return {@code false}.
     *
     * @param genome to mutate;
     * @param history innovation history of the genome;
     * @param p parameters;
     * @param rand random instance, too choose the nodes to connect and set the weight of the new
     *            connection;
     *
     * @return true if a connection was created, false otherwise
     */
    public static boolean addConnectionMutation (Genome genome, InnovationHistory history,
                                                 Parameters p, Random rand) {
        boolean recursive = rand.nextFloat() < p.recursiveConnectionProbability;
        int inputNum = genome.numberOfInputs();
        int outputNum = genome.numberOfOutputs();
        Node[] nodes = genome.getNodes().asArray();

        // create a new array with only the nodes connection can be created from
        Node[] properNodes;
        if (recursive) {
            properNodes = new Node[nodes.length - inputNum];
            System.arraycopy(nodes, inputNum, properNodes, 0, properNodes.length);
        } else {
            properNodes = new Node[nodes.length - outputNum];
            System.arraycopy(nodes, 0, properNodes, 0, properNodes.length);
        }

        // shuffle that new array
        Arrays.shuffle(properNodes, 0, properNodes.length, rand);
        // shuffle the nodes array
        Arrays.shuffle(nodes, 0, nodes.length, rand);

        // loop the proper nodes array: if a connection can be created between a node and another
        // present in the nodes array, create it and return
        for (Node in : properNodes) {
            for (Node out : nodes) {

                // if both nodes are inputs or outputs
                if ((in.getType().equals(Node.Type.INPUT) && out.getType().equals(Node.Type.INPUT)) ||
                        (in.getType().equals(Node.Type.OUTPUT) && out.getType().equals(Node.Type.OUTPUT))) {
                    // if the type is not-recursive, continue: no inputs or outputs can be
                    // connected to other inputs or outputs;
                    // if it is recursive, in and out should be the same node: a recursive
                    // connection for inputs and outputs should only be to the same node;
                    if (!recursive || in != out) {
                        continue;
                    }
                }

                if (recursive) {
                    // if the proposed order is progressive, continue
                    if (in.getLayer() < out.getLayer() ||
                            (in != out && in.getLayer() == out.getLayer())) {
                        continue;
                    }
                } else {
                    // if proposed order is recursive, continue
                    if (in == out || in.getLayer() > out.getLayer()) {
                        continue;
                    }
                }

                // if the connection doesn't exist, call the method and return
                if (!genome.getConnections().containsConnection(in.getId(), out.getId())) {
                    addConnectionMutation(genome, in.getId(), out.getId(), history, p, rand);
                    return true;
                }
            }
        }
        // all possible connections have been explored and rejected, return false;
        return false;
    }

    /**
     * Add a new connection, between two nodes or recursively to the same node, with a random
     * weight between -10 and 10.
     *
     * <p>If the nodes are in the same layer, this method assumes it isn't a recursive connection
     * and the out-node's layer will be increased (and that of all affected nodes). Otherwise,
     * nothing in the topology will change apart from the new connection.
     *
     * @param genome  to mutate;
     * @param inNodeId id of the to-be input node;
     * @param outNodeId id of the to-be output node;
     * @param history innovation history of the genome;
     * @param p parameters;
     * @param rand random instance, to set the weight of the new connection;
     */
    public static void addConnectionMutation (Genome genome, int inNodeId, int outNodeId,
                                              InnovationHistory history, Parameters p,
                                              Random rand) {
        int innovNum = history.getNewConnectionMutationInnovationNumber(inNodeId, outNodeId);
        double range = p.weightUpperBound - p.weightLowerBound;
        double weight = rand.nextDouble()
                * range + p.weightLowerBound;
        Connection con = new Connection(innovNum, inNodeId, outNodeId, weight);
        if (inNodeId != outNodeId) {
            Node inNode = genome.getNodes().get(inNodeId);
            Node outNode = genome.getNodes().get(outNodeId);
            if (inNode.getLayer() == outNode.getLayer()) {
                EvolutionUtils.fixLayerIncrementation(genome, outNode);
            }
        }
        genome.getNodes().sort();
        genome.addConnection(con);
    }

    /**
     * Mutate all weights of a {@link Genome}'s connections, according to the weight mutation
     * probabilities.
     *
     * <p>A connection will be assigned a new random value according to
     * {@link Parameters#newRandomWeightValueProbability}. Otherwise, its weight will be normally
     * perturbed. The standard deviation of the perturbation is a parameter which can depend on
     * the age of the connection (how many generations it has existed for).
     *
     * @param genome to mutate;
     * @param history innovation history;
     * @param p parameters;
     * @param rand random instance;
     *
     * @return {@code true} if the weights of the genome have been mutated, {@code false} if
     * there are no connections to mutate;
     * @see Parameters#weightMutationPower
     * @see Parameters#mutateRecentGenesBias
     * @see Parameters#mutateRecentGenesAgeCutoff
     * @see Parameters#mutateRecentGenesSizeThreshold
     */
    public static boolean mutateWeights (Genome genome, InnovationHistory history, Parameters p,
                                         Random rand) {
        if (genome.getConnections().isEmpty()) {
            return false;
        }

        Function<Integer, Double> powerFunction = EvolutionUtils.calculateMutationPowerFunction(p,
                genome.getConnections().size());

        for (Connection con : genome.getConnections().asArray()) {
            double r = rand.nextDouble();
            if (r <= p.newRandomWeightValueProbability) {
                double range = p.weightUpperBound - p.weightLowerBound;
                con.setWeight(rand.nextDouble()
                        * range + p.weightLowerBound);
            } else {
                double normalOffset = rand.nextGaussian();
                int age = history.getConnectionAge(con.getInNodeId(), con.getOutNodeId());
                double power = powerFunction.apply(age);
                con.setWeight(con.getWeight() + normalOffset * power);
            }
        }
        return true;
    }
}
