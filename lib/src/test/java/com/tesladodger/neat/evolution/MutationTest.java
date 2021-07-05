package com.tesladodger.neat.evolution;

import com.tesladodger.neat.Connection;
import com.tesladodger.neat.Genome;
import com.tesladodger.neat.Node;
import com.tesladodger.neat.utils.InnovationHistory;
import com.tesladodger.neat.utils.Parameters;
import com.tesladodger.neat.GenomeBuilder;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


@SuppressWarnings("TextBlockMigration")
public class MutationTest {

    /**
     * Test the method {@link Mutation#mutate(Genome, InnovationHistory, Parameters, Random)} and
     * assert the percentage of mutations.
     */
    @RepeatedTest(5)
    public void mutationTest0 () {
        Parameters params = new Parameters();
        params.connectionWeightsMutationProbability = 0.4;
        params.newNodeMutationProbability = 0.3;
        params.newConnectionMutationProbability = 0.31;
        params.recursiveConnectionProbability = 0.2;

        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.INPUT);
        Node n2 = new Node(2, Node.Type.INPUT);
        Node n3 = new Node(3, Node.Type.OUTPUT, 2);
        Node n4 = new Node(4, Node.Type.OUTPUT, 2);
        Node n5 = new Node(5, Node.Type.HIDDEN, 1);
        Connection c0 = new Connection(0, 0, 3);
        Connection c1 = new Connection(1, 1, 5);
        Connection c2 = new Connection(2, 2, 4);
        Connection c3 = new Connection(3, 5, 3);
        Connection c4 = new Connection(4, 5, 4);
        Genome g = new Genome().addNodes(n0, n1, n2, n3, n4, n5).addConnections(c0, c1, c2, c3, c4);

        // weight mutations
        int weightMutationCount = 0;

        // node mutations
        int nodeMutationCount = 0;
        int nm03 = 0;
        int nm15 = 0;
        int nm24 = 0;
        int nm53 = 0;
        int nm54 = 0;

        // connection mutations
        int connectionMutationCount = 0;

        // progressive
        int cm05 = 0;
        int cm04 = 0;
        int cm13 = 0;
        int cm14 = 0;
        int cm25 = 0;
        int cm23 = 0;

        // recursive
        int cm30 = 0;
        int cm31 = 0;
        int cm32 = 0;
        int cm35 = 0;
        int cm33 = 0;

        int cm40 = 0;
        int cm41 = 0;
        int cm42 = 0;
        int cm45 = 0;
        int cm44 = 0;

        int cm50 = 0;
        int cm51 = 0;
        int cm52 = 0;
        int cm55 = 0;

        int i = 0;
        while (i++ < 1000) {
            InnovationHistory h = new InnovationHistory(5, 4);
            Genome clone = g.clone();
            assertTrue(Mutation.mutate(clone, h, params, ThreadLocalRandom.current()), "" + i);
            if (clone.getNodes().size() == 7) {
                // new node mutation
                Connection c5 = clone.getConnections().asOrderedList().get(5);
                Connection c6 = clone.getConnections().asOrderedList().get(6);
                assertEquals(6, c5.getOutNodeId());
                assertEquals(c5.getOutNodeId(), c6.getInNodeId());
                switch (c5.getInNodeId()) {
                    case 0 -> nm03++;
                    case 1 -> nm15++;
                    case 2 -> nm24++;
                    case 5 -> {
                        if (c6.getOutNodeId() == 3) nm53++;
                        else if (c6.getOutNodeId() == 4) nm54++;
                        else fail("" + c6.getOutNodeId());
                    }
                    default -> fail("In: " + c5.getInNodeId() + " Out: " + c5.getOutNodeId());
                }
                nodeMutationCount++;
            } else if (clone.getConnections().size() == 6) {
                // new connection mutation
                Connection c5 = clone.getConnections().asOrderedList().get(5);
                switch (c5.getInNodeId()) {
                    case 0 -> {
                        if (c5.getOutNodeId() == 5) cm05++;
                        else if (c5.getOutNodeId() == 4) cm04++;
                        else fail("" + c5.getOutNodeId());
                    }
                    case 1 -> {
                        if (c5.getOutNodeId() == 4) cm14++;
                        else if (c5.getOutNodeId() == 3) cm13++;
                        else fail("" + c5.getOutNodeId());
                    }
                    case 2 -> {
                        if (c5.getOutNodeId() == 5) cm25++;
                        else if (c5.getOutNodeId() == 3) cm23++;
                        else fail("" + c5.getOutNodeId());
                    }
                    case 3 -> {
                        switch (c5.getOutNodeId()) {
                            case 0 -> cm30++;
                            case 1 -> cm31++;
                            case 2 -> cm32++;
                            case 5 -> cm35++;
                            case 3 -> cm33++;
                            default -> fail("" + c5.getOutNodeId());
                        }
                    }
                    case 4 -> {
                        switch (c5.getOutNodeId()) {
                            case 0 -> cm40++;
                            case 1 -> cm41++;
                            case 2 -> cm42++;
                            case 5 -> cm45++;
                            case 4 -> cm44++;
                            default -> fail("" + c5.getOutNodeId());
                        }
                    }
                    case 5 -> {
                        switch (c5.getOutNodeId()) {
                            case 0 -> cm50++;
                            case 1 -> cm51++;
                            case 2 -> cm52++;
                            case 5 -> cm55++;
                            default -> fail("" + c5.getOutNodeId());
                        }
                    }
                    default -> fail("In: " + c5.getInNodeId() + " Out: " + c5.getOutNodeId());
                }
                connectionMutationCount++;
            } else {
                weightMutationCount++;
            }
        }

