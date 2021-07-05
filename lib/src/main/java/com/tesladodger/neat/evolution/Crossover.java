package com.tesladodger.neat.evolution;

import com.tesladodger.neat.Connection;
import com.tesladodger.neat.Genome;
import com.tesladodger.neat.Node;
import com.tesladodger.neat.utils.Parameters;
import com.tesladodger.neat.utils.structures.ConnectionHashTable;
import com.tesladodger.neat.utils.structures.NodeList;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;


/**
 * Contains the method to apply crossover between two genomes.
 *
 * <p>Crossover consists in combining the genes ({@link Connection}) of two genomes. As described
 * in the paper, genes are aligned according to innovation number, allowing similar structures to
 * crossover without loosing information.
 *
 * @author tesla
 * @version 1.0
 */
public class Crossover {

    /**
     * Prevent instantiation.
     */
    private Crossover () {}

    /**
     * Combine two {@link Genome}s to return a child.
     *
     * @param parent1 first parent;
     * @param parent2 second parent;
     * @param p parameters;
     * @param rand random instance;
     *
     * @return child of parent1 and parent2;
     */
    public static Genome mate (Genome parent1, Genome parent2, Parameters p, Random rand) {
        Genome child = new Genome();

        int fittest = compareFitness(parent1, parent2, p);
        // parent2 is fittest, swap them
        if (fittest == 1) {
            Genome temp = parent1;
            parent1 = parent2;
            parent2 = temp;
        }

        List<Connection> p1Cons = parent1.getConnections().asOrderedList();
        List<Connection> p2Cons = parent2.getConnections().asOrderedList();
        ListIterator<Connection> it1 = p1Cons.listIterator();
        ListIterator<Connection> it2 = p2Cons.listIterator();

        while (it1.hasNext() && it2.hasNext()) {
            Connection c1 = it1.next();
            Connection c2 = it2.next();
            if (c1.getInnovationNumber() == c2.getInnovationNumber()) {

                // Matching Gene Inheritance
                float r = rand.nextFloat();
                boolean chance = r < .5 + (fittest == 0 ? 0 : p.fittestParentBias);
                Connection con = chance ? c1.clone() : c2.clone();
                if (!c1.isEnabled() && !c2.isEnabled()) {
                    if (rand.nextFloat() < p.reEnableGeneProbability) {
                        con.enable();
                    }
                } else if (!c1.isEnabled() || !c2.isEnabled()) {
                    con.enable();
                    if (rand.nextFloat() < p.disableGeneProbability) {
                        con.disable();
                    }
                }
                child.addConnection(con);
            } else {

                // Disjoint Gene Inheritance
                Connection disExcCon = c1.getInnovationNumber() < c2.getInnovationNumber() ?
                        c1 : c2;
                // If both genomes have the same fitness, disjoint and excess genes are inherited
                // from both parents. Otherwise, genes are only inherited from the fittest, which
                // is parent1
                if (disExcCon == c1) {
                    child.addConnection(disExcCon.clone());
                    it2.previous();
                } else {
                    if (fittest == 0) {
                        child.addConnection(disExcCon.clone());
                    }
                    it1.previous();
                }
            }
        }

        // Excess genes are inherited from parent1, which is the fittest or at least has the same
        // fitness as parent2
        it1.forEachRemaining(next -> child.addConnection(next.clone()));
        // if the fitness is the same, inherit from parent2 as well
        if (fittest == 0) {
            it2.forEachRemaining(next -> child.addConnection(next.clone()));
        }

        addNodesToChild(child, parent1);
        return child;
    }

    /**
     * @param child to add nodes to;
     * @param parent either parent;
     */
    static void addNodesToChild (Genome child, Genome parent) {
        // Add the inputs and outputs, which are always the same
        int inputs = parent.numberOfInputs();
        Iterator<Node> parentNodeIt = parent.getNodes().iterator();
        while (inputs-- > 0) {
            Node inputNodeClone = parentNodeIt.next().clone();
            child.addNode(inputNodeClone);
        }
        for (Node output : parent.getNodes().getOutputs()) {
            child.addNode(output.clone());
        }
        // Propagate from the inputs, fixing the layers
        Node[] childNodes = child.getNodes().asArray();
        for (Node n : childNodes) {
            addNodesFromNode(child, n);
        }
        child.getNodes().sort();
    }

    /**
     * Recursive method to add all the out-nodes of the connections of the given node, if they
     * are not present already. This is depth first: when a new node is added, this method is
     * called on it. It's the only way to make sure the layers of the nodes of the child are
     * correctly set, since there's a chance they are wrong if merely inherited.
     *
     * @param child to add nodes to;
     * @param node current node;
     */
    private static void addNodesFromNode (Genome child, Node node) {
        ConnectionHashTable childCons = child.getConnections();
        NodeList childNodes = child.getNodes();
        // for every connection from the given node
        for (Connection con : childCons.getConnectionsFrom(node.getId())) {

            // if the out-node isn't already present in the child
            if (!childNodes.containsId(con.getOutNodeId())) {

                // create new node
                Node newNode = new Node(con.getOutNodeId(), Node.Type.HIDDEN, node.getLayer());
                newNode.incrementLayer();

                // fix subsequent layers
                for (Connection conFromNewNode : childCons.getConnectionsFrom(newNode.getId())) {
                    // for every connection from the newly added node, if the connection's
                    // out-node has already been added
                    if (childNodes.containsId(conFromNewNode.getOutNodeId())) {
                        Node outNode = childNodes.get(conFromNewNode.getOutNodeId());
                        // and there's a layer conflict
                        // (note: there's no need to check for recursive connections, this is
                        // only reached if the outNode isn't already present in the genome, which
                        // is never true in the case of recursive connections.
                        if (outNode.getLayer() == newNode.getLayer()) {
                            // solve it
                            EvolutionUtils.fixLayerIncrementation(child, outNode);
                        }
                    }
                }

                // add the new node (only after fixing layers to make sure order is correct
                child.addNode(newNode);
                // call this function on the new node
                addNodesFromNode(child, newNode);
            }
        }
    }

    /**
     * Compare 2 genomes according to their fitness.
     *
     * @param g1 first genome;
     * @param g2 second genome;
     * @param p parameters;
     *
     * @return -1 if first genome is fittest, 0 if same fitness, 1 if second is fittest;
     */
    static int compareFitness (Genome g1, Genome g2, Parameters p) {
        double f1 = g1.getFitness();
        double f2 = g2.getFitness();
        if (f1 > f2 + p.fitnessTolerance) return -1;
        if (f2 > f1 + p.fitnessTolerance) return 1;
        return 0;
    }
}
