package com.tesladodger.neat.evolution;

import com.tesladodger.neat.Connection;
import com.tesladodger.neat.Genome;
import com.tesladodger.neat.Node;
import com.tesladodger.neat.utils.Parameters;
import com.tesladodger.neat.utils.functions.ActivationFunction;
import com.tesladodger.neat.utils.structures.NodeList;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SuppressWarnings("TextBlockMigration")
public class CrossoverTest {

    @Test
    public void compareFitnessTest () {
        Parameters params = new Parameters();
        Genome g1 = new Genome();
        Genome g2 = new Genome();
        g1.setFitness(5);
        g2.setFitness(6);

        params.fitnessTolerance = 0.0;
        assertEquals(1, Crossover.compareFitness(g1, g2, params));
        assertEquals(-1, Crossover.compareFitness(g2, g1, params));

        params.fitnessTolerance = 0.5;
        assertEquals(1, Crossover.compareFitness(g1, g2, params));
        assertEquals(-1, Crossover.compareFitness(g2, g1, params));

        params.fitnessTolerance = 1.0;
        assertEquals(0, Crossover.compareFitness(g1, g2, params));
        assertEquals(0, Crossover.compareFitness(g2, g1, params));
    }

    /**
     * Same fitness parents, only matching genes.
     */
    @Test
    public void connectionInheritanceTest0 () {
        Parameters params = new Parameters();
        Genome g1 = new Genome();
        Genome g2 = new Genome();
        g1.setFitness(0.0);
        g2.setFitness(0.0);

        Connection c0 = new Connection(0, 0, 2);
        Connection c1 = new Connection(1, 1, 2);

        g1.addConnections(c0, c1);
        g2.addConnections(c0.clone(), c1.clone());

        List<Connection> cCons =
                Crossover.mate(g1, g2, params, ThreadLocalRandom.current()).getConnections().asOrderedList();
        assertEquals(2, cCons.size());
        assertNotSame(c0, cCons.get(0));
        assertEquals(c0, cCons.get(0));
        assertNotSame(c1, cCons.get(1));
        assertEquals(c1, cCons.get(1));
    }

    /**
     * Different fitness, only matching genes.
     */
    @Test
    public void connectionInheritanceTest1 () {
        Parameters params = new Parameters();
        params.fittestParentBias = .5;
        Genome g1 = new Genome();
        Genome g2 = new Genome();
        g1.setFitness(0.0);
        g2.setFitness(5.0);

        Connection c0 = new Connection(0, 0, 2);
        Connection c1 = new Connection(1, 1, 2);
        Connection c2 = c0.clone();
        Connection c3 = c1.clone();
        c2.setWeight(1);
        c3.setWeight(5);

        g1.addConnections(c0, c1);
        g2.addConnections(c2, c3);

        List<Connection> cCons = Crossover.mate(g1, g2, params, ThreadLocalRandom.current())
                .getConnections().asOrderedList();
        assertEquals(2, cCons.size());
        assertNotSame(c2, cCons.get(0));
        assertEquals(c2, cCons.get(0));
        assertNotSame(c3, cCons.get(1));
        assertEquals(c3, cCons.get(1));
    }

    /**
     * Only inputs and outputs.
     */
    @Test
    public void nodeInheritanceTest0 () {
        Parameters params = new Parameters();
        Genome g1 = new Genome();
        Genome g2 = new Genome();
        g1.setFitness(0.0);
        g2.setFitness(0.0);

        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.INPUT);
        Node n2 = new Node(2, Node.Type.OUTPUT);
        Connection c0 = new Connection(0, 0, 2);
        Connection c1 = new Connection(1, 1, 2);

        g1.addConnections(c0, c1);
        g1.addNodes(n0, n1, n2);
        g2.addConnections(c0.clone(), c1.clone());
        g2.addNodes(n0.clone(), n1.clone(), n2.clone());

        NodeList cNodes = Crossover.mate(g1, g2, params, ThreadLocalRandom.current()).getNodes();

