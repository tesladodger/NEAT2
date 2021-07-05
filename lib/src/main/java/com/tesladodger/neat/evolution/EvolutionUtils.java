package com.tesladodger.neat.evolution;

import com.tesladodger.neat.Connection;
import com.tesladodger.neat.Genome;
import com.tesladodger.neat.Node;
import com.tesladodger.neat.utils.Parameters;
import com.tesladodger.neat.utils.structures.NodeList;

import java.util.function.Function;


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

    /**
     * The power function differentiates mutation power given the connections age (how many
     * generations it has existed for).
     *
     * <p>The input is the age of the connection and the output is the mutation power.
     *
     * @param p parameters;
     * @param genomeSize number of connections in the genome;
     *
     * @return function;
     * @see Parameters#weightMutationPower
     * @see Parameters#mutateRecentGenesBias
     * @see Parameters#mutateRecentGenesAgeCutoff
     * @see Parameters#mutateRecentGenesSizeThreshold
     * @since v1.1
     */
    protected static Function<Integer, Double> calculateMutationPowerFunction (Parameters p,
                                                                      double genomeSize) {
        Function<Integer, Double> result;
        double cutoff = p.mutateRecentGenesAgeCutoff, bias = p.mutateRecentGenesBias, power = p.weightMutationPower;
        if (p.mutateRecentGenesAgeCutoff <= 0 ||
                p.mutateRecentGenesBias == 0 ||
                genomeSize < p.mutateRecentGenesSizeThreshold) {
            result = (x) -> p.weightMutationPower;
        } else {
            double a = bias / Math.pow(cutoff, 2);
            result = (x) -> (x < cutoff) ?
                    a * Math.pow(x - cutoff, 2) + power:
                    power;
        }
        return result;
    }
}
