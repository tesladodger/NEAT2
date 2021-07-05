package com.tesladodger.neat.evolution;

import com.tesladodger.neat.Connection;
import com.tesladodger.neat.Genome;
import com.tesladodger.neat.Node;
import com.tesladodger.neat.utils.structures.NodeList;


class EvolutionUtils {

    private EvolutionUtils () {}

    /**
     * Recursively searches for connections where the outNode has the same layer as the inNode
     * and increments it.
     *
     * @param genome to fix;
     * @param node currently offending node;
     */
    static void fixLayerIncrementation (Genome genome, Node node) {
        if (node.getType().equals(Node.Type.OUTPUT)) {
            genome.getNodes().getOutputs()
                    .forEach(Node::incrementLayer);
            return;
        }

        node.incrementLayer();

        // Get the connections departing from the node
        NodeList nodes = genome.getNodes();
        for (Connection con : genome.getConnections().getConnectionsFrom(node.getId())) {

            // when this method is called from crossover there's a chance the out-node isn't yet
            // present in the genome
            if (nodes.containsId(con.getOutNodeId())) {
                Node inNode = nodes.get(con.getInNodeId());
                Node outNode = nodes.get(con.getOutNodeId());

                // if a connection was affected by this layer incrementation (both nodes ended up
                // in the same layer as consequence), call this function on its outNode
                if (inNode.getId() != outNode.getId() && inNode.getLayer() == outNode.getLayer()) {
                    fixLayerIncrementation(genome, outNode);
                }
            }
        }
    }
}
