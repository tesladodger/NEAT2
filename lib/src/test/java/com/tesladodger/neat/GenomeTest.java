package com.tesladodger.neat;

import com.tesladodger.neat.utils.InnovationHistory;
import com.tesladodger.neat.evolution.Mutation;
import com.tesladodger.neat.utils.Parameters;
import com.tesladodger.neat.utils.exceptions.IllegalTopologyException;
import com.tesladodger.neat.utils.functions.SigmoidActivationFunction;
import com.tesladodger.neat.utils.functions.StepActivationFunction;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class GenomeTest {

    @Test
    public void fullyConnectTest0 () {
        Genome g = new Genome();

        assertThrows(IllegalTopologyException.class,
                () -> g.fullyConnect(null, new Parameters(), ThreadLocalRandom.current()));

        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.OUTPUT);
        g.addNodes(n0, n1);
        
        InnovationHistory h = new InnovationHistory();
        g.fullyConnect(h, new Parameters(), ThreadLocalRandom.current());

        assertEquals(1, g.getConnections().size());
        Connection con = g.getConnections().asOrderedList().get(0);
        assertEquals(0, con.getInnovationNumber());
        assertEquals(0, con.getInNodeId());
        assertEquals(1, con.getOutNodeId());
    }

    @Test
    public void fullyConnectTest1 () {
        Genome g = new Genome();
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.INPUT);
        Node n2 = new Node(2, Node.Type.INPUT);
        Node n3 = new Node(3, Node.Type.OUTPUT);
        Node n4 = new Node(4, Node.Type.OUTPUT);

        InnovationHistory h = new InnovationHistory();
        // ensure predictable innovation numbers
        h.getNewConnectionMutationInnovationNumber(0, 3);
        h.getNewConnectionMutationInnovationNumber(0, 4);
        h.getNewConnectionMutationInnovationNumber(1, 3);
        h.getNewConnectionMutationInnovationNumber(1, 4);
        h.getNewConnectionMutationInnovationNumber(2, 3);
        h.getNewConnectionMutationInnovationNumber(2, 4);

        g.addNodes(n0, n1, n2, n3, n4);
        g.fullyConnect(h, new Parameters(), ThreadLocalRandom.current());

        List<Connection> cons = g.getConnections().asOrderedList();
        assertEquals(6, cons.size());

        Connection c = cons.get(0);
        assertEquals(0, c.getInnovationNumber());
        assertEquals(0, c.getInNodeId());
        assertEquals(3, c.getOutNodeId());

        c = cons.get(1);
        assertEquals(1, c.getInnovationNumber());
        assertEquals(0, c.getInNodeId());
        assertEquals(4, c.getOutNodeId());

        c = cons.get(2);
        assertEquals(2, c.getInnovationNumber());
        assertEquals(1, c.getInNodeId());
        assertEquals(3, c.getOutNodeId());

        c = cons.get(3);
        assertEquals(3, c.getInnovationNumber());
        assertEquals(1, c.getInNodeId());
        assertEquals(4, c.getOutNodeId());

        c = cons.get(4);
        assertEquals(4, c.getInnovationNumber());
        assertEquals(2, c.getInNodeId());
        assertEquals(3, c.getOutNodeId());

        c = cons.get(5);
        assertEquals(5, c.getInnovationNumber());
        assertEquals(2, c.getInNodeId());
        assertEquals(4, c.getOutNodeId());
    }

    @Test
    public void fullyConnectTest2 () {
        Genome g = new Genome();
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.INPUT);
        Node n2 = new Node(2, Node.Type.INPUT);
        Node n3 = new Node(3, Node.Type.HIDDEN, 1);
        Node n4 = new Node(4, Node.Type.HIDDEN, 1);
        Node n5 = new Node(5, Node.Type.HIDDEN, 2);
        Node n6 = new Node(6, Node.Type.OUTPUT, 3);
        Node n7 = new Node(7, Node.Type.OUTPUT, 3);

        // ensure predictable innovation numbers
        InnovationHistory h = new InnovationHistory();
        h.getNewConnectionMutationInnovationNumber(0, 3);
        h.getNewConnectionMutationInnovationNumber(0, 4);
        h.getNewConnectionMutationInnovationNumber(1, 3);
        h.getNewConnectionMutationInnovationNumber(1, 4);
        h.getNewConnectionMutationInnovationNumber(2, 3);
        h.getNewConnectionMutationInnovationNumber(2, 4);
        h.getNewConnectionMutationInnovationNumber(3, 5);
        h.getNewConnectionMutationInnovationNumber(4, 5);
        h.getNewConnectionMutationInnovationNumber(5, 6);
        h.getNewConnectionMutationInnovationNumber(5, 7);

        g.addNodes(n0, n1, n2, n3, n4, n5, n6, n7);
        g.fullyConnect(h, new Parameters(), ThreadLocalRandom.current());

        List<Connection> cons = g.getConnections().asOrderedList();
        assertEquals(10, cons.size());

        Connection c = cons.get(0);
        assertEquals(0, c.getInnovationNumber());
        assertEquals(0, c.getInNodeId());
        assertEquals(3, c.getOutNodeId());

        c = cons.get(1);
        assertEquals(1, c.getInnovationNumber());
        assertEquals(0, c.getInNodeId());
        assertEquals(4, c.getOutNodeId());

        c = cons.get(2);
        assertEquals(2, c.getInnovationNumber());
        assertEquals(1, c.getInNodeId());
        assertEquals(3, c.getOutNodeId());

        c = cons.get(3);
        assertEquals(3, c.getInnovationNumber());
        assertEquals(1, c.getInNodeId());
        assertEquals(4, c.getOutNodeId());

        c = cons.get(4);
        assertEquals(4, c.getInnovationNumber());
        assertEquals(2, c.getInNodeId());
        assertEquals(3, c.getOutNodeId());

        c = cons.get(5);
        assertEquals(5, c.getInnovationNumber());
        assertEquals(2, c.getInNodeId());
        assertEquals(4, c.getOutNodeId());

        c = cons.get(6);
        assertEquals(6, c.getInnovationNumber());
        assertEquals(3, c.getInNodeId());
        assertEquals(5, c.getOutNodeId());

        c = cons.get(7);
        assertEquals(7, c.getInnovationNumber());
        assertEquals(4, c.getInNodeId());
        assertEquals(5, c.getOutNodeId());

        c = cons.get(8);
        assertEquals(8, c.getInnovationNumber());
        assertEquals(5, c.getInNodeId());
        assertEquals(6, c.getOutNodeId());

        c = cons.get(9);
        assertEquals(9, c.getInnovationNumber());
        assertEquals(5, c.getInNodeId());
        assertEquals(7, c.getOutNodeId());
    }

    /**
     * No excess genes and one empty genome
     */
    @Test
    public void excessGenesWithTest0 () {
        Genome g1 = new Genome();
        Genome g2 = new Genome();
        assertEquals(0, Genome.excessGenesBetween(g1, g2));
        assertEquals(0, Genome.excessGenesBetween(g2, g1));

        Connection c1 = new Connection(0, 0, 0);
        Connection c2 = new Connection(1, 0, 0);
        g1.addConnections(c1, c2);
        assertEquals(2, Genome.excessGenesBetween(g1, g2));
        assertEquals(2, Genome.excessGenesBetween(g2, g1));

        g2.addConnections(c1.clone());
        assertEquals(1, Genome.excessGenesBetween(g1, g2));
        assertEquals(1, Genome.excessGenesBetween(g2, g1));

        g2.addConnections(c2.clone());
        assertEquals(0, Genome.excessGenesBetween(g1, g2));
        assertEquals(0, Genome.excessGenesBetween(g2, g1));
    }

    /**
     * Example in the paper.
     */
    @Test
    public void excessGenesWithTest1 () {
        Connection c1 = new Connection(1, 1, 4);
        Connection c2 = new Connection(2, 2, 4);
        Connection c3 = new Connection(3, 4, 4);
        Connection c4 = new Connection(4, 2, 5);
        Connection c5 = new Connection(5, 5, 4);
        Connection c6 = new Connection(6, 5, 6);
        Connection c7 = new Connection(7, 6, 4);
        Connection c8 = new Connection(8, 1, 5);
        Connection c9 = new Connection(9, 3, 5);
        Connection c10 = new Connection(10, 1, 6);

        Genome g1 = new Genome();
        Genome g2 = new Genome();
        g1.addConnections(c1, c2, c3, c4, c5, c8);
        g2.addConnections(c1, c2, c3, c4, c5, c6, c7, c9, c10);
        assertEquals(2, Genome.excessGenesBetween(g1, g2));
        assertEquals(2, Genome.excessGenesBetween(g2, g1));

        Genome g3 = new Genome();
        Genome g4 = new Genome();
        g3.addConnections(c1, c2, c3, c5);
        g4.addConnections(c1, c2, c3, c4, c5, c6, c7, c9, c10);
        assertEquals(4, Genome.excessGenesBetween(g3, g4));
        assertEquals(4, Genome.excessGenesBetween(g4, g3));
    }

    /**
     * Simple cases.
     */
    @Test
    public void disjointGenesWithTest0 () {
        Genome g1 = new Genome();
        Genome g2 = new Genome();
        assertEquals(0, Genome.disjointGenesBetween(g1, g2));
        assertEquals(0, Genome.disjointGenesBetween(g2, g1));

        Connection c0 = new Connection(0, 0, 0);
        Connection c2 = new Connection(2, 0, 0);
        g1.addConnections(c0, c2);

        assertEquals(0, Genome.disjointGenesBetween(g1, g2));
        assertEquals(0, Genome.disjointGenesBetween(g2, g1));

        Connection c1 = new Connection(1, 0, 0);
        g2.addConnections(c1);
        assertEquals(2, Genome.disjointGenesBetween(g1, g2));
        assertEquals(2, Genome.disjointGenesBetween(g2, g1));

        g2.addConnection(c2);
        assertEquals(2, Genome.disjointGenesBetween(g1, g2));
        assertEquals(2, Genome.disjointGenesBetween(g2, g1));

        g2.addConnection(c0);
        assertEquals(1, Genome.disjointGenesBetween(g1, g2));
        assertEquals(1, Genome.disjointGenesBetween(g2, g1));

        g1.addConnection(c1);
        assertEquals(0, Genome.disjointGenesBetween(g1, g2));
        assertEquals(0, Genome.disjointGenesBetween(g2, g1));
    }

    @Test
    public void disjointGenesWithTest1 () {
        Connection c0 = new Connection(0, 0, 0);
        Connection c1 = new Connection(1, 0, 0);
        Genome g1 = new Genome().addConnections(c0);
        Genome g2 = new Genome().addConnections(c0, c1);

        assertEquals(0, Genome.disjointGenesBetween(g1, g2));
        assertEquals(0, Genome.disjointGenesBetween(g2, g1));

        Connection c2 = new Connection(2, 0, 0);
        g1.addConnections(c1, c2);

        assertEquals(0, Genome.disjointGenesBetween(g2, g1));
        assertEquals(0, Genome.disjointGenesBetween(g2, g1));
    }

    /**
     * Example in the paper.
     */
    @Test
    public void disjointGenesWithTest2 () {
        Connection c1 = new Connection(1, 1, 4);
        Connection c2 = new Connection(2, 2, 4);
        Connection c3 = new Connection(3, 4, 4);
        Connection c4 = new Connection(4, 2, 5);
        Connection c5 = new Connection(5, 5, 4);
        Connection c6 = new Connection(6, 5, 6);
        Connection c7 = new Connection(7, 6, 4);
        Connection c8 = new Connection(8, 1, 5);
        Connection c9 = new Connection(9, 3, 5);
        Connection c10 = new Connection(10, 1, 6);

        Genome g1 = new Genome();
        Genome g2 = new Genome();
        g1.addConnections(c1, c2, c3, c4, c5, c8);
        g2.addConnections(c1, c2, c3, c4, c5, c6, c7, c9, c10);

        assertEquals(3, Genome.disjointGenesBetween(g1, g2));
        assertEquals(3, Genome.disjointGenesBetween(g2, g1));

        Genome g3 = new Genome();
        Genome g4 = new Genome();
        g3.addConnections(c1, c2,     c4, c5, c8);
        g4.addConnections(c1, c2, c3, c4,        c9, c10);
        assertEquals(3, Genome.disjointGenesBetween(g3, g4));
        assertEquals(3, Genome.disjointGenesBetween(g4, g3));

        g4.addConnection(c8);
        assertEquals(2, Genome.disjointGenesBetween(g3, g4));
        assertEquals(2, Genome.disjointGenesBetween(g4, g3));
    }

    /**
     * Simple cases.
     */
    @Test
    public void averageWeightDifferenceTest0 () {
        Genome g1 = new Genome();
        Genome g2 = new Genome();
        Connection c0 = new Connection(0, 0, 0, .5);
        Connection c1 = new Connection(0, 0, 0, 2);
        Connection c2 = new Connection(1, 0, 0, 40);
        assertEquals(0, Genome.averageWeightDifferenceBetween(g1, g2));
        assertEquals(0, Genome.averageWeightDifferenceBetween(g2, g1));

        g1.addConnection(c0);
        assertEquals(0, Genome.averageWeightDifferenceBetween(g1, g2));
        assertEquals(0, Genome.averageWeightDifferenceBetween(g2, g1));

        g2.addConnections(c2);
        assertEquals(0, Genome.averageWeightDifferenceBetween(g1, g2));
        assertEquals(0, Genome.averageWeightDifferenceBetween(g2, g1));

        g2.addConnections(c1);
        assertEquals(1.5, Genome.averageWeightDifferenceBetween(g1, g2));
        assertEquals(1.5, Genome.averageWeightDifferenceBetween(g2, g1));

        Connection c3 = new Connection(2, 0, 0, 3);
        Connection c4 = new Connection(2, 0, 0, -3);
        g1.addConnection(c3);
        g2.addConnection(c4);
        assertEquals(3.75, Genome.averageWeightDifferenceBetween(g1, g2));
        assertEquals(3.75, Genome.averageWeightDifferenceBetween(g2, g1));
    }

    /**
     * Example in the paper (with made up weights).
     */
    @Test
    public void averageWeightDifferenceTest1 () {
        Connection c1 = new Connection(1, 1, 4, 1);
        Connection c1_ = new Connection(1, 1, 4, 3);
        Connection c2 = new Connection(2, 2, 4, -3);
        Connection c2_ = new Connection(2, 2, 4, 4.5);
        Connection c3 = new Connection(3, 4, 4, 2);
        Connection c3_ = new Connection(3, 4, 4, 2);
        Connection c4 = new Connection(4, 2, 5, 16);
        Connection c4_ = new Connection(4, 2, 5, -1);
        Connection c5 = new Connection(5, 5, 4, 0);
        Connection c5_ = new Connection(5, 5, 4, .5);
        Connection c6 = new Connection(6, 5, 6, -1);
        Connection c7 = new Connection(7, 6, 4, 5);
        Connection c8 = new Connection(8, 1, 5, 12);
        Connection c9 = new Connection(9, 3, 5, 3);
        Connection c10 = new Connection(10, 1, 6, 0.1);

        Genome g1 = new Genome();
        Genome g2 = new Genome();
        g1.addConnections(c1, c2, c3, c4, c5, c8);
        g2.addConnections(c1_, c2_, c3_, c4_, c5_, c6, c7, c9, c10);

        assertEquals(5.4, Genome.averageWeightDifferenceBetween(g1, g2), .00001);
        assertEquals(5.4, Genome.averageWeightDifferenceBetween(g2, g1), .00001);
    }

    @Test
    public void compatibilityWithTest0 () {
        Connection c1 = new Connection(1, 1, 4, 1);
        Connection c1_ = new Connection(1, 1, 4, 3);
        Connection c2 = new Connection(2, 2, 4, -3);
        Connection c2_ = new Connection(2, 2, 4, 4.5);
        Connection c3 = new Connection(3, 4, 4, 2);
        Connection c3_ = new Connection(3, 4, 4, 2);
        Connection c4 = new Connection(4, 2, 5, -1);
        Connection c4_ = new Connection(4, 2, 5, 16);
        Connection c5 = new Connection(5, 5, 4, 0);
        Connection c5_ = new Connection(5, 5, 4, .5);
        Connection c6 = new Connection(6, 5, 6, -1);
        Connection c7 = new Connection(7, 6, 4, 5);
        Connection c8 = new Connection(8, 1, 5, 12);
        Connection c9 = new Connection(9, 3, 5, 3);
        Connection c10 = new Connection(10, 1, 6, 0.1);

        Genome g1 = new Genome();
        Genome g2 = new Genome();
        g1.addConnections(c1, c2, c3, c4, c5, c8);
        g2.addConnections(c1_, c2_, c3_, c4_, c5_, c6, c7, c9, c10);

        Parameters params = new Parameters();
        params.excessGenesCompatibilityCoefficient = 7;
        params.disjointGenesCompatibilityCoefficient = 13;
        params.averageWeightDifferenceCompatibilityCoefficient = 1;

        assertEquals(58.4, Genome.compatibilityBetween(g1, g2, params), .00001);
        assertEquals(58.4, Genome.compatibilityBetween(g2, g1, params), .00001);
    }

    /**
     * Compatibility with test for large genomes
     */
    @Test
    public void compatibilityWithTest1 () {
        Connection c1 = new Connection(1, 1, 4, 1);
        Connection c1_ = new Connection(1, 1, 4, 3);
        Connection c2 = new Connection(2, 2, 4, -3);
        Connection c2_ = new Connection(2, 2, 4, 4.5);
        Connection c3 = new Connection(3, 4, 4, 2);
        Connection c3_ = new Connection(3, 4, 4, 2);
        Connection c4 = new Connection(4, 2, 5, -1);
        Connection c4_ = new Connection(4, 2, 5, 16);
        Connection c5 = new Connection(5, 5, 4, 0);
        Connection c5_ = new Connection(5, 5, 4, .5);
        Connection c6 = new Connection(6, 5, 6, -1);
        Connection c7 = new Connection(7, 6, 4, 5);
        Connection c8 = new Connection(8, 1, 5, 12);
        Connection c9 = new Connection(9, 3, 5, 3);
        Connection c10 = new Connection(10, 1, 6, 0.1);

        Genome g1 = new Genome();
        Genome g2 = new Genome();
        g1.addConnections(c1, c2, c3, c4, c5, c8);
        g2.addConnections(c1_, c2_, c3_, c4_, c5_, c6, c7, c9, c10);

        Parameters params = new Parameters();
        params.excessGenesCompatibilityCoefficient = 7;
        params.disjointGenesCompatibilityCoefficient = 13;
        params.averageWeightDifferenceCompatibilityCoefficient = 1;
        params.largeGenomeNormalizerThreshold = 5;

        assertEquals(11.2888, Genome.compatibilityBetween(g1, g2, params), 0.0001);
        assertEquals(11.2888, Genome.compatibilityBetween(g2, g1, params), 0.0001);
    }

    /**
     * OR and AND gates.
     */
    @Test
    public void feedForwardTest0 () {
        // OR gate
        Node n0 = new Node(0, Node.Type.INPUT, 0);
        Node n1 = new Node(1, Node.Type.INPUT, 0);
        Node bias = new Node(2, Node.Type.INPUT, 0);
        Node n3 = new Node(3, Node.Type.OUTPUT, 1);

        Connection c0 = new Connection(0, 0, 3);
        Connection c1 = new Connection(1, 1, 3);
        Connection c2 = new Connection(2, 2, 3);
        c0.setWeight(1);
        c1.setWeight(1);
        c2.setWeight(0);

        Genome genome = new Genome();
        genome.addNodes(n0, n1, n3);
        genome.addConnections(c0, c1);

        StepActivationFunction function = new StepActivationFunction();
        function.offset = 0.5;

        assertArrayEquals(new double[] {0},
                genome.calculateOutput(new double[] {0, 0}, function),
                0.0001);
        assertArrayEquals(new double[] {1},
                genome.calculateOutput(new double[] {0, 1}, function),
                0.0001);
        assertArrayEquals(new double[] {1},
                genome.calculateOutput(new double[] {1, 0}, function),
                0.0001);
        assertArrayEquals(new double[] {1},
                genome.calculateOutput(new double[] {1, 1}, function),
                0.0001);

        // Change it to an AND gate
        c2.setWeight(-1);
        genome.addNode(bias);
        genome.addConnection(c2);

        assertArrayEquals(new double[] {0},
                genome.calculateOutput(new double[] {0, 0, 1}, function),
                0.0001);
        assertArrayEquals(new double[] {0},
                genome.calculateOutput(new double[] {0, 1, 1}, function),
                0.0001);
        assertArrayEquals(new double[] {0},
                genome.calculateOutput(new double[] {1, 0, 1}, function),
                0.0001);
        assertArrayEquals(new double[] {1},
                genome.calculateOutput(new double[] {1, 1, 1}, function),
                0.0001);
    }

    /**
     * XOR gate.
     */
    @Test
    public void feedForwardTest1 () {
        // XOR gate
        Node n0 = new Node(0, Node.Type.INPUT, 0);
        Node n1 = new Node(1, Node.Type.INPUT, 0);
        Node n2 = new Node(2, Node.Type.HIDDEN, 1);
        Node n3 = new Node(3, Node.Type.OUTPUT, 2);

        Connection c0 = new Connection(0, 0, 3);
        Connection c1 = new Connection(1, 0, 2);
        Connection c2 = new Connection(2, 1, 2);
        Connection c3 = new Connection(3, 1, 3);
        Connection c5 = new Connection(5, 2, 3);
        c0.setWeight(1);
        c1.setWeight(.4);
        c2.setWeight(.4);
        c3.setWeight(1);
        c5.setWeight(-2);

        Genome genome = new Genome();
        genome.addNodes(n0, n1, n2, n3);
        genome.addConnections(c0, c1, c2, c3, c5);

        StepActivationFunction function = new StepActivationFunction();
        function.offset = .5;

        assertArrayEquals(new double[] {0},
                genome.calculateOutput(new double[] {0, 0}, function),
                0.0001);
        assertArrayEquals(new double[] {1},
                genome.calculateOutput(new double[] {0, 1}, function),
                0.0001);
        assertArrayEquals(new double[] {1},
                genome.calculateOutput(new double[] {1, 0}, function),
                0.0001);
        assertArrayEquals(new double[] {0},
                genome.calculateOutput(new double[] {1, 1}, function),
                0.0001);
    }

    /**
     * Test before and after new node mutation.
     */
    @Test
    @SuppressWarnings("TextBlockMigration")
    public void feedForwardTest2 () {
        Node n0 = new Node(0, Node.Type.INPUT, 0);
        Node n1 = new Node(1, Node.Type.INPUT, 0);
        Node n2 = new Node(2, Node.Type.OUTPUT, 1);

        Connection c0 = new Connection(0, 0, 2);
        Connection c1 = new Connection(1, 1, 2);
        c0.setWeight(2);
        c1.setWeight(-5);

        Genome genome = new Genome();
        genome.addNodes(n0, n1, n2);
        genome.addConnections(c0, c1);

        SigmoidActivationFunction function = new SigmoidActivationFunction();
        function.logisticGrowthRate = 1.0;

        assertArrayEquals(new double[] {0.1824},
                genome.calculateOutput(new double[] {0, 0}, function),
                0.0001);
        assertArrayEquals(new double[] {0.0656},
                genome.calculateOutput(new double[] {0, 1}, function),
                0.0001);
        assertArrayEquals(new double[] {0.2615},
                genome.calculateOutput(new double[] {1, 0}, function),
                0.0001);
        assertArrayEquals(new double[] {0.3085},
                genome.calculateOutput(new double[] {-1, -1}, function),
                0.0001);

        // Add a mutation in c0
        InnovationHistory history = new InnovationHistory(2, 1);
        Mutation.addNodeMutation(genome, c0, history);

        assertEquals("Genome{\n" +
                "\tnodes=[" +
                "Node{id=0, type=INPUT, layer=0}, " +
                "Node{id=1, type=INPUT, layer=0}, " +
                "Node{id=3, type=HIDDEN, layer=1}, " +
                "Node{id=2, type=OUTPUT, layer=2}],\n" +
                "\tconnections=[" +
                "Connection{innovNum=0, in=0, out=2, weight=2.0, enabled=false}, " +
                "Connection{innovNum=1, in=1, out=2, weight=-5.0, enabled=true}, " +
                "Connection{innovNum=2, in=0, out=3, weight=1.0, enabled=true}, " +
                "Connection{innovNum=3, in=3, out=2, weight=2.0, enabled=true}]" +
                "\n}", genome.toString());

        assertArrayEquals(new double[] {0.2218},
                genome.calculateOutput(new double[] {0, 0}, function),
                0.0001);
        assertArrayEquals(new double[] {0.0307},
                genome.calculateOutput(new double[] {-5, 2.1}, function),
                0.0001);
        assertArrayEquals(new double[] {0.0179},
                genome.calculateOutput(new double[] {-10, 10}, function),
                0.0001);

    }

    /**
     * Recursive connection (backwards, not to self).
     */
    @Test
    public void feedForwardTest3 () {
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.OUTPUT);
        Connection c0 = new Connection(0, 0, 1);
        c0.setWeight(5);
        Connection c1 = new Connection(1, 1, 0);
        c1.setWeight(-0.2);

        Genome g = new Genome();
        g.addNodes(n0, n1);
        g.addConnections(c0, c1);

        SigmoidActivationFunction f = new SigmoidActivationFunction();
        f.offset = 0.0;
        f.logisticGrowthRate = 3;

        assertEquals(0.9994, g.calculateOutput(new double[] {0}, f)[0], .0001);
        assertEquals(0.9951, g.calculateOutput(new double[] {0}, f)[0], .0001);

        c1.setWeight(-3);
        n0.reset();
        n1.reset();

        assertEquals(0.9994, g.calculateOutput(new double[] {0}, f)[0], .0001);
        assertEquals(0.5004, g.calculateOutput(new double[] {0}, f)[0], .0001);
        assertEquals(0.5020, g.calculateOutput(new double[] {-1}, f)[0], .0001);
    }

    /**
     * Connection to self.
     */
    @Test
    public void feedForwardTest4 () {
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.OUTPUT);
        Connection c0 = new Connection(0, 0, 1);
        Connection c1 = new Connection(1, 1, 1);
        c0.setWeight(0.2);
        c1.setWeight(-0.1);

        Genome g = new Genome();
        g.addNodes(n0, n1);
        g.addConnections(c0, c1);

        SigmoidActivationFunction f = new SigmoidActivationFunction();
        f.logisticGrowthRate = 1.0;
        f.offset = 0.0;

        assertEquals(0.5364, g.calculateOutput(new double[] {1}, f)[0], .0001);
        assertEquals(0.4925, g.calculateOutput(new double[] {-2}, f)[0], .0001);
        assertEquals(0.5352, g.calculateOutput(new double[] {3}, f)[0], .0001);
        assertEquals(0.5341, g.calculateOutput(new double[] {3}, f)[0], .0001);
    }

    /**
     * Connection to self 2.
     */
    @Test
    public void feedForwardTest5 () {
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.OUTPUT);
        Connection c0 = new Connection(0, 0, 1);
        Connection c1 = new Connection(1, 1, 1);
        c0.setWeight(0.2);
        c1.setWeight(-0.4);

        Genome g = new Genome();
        g.addNodes(n0, n1);
        g.addConnections(c0, c1);

        SigmoidActivationFunction f = new SigmoidActivationFunction();
        f.logisticGrowthRate = 1.0;
        f.offset = 0.0;

        assertEquals(0.5364, g.calculateOutput(new double[] {1}, f)[0], .0001);
        assertEquals(0.4524, g.calculateOutput(new double[] {-2}, f)[0], .0001);
        assertEquals(0.5023, g.calculateOutput(new double[] {3}, f)[0], .0001);
        assertEquals(0.4973, g.calculateOutput(new double[] {3}, f)[0], .0001);
    }
}
