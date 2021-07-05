package com.tesladodger.neat;

import com.tesladodger.neat.utils.functions.StepActivationFunction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class CloningTest {

    @Test
    @DisplayName("Node cloning test: independence")
    public void nodeCloningTest0 () {
        Node node = new Node(1, Node.Type.INPUT, 0);
        Node clone = node.clone();

        assertEquals(clone, node);
        assertNotSame(node, clone);
    }

    @Test
    @DisplayName("Node cloning test: feed-forward")
    public void nodeCloningTest1 () {
        Node node = new Node(3, Node.Type.HIDDEN, 4);
        Node clone = node.clone();
        node.addInput(1);
        StepActivationFunction f = new StepActivationFunction();
        f.offset = .5;
        assertEquals(1, node.getOutput(f));
        assertEquals(0, clone.getOutput(f));
        assertEquals(node, clone);
        assertNotSame(node, clone);
    }

    @Test
    @DisplayName("Connection cloning test: independence")
    public void connectionCloningTest () {
        Connection connection = new Connection(1, 0, 1);
        connection.setWeight(.5);
        Connection clone = connection.clone();

        assertEquals(clone, connection);
        assertNotSame(connection, clone);

        // Assert independence
        clone.setWeight(0);
        assertNotEquals(clone, connection);
    }

    @Test
    @DisplayName("Genome cloning test: independence")
    public void genomeCloningTest () {
        Node n1 = new Node(1, Node.Type.INPUT, 0);
        Node n2 = new Node(2, Node.Type.INPUT, 0);
        Node n3 = new Node(3, Node.Type.INPUT, 0);
        Node n4 = new Node(4, Node.Type.OUTPUT, 0);
        Node n5 = new Node(5, Node.Type.HIDDEN, 0);

        Connection c1 = new Connection(1, 1, 4);
        Connection c2 = new Connection(2, 2, 4);
        Connection c3 = new Connection(3, 3, 4);
        Connection c4 = new Connection(4, 2, 5);
        Connection c5 = new Connection(5, 5, 4);
        Connection c6 = new Connection(6, 1, 5);

        Genome genome = new Genome();
        genome.addNodes(n1, n2, n3, n4, n5);
        genome.addConnections(c1, c2, c3, c4, c5, c6);

        Genome clone = genome.clone();
        assertEquals(clone, genome);
        assertNotSame(genome, clone);
        assertNotSame(genome.getNodes(), clone.getNodes());
        assertNotSame(genome.getConnections(), clone.getConnections());

        Connection con0 = genome.getConnections().getConnectionsFrom(5).iterator().next();
        Connection con1 = clone.getConnections().getConnectionsFrom(5).iterator().next();
        con0.setWeight(1);
        assertEquals(1, con0.getWeight());
        assertEquals(0, con1.getWeight());
        assertNotEquals(genome, clone);
    }
}
