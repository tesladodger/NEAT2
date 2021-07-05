package com.tesladodger.neat.evolution;

import com.tesladodger.neat.Connection;
import com.tesladodger.neat.Genome;
import com.tesladodger.neat.utils.InnovationHistory;
import com.tesladodger.neat.Node;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


@SuppressWarnings("TextBlockMigration")
public class NewNodeMutationTest {

    @Test
    @SuppressWarnings("TextBlockMigration")
    public void newNodeMutationTest0 () {
        // Create a genome with two nodes and a connection between them
        Node inputNode = new Node(0, Node.Type.INPUT, 0);
        Node outputNode = new Node(1, Node.Type.OUTPUT, 1);
        Genome genome = new Genome();
        genome.addNodes(inputNode, outputNode);
        Connection connection = new Connection(1, 0, 1);
        connection.setWeight(0.5);
        genome.addConnection(connection);

        InnovationHistory innovationHistory = new InnovationHistory(1, 1);

        Mutation.addNodeMutation(genome, innovationHistory, ThreadLocalRandom.current());

        Node[] nodes = genome.getNodes().asArray();
        assertEquals(3, nodes.length);
        assertEquals(inputNode, nodes[0]);
        assertEquals(outputNode, nodes[2]);
        assertEquals(2, nodes[1].getId());

        Node newNode = nodes[1];

        Connection[] connections = genome.getConnections().asArray();
        assertEquals(3, connections.length);

        // Check previous connection is disabled
        assertEquals(connection, connections[0]);
        assertFalse(connections[0].isEnabled());

        // Check connection from input to new node
        assertEquals(2, connections[1].getInnovationNumber());
        assertEquals(inputNode.getId(), connections[1].getInNodeId());
        assertEquals(newNode.getId(), connections[1].getOutNodeId());
        assertEquals(1, connections[1].getWeight());

        // Check connection from new node to output
        assertEquals(3, connections[2].getInnovationNumber());
        assertEquals(newNode.getId(), connections[2].getInNodeId());
        assertEquals(outputNode.getId(), connections[2].getOutNodeId());
        assertEquals(0.5, connections[2].getWeight());

        assertEquals("Genome{" +
                        "\n\tnodes=[" +
                        "Node{id=0, type=INPUT, layer=0}, " +
                        "Node{id=2, type=HIDDEN, layer=1}, " +
                        "Node{id=1, type=OUTPUT, layer=2}" +
                        "],\n\tconnections=[" +
                        "Connection{innovNum=1, in=0, out=1, weight=0.5, enabled=false}, " +
                        "Connection{innovNum=2, in=0, out=2, weight=1.0, enabled=true}, " +
                        "Connection{innovNum=3, in=2, out=1, weight=0.5, enabled=true}" +
                        "]\n}",
                genome.toString());
    }

    /**
     * Make sure a recursive connection is never disturbed.
     */
    @RepeatedTest(10)
    public void addNodeMutationNotRecursiveTest0 () {
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.OUTPUT);
        Connection c0 = new Connection(0, 0, 1, 0.5);
        Connection c1 = new Connection(1, 1, 0);
        Genome g = new Genome();
        g.addNodes(n0, n1);
        g.addConnections(c0, c1);
        InnovationHistory h = new InnovationHistory(1, 1);

