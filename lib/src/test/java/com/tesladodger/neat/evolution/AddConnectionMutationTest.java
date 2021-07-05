package com.tesladodger.neat.evolution;

import com.tesladodger.neat.Connection;
import com.tesladodger.neat.Genome;
import com.tesladodger.neat.Node;
import com.tesladodger.neat.utils.InnovationHistory;
import com.tesladodger.neat.utils.Parameters;
import com.tesladodger.neat.utils.structures.NodeList;
import com.tesladodger.neat.GenomeBuilder;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public class AddConnectionMutationTest {

    /**
     * Only one input and output.
     */
    @RepeatedTest(20)
    public void addConnectionMutationTest0 () {
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.OUTPUT);
        Genome g = new Genome();
        g.addNodes(n0, n1);
        InnovationHistory h = new InnovationHistory(1, -1);
        Parameters p = new Parameters();
        Mutation.addConnectionMutation(g, 0, 1, h, p, ThreadLocalRandom.current());
        assertEquals(1, g.getConnections().size());
        Connection con = g.getConnections().asOrderedList().get(0);
        assertEquals(0, con.getInnovationNumber());
        assertEquals(0, con.getInNodeId());
        assertEquals(1, con.getOutNodeId());
        assertTrue(con.getWeight() <= 10);
        assertTrue(con.getWeight() >= -10);
    }

    /**
     * Layer fixing.
     */
    @Test
    public void addConnectionMutationTest1 () {
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.HIDDEN, 1);
        Node n2 = new Node(2, Node.Type.HIDDEN, 1);
        Node n3 = new Node(3, Node.Type.OUTPUT, 2);
        Connection c0 = new Connection(0, 0, 1);
        Connection c1 = new Connection(1, 0, 2);
        Connection c2 = new Connection(2, 1, 3);
        Genome g = new Genome();
        g.addNodes(n0.clone(), n1.clone(), n2.clone(), n3.clone());
        g.addConnections(c0.clone(), c1.clone(), c2.clone());
        InnovationHistory h = new InnovationHistory(3, 2);
        Parameters p = new Parameters();
        Mutation.addConnectionMutation(g, 2, 1, h, p, ThreadLocalRandom.current());

        // test connections
        List<Connection> cons = g.getConnections().asOrderedList();
        assertEquals(4, cons.size());
        assertEquals(c0, cons.get(0));
        assertEquals(c1, cons.get(1));
        assertEquals(c2, cons.get(2));

        Connection newCon = cons.get(3);
        assertEquals(3, newCon.getInnovationNumber());
        assertEquals(2, newCon.getInNodeId());
        assertEquals(1, newCon.getOutNodeId());
        assertTrue(newCon.getWeight() <= 10);
        assertTrue(newCon.getWeight() >= -10);

        // test nodes
        NodeList nodes = g.getNodes();
        assertEquals(n0, nodes.get(0));
        assertEquals(n2, nodes.get(2));
        assertEquals(2, nodes.get(1).getLayer());
        assertEquals(3, nodes.get(3).getLayer());
    }

    /**
     * Recursive.
     */
    @Test
    public void addConnectionTest2 () {
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.OUTPUT, 2);
        Node n2 = new Node(2, Node.Type.HIDDEN, 1);
        Connection c0 = new Connection(0, 0, 2);
        Connection c1 = new Connection(1, 2, 1);
        Genome g = new Genome().addNodes(n0, n1, n2).addConnections(c0, c1);
        InnovationHistory h = new InnovationHistory(2, 1);
        Parameters p = new Parameters();

        Mutation.addConnectionMutation(g, 1, 2, h, p, ThreadLocalRandom.current());

        NodeList nodes = g.getNodes();
        assertEquals(0, nodes.get(0).getLayer());
        assertEquals(1, nodes.get(2).getLayer());
        assertEquals(2, nodes.get(1).getLayer());

        List<Connection> cons = g.getConnections().asOrderedList();
        assertEquals(3, cons.size());

        Connection newCon = cons.get(2);
        assertEquals(2, newCon.getInnovationNumber());
        assertEquals(1, newCon.getInNodeId());
        assertEquals(2, newCon.getOutNodeId());
        assertTrue(newCon.getWeight() <= 10);
        assertTrue(newCon.getWeight() >= -10);
    }

    /**
     * Layer fixing with recursive connections.
     */
    @Test
    public void addConnectionTest3 () {
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.HIDDEN, 1);
        Node n2 = new Node(2, Node.Type.HIDDEN, 1);
        Node n3 = new Node(3, Node.Type.OUTPUT, 2);
        Connection c0 = new Connection(0, 0, 1);
        Connection c1 = new Connection(1, 0, 2);
        Connection c2 = new Connection(2, 1, 3);
        Connection c3 = new Connection(3, 1, 1);
        Genome g = new Genome().addNodes(n0, n1, n2, n3).addConnections(c0, c1, c2, c3);
        InnovationHistory h = new InnovationHistory(3, 3);
        Parameters p = new Parameters();
        Mutation.addConnectionMutation(g, 2, 1, h, p, ThreadLocalRandom.current());

        NodeList nodes = g.getNodes();
        assertEquals(0, nodes.get(0).getLayer());
        assertEquals(1, nodes.get(2).getLayer());
        assertEquals(2, nodes.get(1).getLayer());
        assertEquals(3, nodes.get(3).getLayer());

        List<Connection> cons = g.getConnections().asOrderedList();

        Connection con0 = cons.get(0);
        assertEquals(0, con0.getInnovationNumber());
        assertEquals(0, con0.getInNodeId());
        assertEquals(1, con0.getOutNodeId());
        assertEquals(0.0, con0.getWeight());
        assertTrue(con0.isEnabled());

        Connection con1 = cons.get(1);
        assertEquals(1, con1.getInnovationNumber());
        assertEquals(0, con1.getInNodeId());
        assertEquals(2, con1.getOutNodeId());
        assertEquals(0.0, con1.getWeight());
        assertTrue(con1.isEnabled());

        Connection con2 = cons.get(2);
        assertEquals(2, con2.getInnovationNumber());
        assertEquals(1, con2.getInNodeId());
        assertEquals(3, con2.getOutNodeId());
        assertEquals(0.0, con2.getWeight());
        assertTrue(con2.isEnabled());

        Connection con3 = cons.get(3);
        assertEquals(3, con3.getInnovationNumber());
        assertEquals(1, con3.getInNodeId());
        assertEquals(1, con3.getOutNodeId());
        assertEquals(0.0, con3.getWeight());
        assertTrue(con3.isEnabled());

        Connection con4 = cons.get(4);
        assertEquals(4, con4.getInnovationNumber());
        assertEquals(2, con4.getInNodeId());
        assertEquals(1, con4.getOutNodeId());
        assertTrue(con4.getWeight() <= 10);
        assertTrue(con4.getWeight() >= -10);
        assertTrue(con4.isEnabled());
    }

    /**
     * Layer fixing that breaks order. In this test, without ordering of the nodeList the
     * resulting genome would be defective (the output would be wrong).
     */
    @Test
    public void addConnectionTest4 () {
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.HIDDEN, 1);
        Node n2 = new Node(2, Node.Type.HIDDEN, 1);
        Node n3 = new Node(3, Node.Type.OUTPUT, 2);
        Node n4 = new Node(4, Node.Type.OUTPUT, 2);
        Connection c0 = new Connection(0, 0, 1);
        Connection c1 = new Connection(1, 0, 2);
        Connection c2 = new Connection(2, 1, 3);
        Connection c3 = new Connection(3, 2, 4);
        Genome g = new Genome().addNodes(n0, n1, n2, n3, n4).addConnections(c0, c1, c2, c3);
        InnovationHistory h = new InnovationHistory(4, 3);
        Parameters p = new Parameters();

        Mutation.addConnectionMutation(g, 2, 1, h, p, ThreadLocalRandom.current());

        assertEquals("[" +
                "Node{id=0, type=INPUT, layer=0}, " +
                "Node{id=2, type=HIDDEN, layer=1}, " +
                "Node{id=1, type=HIDDEN, layer=2}, " +
                "Node{id=3, type=OUTPUT, layer=3}, " +
                "Node{id=4, type=OUTPUT, layer=3}" +
                "]", g.getNodes().toString());
    }

    /**
     * Progressive, only one option available.
     */
    @RepeatedTest(10)
    public void addConnectionTest5 () {
        Parameters params = new Parameters();
        params.recursiveConnectionProbability = 0;

        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.OUTPUT);
        Genome g = new Genome().addNodes(n0, n1);
        InnovationHistory h = new InnovationHistory(1, -1);

        assertTrue(Mutation.addConnectionMutation(g, h, params, ThreadLocalRandom.current()));

        List<Connection> cons = g.getConnections().asOrderedList();
        assertEquals(1, cons.size());

        Connection con = cons.get(0);
        assertEquals(0, con.getInnovationNumber());
        assertEquals(0, con.getInNodeId());
        assertEquals(1, con.getOutNodeId());
        assertTrue(con.getWeight() <= 10);
        assertTrue(con.getWeight() >= -10);

        assertFalse(Mutation.addConnectionMutation(g, h, params, ThreadLocalRandom.current()));
        assertEquals(1, cons.size());
    }

    /**
     * Recursive, only one option available.
     */
    @RepeatedTest(10)
    public void addConnectionTest6 () {
        Parameters params = new Parameters();
        params.recursiveConnectionProbability = 1.0;

        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.OUTPUT);
        Connection c0 = new Connection(0, 1, 0);
        Genome g = new Genome().addNodes(n0, n1).addConnections(c0);
        InnovationHistory h = new InnovationHistory(1, 0);

        assertTrue(Mutation.addConnectionMutation(g, h, params, ThreadLocalRandom.current()));

        List<Connection> cons = g.getConnections().asOrderedList();
        assertEquals(2, cons.size());

        Connection con = cons.get(1);
        assertEquals(1, con.getInnovationNumber());
        assertEquals(1, con.getInNodeId());
        assertEquals(1, con.getOutNodeId());
        assertTrue(con.getWeight() <= 10);
        assertTrue(con.getWeight() >= -10);

        assertFalse(Mutation.addConnectionMutation(g, h, params, ThreadLocalRandom.current()));
        assertEquals(2, cons.size());
    }

    /**
     * Recursive, only one option available.
     */
    @RepeatedTest(10)
    public void addConnectionTest7 () {
        Parameters params = new Parameters();
        params.recursiveConnectionProbability = 1.0;

        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.OUTPUT);
        Connection c0 = new Connection(0, 0, 0);
        Connection c1 = new Connection(1, 1, 1);
        Genome g = new Genome().addNodes(n0, n1).addConnections(c0, c1);
        InnovationHistory h = new InnovationHistory(1, 1);

        Mutation.addConnectionMutation(g, h, params, ThreadLocalRandom.current());

        List<Connection> cons = g.getConnections().asOrderedList();
        assertEquals(3, cons.size());

        Connection con = cons.get(2);
        assertEquals(2, con.getInnovationNumber());
        assertEquals(1, con.getInNodeId());
        assertEquals(0, con.getOutNodeId());
        assertTrue(con.getWeight() <= 10);
        assertTrue(con.getWeight() >= -10);

        assertFalse(Mutation.addConnectionMutation(g, h, params, ThreadLocalRandom.current()));
        assertEquals(3, cons.size());
    }

    /**
     * Recursive, no options available.
     */
    @RepeatedTest(10)
    public void addConnectionTest8 () {
        Parameters params = new Parameters();
        params.recursiveConnectionProbability = 1.0;

        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.OUTPUT);
        Connection c0 = new Connection(0, 1, 0);
        Connection c1 = new Connection(1, 1, 1);
        Genome g = new Genome().addNodes(n0, n1).addConnections(c0, c1);
        InnovationHistory h = new InnovationHistory(1, 1);

        assertFalse(Mutation.addConnectionMutation(g, h, params, ThreadLocalRandom.current()));

        List<Connection> cons = g.getConnections().asOrderedList();
        assertEquals(2, cons.size());
    }

    /**
     * Progressive, only one option with layer fixing.
     */
    @RepeatedTest(10)
    public void addConnectionTest9 () {
        Parameters params = new Parameters();
        params.recursiveConnectionProbability = 0.0;

        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.INPUT);
        Node n2 = new Node(2, Node.Type.HIDDEN, 1);
        Node n3 = new Node(3, Node.Type.HIDDEN, 1);
        Node n4 = new Node(4, Node.Type.OUTPUT, 2);
        Connection c0 = new Connection(0, 0, 2);
        Connection c1 = new Connection(1, 0, 3);
        Connection c2 = new Connection(2, 1, 2);
        Connection c3 = new Connection(3, 1, 3);
        Connection c4 = new Connection(4, 0, 4);
        Connection c5 = new Connection(5, 1, 4);
        Connection c6 = new Connection(6, 2, 4);
        Connection c7 = new Connection(7, 3, 4);
        Genome g = new Genome().addNodes(n0, n1, n2, n3, n4).addConnections(c0, c1, c2, c3, c4,
                c5, c6, c7);
        InnovationHistory h = new InnovationHistory(4, 7);

        // only possible progressive connections are either 2->1 or 1->2
        assertTrue(Mutation.addConnectionMutation(g, h, params, ThreadLocalRandom.current()));

        List<Connection> cons = g.getConnections().asOrderedList();
        assertEquals(9, cons.size());

        Connection con = cons.get(8);
        assertEquals(8, con.getInnovationNumber());
        int in = con.getInNodeId();
        int out = con.getOutNodeId();
        assertTrue((in == 2 && out == 3) || (in == 3 && out == 2));
        assertTrue(con.getWeight() <= 10);
        assertTrue(con.getWeight() >= -10);

        assertEquals(3, g.getNodes().get(4).getLayer());
        assertEquals(2, g.getNodes().get(out).getLayer());
        assertEquals(1, g.getNodes().get(in).getLayer());

        assertFalse(Mutation.addConnectionMutation(g, h, params, ThreadLocalRandom.current()));
        assertEquals(9, cons.size());
    }

    /**
     * Progressive, no options.
     */
    @RepeatedTest(10)
    public void addConnectionTest10 () {
        Parameters params = new Parameters();
        params.recursiveConnectionProbability = 0.0;
        InnovationHistory h = new InnovationHistory();
        GenomeBuilder gc = new GenomeBuilder(h).
                setFullyConnected(true)
                .setNumberOfNodes(3, 2);
        Genome g = gc.build();

        assertFalse(Mutation.addConnectionMutation(g, h, params, ThreadLocalRandom.current()));
        assertEquals(6, g.getConnections().size());
    }

    /**
     * Test the randomness of the selection of the nodes to connect.
     */
    @RepeatedTest(10)
    public void randomnessTest () {
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.INPUT);
        Node n2 = new Node(2, Node.Type.INPUT);
        Node n3 = new Node(3, Node.Type.OUTPUT);
        Node n4 = new Node(4, Node.Type.OUTPUT);
        Genome g = new Genome().addNodes(n0, n1, n2, n3, n4);
        InnovationHistory history = new InnovationHistory();
        history.setInitialHighestNodeId(4);
        assertEquals(0, history.getNewConnectionMutationInnovationNumber(0, 3));
        assertEquals(1, history.getNewConnectionMutationInnovationNumber(0, 4));
        assertEquals(2, history.getNewConnectionMutationInnovationNumber(1, 3));
        assertEquals(3, history.getNewConnectionMutationInnovationNumber(1, 4));
        assertEquals(4, history.getNewConnectionMutationInnovationNumber(2, 3));
        assertEquals(5, history.getNewConnectionMutationInnovationNumber(2, 4));

        Parameters p = new Parameters();
        p.recursiveConnectionProbability = 0;

        int c0Counter = 0;
        int c1Counter = 0;
        int c2Counter = 0;
        int c3Counter = 0;
        int c4Counter = 0;
        int c5Counter = 0;
        for (int i = 0; i < 6000; i++) {
            assertTrue(Mutation.addConnectionMutation(g.clone(), history, p, ThreadLocalRandom.current()));
            switch (history.getLastReturnedInnovationNumber()) {
                case 0 -> c0Counter++;
                case 1 -> c1Counter++;
                case 2 -> c2Counter++;
                case 3 -> c3Counter++;
                case 4 -> c4Counter++;
                case 5 -> c5Counter++;
                default -> fail();
            }
        }

        assertEquals(1000, c0Counter, 100);
        assertEquals(1000, c1Counter, 100);
        assertEquals(1000, c2Counter, 100);
        assertEquals(1000, c3Counter, 100);
        assertEquals(1000, c4Counter, 100);
        assertEquals(1000, c5Counter, 100);
    }
}