        assertEquals(3, cNodes.size());
        assertEquals(n0, cNodes.get(0));
        assertNotSame(n0, cNodes.get(0));
        assertEquals(n1, cNodes.get(1));
        assertNotSame(n1, cNodes.get(1));
        assertEquals(n2, cNodes.get(2));
        assertNotSame(n2, cNodes.get(2));
    }

    /**
     * Hidden node, so that simple layer fixing is necessary.
     */
    @Test
    public void nodeInheritanceTest1 () {
        Parameters params = new Parameters();
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.INPUT);
        Node n2 = new Node(2, Node.Type.OUTPUT, 2);
        Node n3 = new Node(3, Node.Type.HIDDEN, 1);
        Connection c0 = new Connection(0, 0, 2, 0.0, false);
        Connection c1 = new Connection(1, 1, 2);
        Connection c2 = new Connection(2, 0, 3);
        Connection c3 = new Connection(3, 3, 2);

        Genome parent1 = new Genome();
        parent1.addNodes(n0, n1, n2, n3);
        parent1.addConnections(c0, c1, c2, c3);
        Genome parent2 = parent1.clone();

        Genome child = Crossover.mate(parent1, parent2, params, ThreadLocalRandom.current());

        assertEquals("Genome{\n" +
                "\tnodes=[" +
                "Node{id=0, type=INPUT, layer=0}, " +
                "Node{id=1, type=INPUT, layer=0}, " +
                "Node{id=3, type=HIDDEN, layer=1}, " +
                "Node{id=2, type=OUTPUT, layer=2}],\n" +
                "\tconnections=[" +
                "Connection{innovNum=0, in=0, out=2, weight=0.0, enabled=false}, " +
                "Connection{innovNum=1, in=1, out=2, weight=0.0, enabled=true}, " +
                "Connection{innovNum=2, in=0, out=3, weight=0.0, enabled=true}, " +
                "Connection{innovNum=3, in=3, out=2, weight=0.0, enabled=true}]\n}",
                child.toString());
    }

    /**
     * Example in the paper, layer fixing is necessary.
     */
    @RepeatedTest(10)
    public void nodeInheritanceTest2 () {
        Parameters params = new Parameters();
        params.disableGeneProbability = 1;
        params.reEnableGeneProbability = 0;
        Node na0 = new Node(0, Node.Type.INPUT);
        Node na1 = new Node(1, Node.Type.INPUT);
        Node na2 = new Node(2, Node.Type.INPUT);
        Node na3 = new Node(3, Node.Type.OUTPUT, 2);
        Node na4 = new Node(4, Node.Type.HIDDEN, 1);
        Connection ca0 = new Connection(0, 0, 3);
        Connection ca1 = new Connection(1, 1, 3, 0.0, false);
        Connection ca2 = new Connection(2, 2, 3);
        Connection ca3 = new Connection(3, 1, 4);
        Connection ca4 = new Connection(4, 4, 3);
        Connection ca5 = new Connection(7, 0, 4);
        Genome ga = new Genome();
        ga.addNodes(na0, na1, na2, na3, na4);
        ga.addConnections(ca0, ca1, ca2, ca3, ca4, ca5);

        Node nb0 = new Node(0, Node.Type.INPUT);
        Node nb1 = new Node(1, Node.Type.INPUT);
        Node nb2 = new Node(2, Node.Type.INPUT);
        Node nb3 = new Node(3, Node.Type.OUTPUT, 3);
        Node nb4 = new Node(4, Node.Type.HIDDEN, 1);
        Node nb5 = new Node(5, Node.Type.HIDDEN, 2);
        Connection cb0 = new Connection(0, 0, 3);
        Connection cb1 = new Connection(1, 1, 3, 0.0, false);
        Connection cb2 = new Connection(2, 2, 3);
        Connection cb3 = new Connection(3, 1, 4);
        Connection cb4 = new Connection(4, 4, 3, 0.0, false);
        Connection cb5 = new Connection(5, 4, 5);
        Connection cb6 = new Connection(6, 5, 3);
        Connection cb7 = new Connection(8, 2, 4);
        Connection cb8 = new Connection(9, 0, 5);
        Genome gb = new Genome();
        gb.addNodes(nb0, nb1, nb2, nb3, nb4, nb5);
        gb.addConnections(cb0, cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8);

        Genome child = Crossover.mate(ga, gb, params, ThreadLocalRandom.current());
        assertEquals("Genome{\n" +
                "\tnodes=[" +
                "Node{id=0, type=INPUT, layer=0}, " +
                "Node{id=1, type=INPUT, layer=0}, " +
                "Node{id=2, type=INPUT, layer=0}, " +
                "Node{id=4, type=HIDDEN, layer=1}, " +
                "Node{id=5, type=HIDDEN, layer=2}, " +
                "Node{id=3, type=OUTPUT, layer=3}],\n" +
                "\tconnections=[" +
                "Connection{innovNum=0, in=0, out=3, weight=0.0, enabled=true}, " +
                "Connection{innovNum=1, in=1, out=3, weight=0.0, enabled=false}, " +
                "Connection{innovNum=2, in=2, out=3, weight=0.0, enabled=true}, " +
                "Connection{innovNum=3, in=1, out=4, weight=0.0, enabled=true}, " +
                "Connection{innovNum=4, in=4, out=3, weight=0.0, enabled=false}, " +
                "Connection{innovNum=5, in=4, out=5, weight=0.0, enabled=true}, " +
                "Connection{innovNum=6, in=5, out=3, weight=0.0, enabled=true}, " +
                "Connection{innovNum=7, in=0, out=4, weight=0.0, enabled=true}, " +
                "Connection{innovNum=8, in=2, out=4, weight=0.0, enabled=true}, " +
                "Connection{innovNum=9, in=0, out=5, weight=0.0, enabled=true}]\n" +
                "}", child.toString());
    }

    /**
     * Mate two default parents with given fitness and given parameters and return the
     * child for testing.
     *
     * @param params desired parameters for testing;
     *
     * @return child from crossover;
     */
    public Genome mateABParents (double fitnessA, double fitnessB, Parameters params) {
        Node na0 = new Node(0, Node.Type.INPUT);
        Node na1 = new Node(1, Node.Type.INPUT);
        Node na2 = new Node(2, Node.Type.INPUT);
        Node na3 = new Node(3, Node.Type.OUTPUT, 2);
        Node na4 = new Node(4, Node.Type.HIDDEN, 1);
        Connection ca0 = new Connection(0, 0, 3);
        Connection ca1 = new Connection(1, 1, 3, 0.0, false);
        Connection ca2 = new Connection(2, 2, 3);
        Connection ca3 = new Connection(3, 1, 4);
        Connection ca4 = new Connection(4, 4, 3);
        Connection ca5 = new Connection(7, 0, 4);
        Genome ga = new Genome();
        ga.addNodes(na0, na1, na2, na3, na4);
        ga.addConnections(ca0, ca1, ca2, ca3, ca4, ca5);

        Node nb0 = new Node(0, Node.Type.INPUT);
        Node nb1 = new Node(1, Node.Type.INPUT);
        Node nb2 = new Node(2, Node.Type.INPUT);
        Node nb3 = new Node(3, Node.Type.OUTPUT, 3);
        Node nb4 = new Node(4, Node.Type.HIDDEN, 1);
        Node nb5 = new Node(5, Node.Type.HIDDEN, 2);
        Connection cb0 = new Connection(0, 0, 3);
        Connection cb1 = new Connection(1, 1, 3, 0.0, false);
        Connection cb2 = new Connection(2, 2, 3);
        Connection cb3 = new Connection(3, 1, 4);
        Connection cb4 = new Connection(4, 4, 3, 0.0, false);
        Connection cb5 = new Connection(5, 4, 5);
        Connection cb6 = new Connection(6, 5, 3);
        Connection cb7 = new Connection(8, 2, 4);
        Connection cb8 = new Connection(9, 0, 5);
        Genome gb = new Genome();
        gb.addNodes(nb0, nb1, nb2, nb3, nb4, nb5);
        gb.addConnections(cb0, cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8);

        ga.setFitness(fitnessA);
        gb.setFitness(fitnessB);

        return Crossover.mate(ga, gb, params, ThreadLocalRandom.current());
    }

    @Test
    public void disjointExcessFromFittestTest0 () {
        Parameters params = new Parameters();
        params.disableGeneProbability = 1;
        params.reEnableGeneProbability = 0;

        // parent A is fittest
        Genome child0 = mateABParents(5, 2, params);
        Node[] nodes0 = child0.getNodes().asArray();
        assertEquals(5, nodes0.length);

        assertEquals(0, nodes0[0].getId());
        assertEquals(0, nodes0[0].getLayer());
        assertEquals( Node.Type.INPUT, nodes0[0].getType());

        assertEquals(1, nodes0[1].getId());
        assertEquals(0, nodes0[1].getLayer());
        assertEquals( Node.Type.INPUT, nodes0[1].getType());

        assertEquals(2, nodes0[2].getId());
        assertEquals(0, nodes0[2].getLayer());
        assertEquals( Node.Type.INPUT, nodes0[2].getType());

        assertEquals(4, nodes0[3].getId());
        assertEquals(1, nodes0[3].getLayer());
        assertEquals( Node.Type.HIDDEN, nodes0[3].getType());

        assertEquals(3, nodes0[4].getId());
        assertEquals(2, nodes0[4].getLayer());
        assertEquals( Node.Type.OUTPUT, nodes0[4].getType());

        List<Connection> cons0 = child0.getConnections().asOrderedList();
        assertEquals(6, cons0.size());

        assertEquals(0, cons0.get(0).getInnovationNumber());
        assertEquals(0, cons0.get(0).getInNodeId());
        assertEquals(3, cons0.get(0).getOutNodeId());
        assertTrue(cons0.get(0).isEnabled());

        assertEquals(1, cons0.get(1).getInnovationNumber());
        assertEquals(1, cons0.get(1).getInNodeId());
        assertEquals(3, cons0.get(1).getOutNodeId());
        assertFalse(cons0.get(1).isEnabled());

        assertEquals(2, cons0.get(2).getInnovationNumber());
        assertEquals(2, cons0.get(2).getInNodeId());
        assertEquals(3, cons0.get(2).getOutNodeId());
        assertTrue(cons0.get(2).isEnabled());

        assertEquals(3, cons0.get(3).getInnovationNumber());
        assertEquals(1, cons0.get(3).getInNodeId());
        assertEquals(4, cons0.get(3).getOutNodeId());
        assertTrue(cons0.get(3).isEnabled());

        assertEquals(4, cons0.get(4).getInnovationNumber());
        assertEquals(4, cons0.get(4).getInNodeId());
        assertEquals(3, cons0.get(4).getOutNodeId());
        assertFalse(cons0.get(4).isEnabled());

        assertEquals(7, cons0.get(5).getInnovationNumber());
        assertEquals(0, cons0.get(5).getInNodeId());
        assertEquals(4, cons0.get(5).getOutNodeId());
        assertTrue(cons0.get(5).isEnabled());


        // parent B is fittest
        Genome child1 = mateABParents(2, 5, params);

        Node[] nodes1 = child1.getNodes().asArray();
        assertEquals(6, nodes1.length);

        assertEquals(0, nodes1[0].getId());
        assertEquals(0, nodes1[0].getLayer());
        assertEquals( Node.Type.INPUT, nodes1[0].getType());

        assertEquals(1, nodes1[1].getId());
        assertEquals(0, nodes1[1].getLayer());
        assertEquals( Node.Type.INPUT, nodes1[1].getType());

        assertEquals(2, nodes1[2].getId());
        assertEquals(0, nodes1[2].getLayer());
        assertEquals( Node.Type.INPUT, nodes1[2].getType());

        assertEquals(4, nodes1[3].getId());
        assertEquals(1, nodes1[3].getLayer());
        assertEquals( Node.Type.HIDDEN, nodes1[3].getType());

        assertEquals(5, nodes1[4].getId());
        assertEquals(2, nodes1[4].getLayer());
        assertEquals( Node.Type.HIDDEN, nodes1[4].getType());

        assertEquals(3, nodes1[5].getId());
        assertEquals(3, nodes1[5].getLayer());
        assertEquals( Node.Type.OUTPUT, nodes1[5].getType());

        List<Connection> cons1 = child1.getConnections().asOrderedList();
        assertEquals(9, cons1.size());

        assertEquals(0, cons1.get(0).getInnovationNumber());
        assertEquals(0, cons1.get(0).getInNodeId());
        assertEquals(3, cons1.get(0).getOutNodeId());
        assertTrue(cons1.get(0).isEnabled());

        assertEquals(1, cons1.get(1).getInnovationNumber());
        assertEquals(1, cons1.get(1).getInNodeId());
        assertEquals(3, cons1.get(1).getOutNodeId());
        assertFalse(cons1.get(1).isEnabled());

        assertEquals(2, cons1.get(2).getInnovationNumber());
        assertEquals(2, cons1.get(2).getInNodeId());
        assertEquals(3, cons1.get(2).getOutNodeId());
        assertTrue(cons1.get(2).isEnabled());

        assertEquals(3, cons1.get(3).getInnovationNumber());
        assertEquals(1, cons1.get(3).getInNodeId());
        assertEquals(4, cons1.get(3).getOutNodeId());
        assertTrue(cons1.get(3).isEnabled());

        assertEquals(4, cons1.get(4).getInnovationNumber());
        assertEquals(4, cons1.get(4).getInNodeId());
        assertEquals(3, cons1.get(4).getOutNodeId());
        assertFalse(cons1.get(4).isEnabled());

        assertEquals(5, cons1.get(5).getInnovationNumber());
        assertEquals(4, cons1.get(5).getInNodeId());
        assertEquals(5, cons1.get(5).getOutNodeId());
        assertTrue(cons1.get(5).isEnabled());

        assertEquals(6, cons1.get(6).getInnovationNumber());
        assertEquals(5, cons1.get(6).getInNodeId());
        assertEquals(3, cons1.get(6).getOutNodeId());
        assertTrue(cons1.get(6).isEnabled());

        assertEquals(8, cons1.get(7).getInnovationNumber());
        assertEquals(2, cons1.get(7).getInNodeId());
        assertEquals(4, cons1.get(7).getOutNodeId());
        assertTrue(cons1.get(7).isEnabled());

        assertEquals(9, cons1.get(8).getInnovationNumber());
        assertEquals(0, cons1.get(8).getInNodeId());
        assertEquals(5, cons1.get(8).getOutNodeId());
        assertTrue(cons1.get(8).isEnabled());
    }

    @Test
    public void disjointExcessFromFittestTest1 () {
        Parameters params = new Parameters();
        params.disableGeneProbability = 0;
        params.reEnableGeneProbability = 1;

        // parent A is fittest
        Genome child0 = mateABParents(5, 2, params);
        Node[] nodes0 = child0.getNodes().asArray();
        assertEquals(5, nodes0.length);

        assertEquals(0, nodes0[0].getId());
        assertEquals(0, nodes0[0].getLayer());
        assertEquals( Node.Type.INPUT, nodes0[0].getType());

        assertEquals(1, nodes0[1].getId());
        assertEquals(0, nodes0[1].getLayer());
        assertEquals( Node.Type.INPUT, nodes0[1].getType());

        assertEquals(2, nodes0[2].getId());
        assertEquals(0, nodes0[2].getLayer());
        assertEquals( Node.Type.INPUT, nodes0[2].getType());

        assertEquals(4, nodes0[3].getId());
        assertEquals(1, nodes0[3].getLayer());
        assertEquals( Node.Type.HIDDEN, nodes0[3].getType());

        assertEquals(3, nodes0[4].getId());
        assertEquals(2, nodes0[4].getLayer());
        assertEquals( Node.Type.OUTPUT, nodes0[4].getType());

        List<Connection> cons0 = child0.getConnections().asOrderedList();
        assertEquals(6, cons0.size());

        assertEquals(0, cons0.get(0).getInnovationNumber());
        assertEquals(0, cons0.get(0).getInNodeId());
        assertEquals(3, cons0.get(0).getOutNodeId());
        assertTrue(cons0.get(0).isEnabled());

        assertEquals(1, cons0.get(1).getInnovationNumber());
        assertEquals(1, cons0.get(1).getInNodeId());
        assertEquals(3, cons0.get(1).getOutNodeId());
        assertTrue(cons0.get(1).isEnabled());

        assertEquals(2, cons0.get(2).getInnovationNumber());
        assertEquals(2, cons0.get(2).getInNodeId());
        assertEquals(3, cons0.get(2).getOutNodeId());
        assertTrue(cons0.get(2).isEnabled());

        assertEquals(3, cons0.get(3).getInnovationNumber());
        assertEquals(1, cons0.get(3).getInNodeId());
        assertEquals(4, cons0.get(3).getOutNodeId());
        assertTrue(cons0.get(3).isEnabled());

        assertEquals(4, cons0.get(4).getInnovationNumber());
        assertEquals(4, cons0.get(4).getInNodeId());
        assertEquals(3, cons0.get(4).getOutNodeId());
        assertTrue(cons0.get(4).isEnabled());

        assertEquals(7, cons0.get(5).getInnovationNumber());
        assertEquals(0, cons0.get(5).getInNodeId());
        assertEquals(4, cons0.get(5).getOutNodeId());
        assertTrue(cons0.get(5).isEnabled());


        // parent B is fittest
        Genome child1 = mateABParents(2, 5, params);

        Node[] nodes1 = child1.getNodes().asArray();
        assertEquals(6, nodes1.length);

        assertEquals(0, nodes1[0].getId());
        assertEquals(0, nodes1[0].getLayer());
        assertEquals( Node.Type.INPUT, nodes1[0].getType());

        assertEquals(1, nodes1[1].getId());
        assertEquals(0, nodes1[1].getLayer());
        assertEquals( Node.Type.INPUT, nodes1[1].getType());

        assertEquals(2, nodes1[2].getId());
        assertEquals(0, nodes1[2].getLayer());
        assertEquals( Node.Type.INPUT, nodes1[2].getType());

        assertEquals(4, nodes1[3].getId());
        assertEquals(1, nodes1[3].getLayer());
        assertEquals( Node.Type.HIDDEN, nodes1[3].getType());

        assertEquals(5, nodes1[4].getId());
        assertEquals(2, nodes1[4].getLayer());
        assertEquals( Node.Type.HIDDEN, nodes1[4].getType());

        assertEquals(3, nodes1[5].getId());
        assertEquals(3, nodes1[5].getLayer());
        assertEquals( Node.Type.OUTPUT, nodes1[5].getType());

        List<Connection> cons1 = child1.getConnections().asOrderedList();
        assertEquals(9, cons1.size());

        assertEquals(0, cons1.get(0).getInnovationNumber());
        assertEquals(0, cons1.get(0).getInNodeId());
        assertEquals(3, cons1.get(0).getOutNodeId());
        assertTrue(cons1.get(0).isEnabled());

        assertEquals(1, cons1.get(1).getInnovationNumber());
        assertEquals(1, cons1.get(1).getInNodeId());
        assertEquals(3, cons1.get(1).getOutNodeId());
        assertTrue(cons1.get(1).isEnabled());

        assertEquals(2, cons1.get(2).getInnovationNumber());
        assertEquals(2, cons1.get(2).getInNodeId());
        assertEquals(3, cons1.get(2).getOutNodeId());
        assertTrue(cons1.get(2).isEnabled());

        assertEquals(3, cons1.get(3).getInnovationNumber());
        assertEquals(1, cons1.get(3).getInNodeId());
        assertEquals(4, cons1.get(3).getOutNodeId());
        assertTrue(cons1.get(3).isEnabled());

        assertEquals(4, cons1.get(4).getInnovationNumber());
        assertEquals(4, cons1.get(4).getInNodeId());
        assertEquals(3, cons1.get(4).getOutNodeId());
        assertTrue(cons1.get(4).isEnabled());

        assertEquals(5, cons1.get(5).getInnovationNumber());
        assertEquals(4, cons1.get(5).getInNodeId());
        assertEquals(5, cons1.get(5).getOutNodeId());
        assertTrue(cons1.get(5).isEnabled());

        assertEquals(6, cons1.get(6).getInnovationNumber());
        assertEquals(5, cons1.get(6).getInNodeId());
        assertEquals(3, cons1.get(6).getOutNodeId());
        assertTrue(cons1.get(6).isEnabled());

        assertEquals(8, cons1.get(7).getInnovationNumber());
        assertEquals(2, cons1.get(7).getInNodeId());
        assertEquals(4, cons1.get(7).getOutNodeId());
        assertTrue(cons1.get(7).isEnabled());

        assertEquals(9, cons1.get(8).getInnovationNumber());
        assertEquals(0, cons1.get(8).getInNodeId());
        assertEquals(5, cons1.get(8).getOutNodeId());
        assertTrue(cons1.get(8).isEnabled());
    }

    /**
     * Trying to make adding nodes fail.
     */
    @Test
    public void addNodesTest () {
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.INPUT);
        Node n2 = new Node(2, Node.Type.OUTPUT, 2);
        Genome parent = new Genome().addNodes(n0, n1, n2);

        Connection c0 = new Connection(0, 0, 2, 0.2);
        Connection c1 = new Connection(1, 1, 2, 0.0, false);
        Connection c2 = new Connection(2, 1, 3, -.6);
        Connection c3 = new Connection(3, 3, 4, .4);
        Connection c4 = new Connection(4, 4, 2, -.1);
        Genome child = new Genome().addConnections(c0, c1, c2, c3, c4);

        Crossover.addNodesToChild(child, parent);
        assertEquals("[" +
                "Node{id=0, type=INPUT, layer=0}, " +
                "Node{id=1, type=INPUT, layer=0}, " +
                "Node{id=3, type=HIDDEN, layer=1}, " +
                "Node{id=4, type=HIDDEN, layer=2}, " +
                "Node{id=2, type=OUTPUT, layer=3}" +
                "]", child.getNodes().toString());

        ActivationFunction f = x -> x;

        assertArrayEquals(new double[] {0.2072},
                child.calculateOutput(new double[] {1, .3}, f), 0.00001);
        assertArrayEquals(new double[] {-0.952},
                child.calculateOutput(new double[] {-5, 2}, f), 0.00001);
    }
}