        // connection c0 should always be broken, because c1 is recursive:
        Mutation.addNodeMutation(g, h, ThreadLocalRandom.current());
        assertEquals("Genome{\n" +
                "\tnodes=[" +
                "Node{id=0, type=INPUT, layer=0}, " +
                "Node{id=2, type=HIDDEN, layer=1}, " +
                "Node{id=1, type=OUTPUT, layer=2}],\n" +
                "\tconnections=[" +
                "Connection{innovNum=0, in=0, out=1, weight=0.5, enabled=false}, " +
                "Connection{innovNum=1, in=1, out=0, weight=0.0, enabled=true}, " +
                "Connection{innovNum=2, in=0, out=2, weight=1.0, enabled=true}, " +
                "Connection{innovNum=3, in=2, out=1, weight=0.5, enabled=true}]\n" +
                "}", g.toString());
    }

    /**
     * Genome riddled with recursive connections, only one forward.
     */
    @RepeatedTest(10)
    public void addNodeMutationNotRecursiveTest1 () {
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.OUTPUT, 3);
        Node n2 = new Node(2, Node.Type.HIDDEN, 2);
        Node n3 = new Node(3, Node.Type.HIDDEN, 1);
        Node n4 = new Node(4, Node.Type.HIDDEN, 2);
        Connection c0 = new Connection(0, 2, 3);
        Connection c1 = new Connection(1, 3, 0);
        Connection c2 = new Connection(2, 0, 1); // this one
        Connection c3 = new Connection(3, 4, 0);
        Connection c4 = new Connection(4, 1, 2);
        Connection c5 = new Connection(5, 4, 4);
        Genome g = new Genome();
        g.addNodes(n0, n1, n2, n3, n4);
        g.addConnections(c0, c1, c2, c3, c4, c5);
        InnovationHistory h = new InnovationHistory(4, 5);

        Mutation.addNodeMutation(g, h, ThreadLocalRandom.current());
        assertEquals("Genome{\n" +
                "\tnodes=[" +
                "Node{id=0, type=INPUT, layer=0}, " +
                "Node{id=3, type=HIDDEN, layer=1}, " +
                "Node{id=5, type=HIDDEN, layer=1}, " +
                "Node{id=2, type=HIDDEN, layer=2}, " +
                "Node{id=4, type=HIDDEN, layer=2}, " +
                "Node{id=1, type=OUTPUT, layer=3}],\n" +
                "\tconnections=[" +
                "Connection{innovNum=0, in=2, out=3, weight=0.0, enabled=true}, " +
                "Connection{innovNum=1, in=3, out=0, weight=0.0, enabled=true}, " +
                "Connection{innovNum=2, in=0, out=1, weight=0.0, enabled=false}, " +
                "Connection{innovNum=3, in=4, out=0, weight=0.0, enabled=true}, " +
                "Connection{innovNum=4, in=1, out=2, weight=0.0, enabled=true}, " +
                "Connection{innovNum=5, in=4, out=4, weight=0.0, enabled=true}, " +
                "Connection{innovNum=6, in=0, out=5, weight=1.0, enabled=true}, " +
                "Connection{innovNum=7, in=5, out=1, weight=0.0, enabled=true}]\n" +
                "}", g.toString());
    }

    @RepeatedTest(10)
    public void noAvailableConnectionsTest0 () {
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.INPUT);
        Node n2 = new Node(2, Node.Type.OUTPUT);
        Connection c0 = new Connection(0, 2, 0);
        Connection c1 = new Connection(1, 2, 1);
        Connection c2 = new Connection(2, 2, 2);
        Genome g = new Genome().addNodes(n0, n1, n2).addConnections(c0, c1, c2);
        InnovationHistory h = new InnovationHistory(2, 2);

        assertFalse(Mutation.addNodeMutation(g, h, ThreadLocalRandom.current()));
        assertEquals(3, g.getNodes().size());
    }

    /**
     * Test the randomness of the selection of the connection to mutate.
     */
    @RepeatedTest(10)
    public void randomnessTest0 () {
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.INPUT);
        Node n2 = new Node(2, Node.Type.INPUT);
        Node n3 = new Node(3, Node.Type.OUTPUT);
        Node n4 = new Node(4, Node.Type.OUTPUT);
        Connection c0 = new Connection(0, 0, 3);
        Connection c1 = new Connection(1, 0, 4);
        Connection c2 = new Connection(2, 1, 3);
        Connection c3 = new Connection(3, 1, 4);
        Connection c4 = new Connection(4, 2, 3);
        Connection c5 = new Connection(5, 2, 4);
        Genome g = new Genome().addNodes(n0, n1, n2, n3, n4).addConnections(c0, c1, c2, c3, c4, c5);
        InnovationHistory history = new InnovationHistory(4, 5);
        assertEquals(5, history.getNewNodeMutationId(0));
        assertEquals(6, history.getNewNodeMutationId(1));
        assertEquals(7, history.getNewNodeMutationId(2));
        assertEquals(8, history.getNewNodeMutationId(3));
        assertEquals(9, history.getNewNodeMutationId(4));
        assertEquals(10, history.getNewNodeMutationId(5));

        int c0Counter = 0;
        int c1Counter = 0;
        int c2Counter = 0;
        int c3Counter = 0;
        int c4Counter = 0;
        int c5Counter = 0;
        for (int i = 0; i < 6000; i++) {
            assertTrue(Mutation.addNodeMutation(g.clone(), history, ThreadLocalRandom.current()));
            switch (history.getLastReturnedNodeId()) {
                case 5 -> c0Counter++;
                case 6 -> c1Counter++;
                case 7 -> c2Counter++;
                case 8 -> c3Counter++;
                case 9 -> c4Counter++;
                case 10 -> c5Counter++;
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

    @Test
    public void nodeListOrderFixTest () {
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.OUTPUT);
        Node n2 = new Node(2, Node.Type.OUTPUT);
        Connection c0 = new Connection(0, 0, 1);
        Connection c1 = new Connection(1, 0, 2);
        Genome g = new Genome().addNodes(n0, n1, n2).addConnections(c0, c1);

        Mutation.addNodeMutation(g, c1, new InnovationHistory(2, 1));

        Node[] nodes = g.getNodes().asArray();
        assertEquals(4, nodes.length);

        assertEquals(0, nodes[0].getId());
        assertEquals(0, nodes[0].getLayer());
        assertEquals(3, nodes[1].getId());
        assertEquals(1, nodes[1].getLayer());
        assertEquals(1, nodes[2].getId());
        assertEquals(2, nodes[2].getLayer());
        assertEquals(2, nodes[3].getId());
        assertEquals(2, nodes[3].getLayer());

        assertEquals("Genome{\n" +
                "\tnodes=[" +
                "Node{id=0, type=INPUT, layer=0}, " +
                "Node{id=3, type=HIDDEN, layer=1}, " +
                "Node{id=1, type=OUTPUT, layer=2}, " +
                "Node{id=2, type=OUTPUT, layer=2}],\n" +
                "\tconnections=[" +
                "Connection{innovNum=0, in=0, out=1, weight=0.0, enabled=true}, " +
                "Connection{innovNum=1, in=0, out=2, weight=0.0, enabled=false}, " +
                "Connection{innovNum=2, in=0, out=3, weight=1.0, enabled=true}, " +
                "Connection{innovNum=3, in=3, out=2, weight=0.0, enabled=true}]\n" +
                "}", g.toString());
    }
}