        assertEquals(400, weightMutationCount, 50);
        assertEquals(300, nodeMutationCount, 50);
        assertEquals(300, connectionMutationCount, 50);

        int[] nm = new int[] {nm03, nm15, nm24, nm53, nm54};
        System.out.println("node mutations: " + Arrays.toString(nm));
        int[] cm = new int[] {cm05, cm04, cm13, cm14, cm25, cm23, cm30, cm31, cm32, cm35, cm33,
                cm40, cm41, cm42, cm45, cm44, cm50, cm51, cm52, cm55};
        System.out.println("connection mutations: " + Arrays.toString(cm));
    }

    /**
     * Check whether the genome makes sense.
     *
     * @param g genome to check;
     *
     * @return true if it is, false otherwise;
     */
    public boolean isTopologicallyCorrect (Genome g, InnovationHistory h) {
        List<Connection> cons = g.getConnections().asOrderedList();

        // check repeated connections
        for (int i = 0; i < cons.size()-1; i++) {
            if (cons.get(i).getInnovationNumber() == cons.get(i+1).getInnovationNumber()) {
                return false;
            }
        }

        Node[] nodes = g.getNodes().asArray();
        if (!nodes[nodes.length-1].getType().equals(Node.Type.OUTPUT)) {
            return false;
        }

        for (Connection con : g.getConnections().asOrderedList()) {
            int innov = h.getNewConnectionMutationInnovationNumber(con.getInNodeId(), con.getOutNodeId());
            if (con.getInnovationNumber() != innov) {
                return false;
            }
        }

        return true;
    }

    /**
     * Repeatedly test mutation of a genome and assert its integrity.
     */
    @Test
    public void mutationTest1 () {
        InnovationHistory history = new InnovationHistory();
        Parameters params = new Parameters();
        params.recursiveConnectionProbability = 0;
        Genome genome = new GenomeBuilder(history)
                .setNumberOfNodes(3, 1).build();

        for (int i = 0; i < 1000; i++) {
            Genome clone = genome.clone();
            Mutation.mutate(clone, history, params, ThreadLocalRandom.current());
            assertTrue(isTopologicallyCorrect(clone, history));
            genome = clone;
        }
    }

    @Test
    public void fixLayerIncrementationTest0 () {
        Node n0 = new Node(0, Node.Type.HIDDEN, 0);
        Node n1 = new Node(1, Node.Type.HIDDEN, 0);
        Node n2 = new Node(2, Node.Type.HIDDEN, 1);
        Node n3 = new Node(3, Node.Type.HIDDEN, 1);
        Node n4 = new Node(4, Node.Type.HIDDEN, 2);
        Connection c0 = new Connection(0, 0, 2);
        Connection c1 = new Connection(1, 0, 3);
        Connection c2 = new Connection(2, 1, 2);
        Connection c3 = new Connection(3, 1, 3);
        Connection c4 = new Connection(4, 2, 4);
        Connection c5 = new Connection(5, 3, 4);
        Genome g = new Genome().addNodes(n0, n1, n2, n3, n4).addConnections(c0, c1, c2, c3, c4, c5);

        EvolutionUtils.fixLayerIncrementation(g, n1);
        assertEquals("Genome{\n" +
                "\tnodes=[" +
                "Node{id=0, type=HIDDEN, layer=0}, " +
                "Node{id=1, type=HIDDEN, layer=1}, " +
                "Node{id=2, type=HIDDEN, layer=2}, " +
                "Node{id=3, type=HIDDEN, layer=2}, " +
                "Node{id=4, type=HIDDEN, layer=3}],\n" +
                "\tconnections=[" +
                "Connection{innovNum=0, in=0, out=2, weight=0.0, enabled=true}, " +
                "Connection{innovNum=1, in=0, out=3, weight=0.0, enabled=true}, " +
                "Connection{innovNum=2, in=1, out=2, weight=0.0, enabled=true}, " +
                "Connection{innovNum=3, in=1, out=3, weight=0.0, enabled=true}, " +
                "Connection{innovNum=4, in=2, out=4, weight=0.0, enabled=true}, " +
                "Connection{innovNum=5, in=3, out=4, weight=0.0, enabled=true}]\n" +
                "}", g.toString());
    }

    @Test
    public void fixLayerIncrementationTest1 () {
        Node n0 = new Node(0, Node.Type.HIDDEN, 1);
        Node n1 = new Node(1, Node.Type.HIDDEN, 2);
        Connection c0 = new Connection(0, 0, 1);
        Connection c1 = new Connection(1, 1, 0);
        Genome g = new Genome().addNodes(n0, n1).addConnections(c0, c1);
        EvolutionUtils.fixLayerIncrementation(g, n0);
        assertEquals("Genome{\n" +
                "\tnodes=[" +
                "Node{id=0, type=HIDDEN, layer=2}, " +
                "Node{id=1, type=HIDDEN, layer=3}],\n" +
                "\tconnections=[" +
                "Connection{innovNum=0, in=0, out=1, weight=0.0, enabled=true}, " +
                "Connection{innovNum=1, in=1, out=0, weight=0.0, enabled=true}]\n" +
                "}", g.toString());

        EvolutionUtils.fixLayerIncrementation(g, n1);
        assertEquals("Genome{\n" +
                "\tnodes=[" +
                "Node{id=0, type=HIDDEN, layer=2}, " +
                "Node{id=1, type=HIDDEN, layer=4}],\n" +
                "\tconnections=[" +
                "Connection{innovNum=0, in=0, out=1, weight=0.0, enabled=true}, " +
                "Connection{innovNum=1, in=1, out=0, weight=0.0, enabled=true}]\n" +
                "}", g.toString());
    }
}
