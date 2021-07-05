package com.tesladodger.neat.utils;

import com.tesladodger.neat.Connection;
import com.tesladodger.neat.Genome;
import com.tesladodger.neat.Node;
import com.tesladodger.neat.evolution.Mutation;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class InnovationHistoryTest {

    @Test
    public void newNodeMutationTest () {
        InnovationHistory innovationHistory = new InnovationHistory();
        innovationHistory.setInitialHighestNodeId(0);
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);

        // Break up connection with id = 0
        assertEquals(1, innovationHistory.getNewNodeMutationId(0));
        assertEquals(1, innovationHistory.getLastReturnedNodeId());
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);

        // Break up connection with id = 3
        assertEquals(2, innovationHistory.getNewNodeMutationId(3));
        assertEquals(2, innovationHistory.getLastReturnedNodeId());
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);

        // Break up connection with id = 6
        assertEquals(3, innovationHistory.getNewNodeMutationId(6));
        assertEquals(3, innovationHistory.getLastReturnedNodeId());
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);

        // Break up connection with id = 0, should return 1
        assertEquals(1, innovationHistory.getNewNodeMutationId(0));
        assertEquals(1, innovationHistory.getLastReturnedNodeId());
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);

        // Break up connection with id = 2
        assertEquals(4, innovationHistory.getNewNodeMutationId(2));
        assertEquals(4, innovationHistory.getLastReturnedNodeId());
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);

        // Break up connection with id = 3, should return 2
        assertEquals(2, innovationHistory.getNewNodeMutationId(3));
        assertEquals(2, innovationHistory.getLastReturnedNodeId());
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);

        // Break up connection with id = 14
        assertEquals(5, innovationHistory.getNewNodeMutationId(14));
        assertEquals(5, innovationHistory.getLastReturnedNodeId());
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);

        // Break up connection with id = 3, should return 2
        assertEquals(2, innovationHistory.getNewNodeMutationId(3));
        assertEquals(2, innovationHistory.getLastReturnedNodeId());
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);

        innovationHistory.reset();
        assertEquals(2, innovationHistory.getLastReturnedNodeId());
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);

        assertEquals(0, innovationHistory.getNewNodeMutationId(0));
        assertEquals(0, innovationHistory.getLastReturnedNodeId());
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);
        assertEquals(1, innovationHistory.getNewNodeMutationId(3));
        assertEquals(1, innovationHistory.getLastReturnedNodeId());
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);
        assertEquals(2, innovationHistory.getNewNodeMutationId(6));
        assertEquals(2, innovationHistory.getLastReturnedNodeId());
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);
        assertEquals(0, innovationHistory.getNewNodeMutationId(0));
        assertEquals(0, innovationHistory.getLastReturnedNodeId());
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);
        assertEquals(3, innovationHistory.getNewNodeMutationId(2));
        assertEquals(3, innovationHistory.getLastReturnedNodeId());
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);
        assertEquals(1, innovationHistory.getNewNodeMutationId(3));
        assertEquals(1, innovationHistory.getLastReturnedNodeId());
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);
        assertEquals(4, innovationHistory.getNewNodeMutationId(14));
        assertEquals(4, innovationHistory.getLastReturnedNodeId());
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);
        assertEquals(1, innovationHistory.getNewNodeMutationId(3));
        assertEquals(1, innovationHistory.getLastReturnedNodeId());
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);
    }

    @Test
    public void newConnectionMutationTest () {
        InnovationHistory innovationHistory = new InnovationHistory();
        innovationHistory.setInitialHighestInnovationNumber(0);
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedInnovationNumber);

        // connect node 0 to 1
        assertEquals(1, innovationHistory.getNewConnectionMutationInnovationNumber(0, 1));
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(1, innovationHistory.getLastReturnedInnovationNumber());

        // connect node 2 to 1
        assertEquals(2, innovationHistory.getNewConnectionMutationInnovationNumber(2, 1));
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(2, innovationHistory.getLastReturnedInnovationNumber());

        // connect node 2 to 3
        assertEquals(3, innovationHistory.getNewConnectionMutationInnovationNumber(2, 3));
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(3, innovationHistory.getLastReturnedInnovationNumber());

        // connect node 0 to 1, should be 1
        assertEquals(1, innovationHistory.getNewConnectionMutationInnovationNumber(0, 1));
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(1, innovationHistory.getLastReturnedInnovationNumber());

        // connect node 1 to 0
        assertEquals(4, innovationHistory.getNewConnectionMutationInnovationNumber(1, 0));
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(4, innovationHistory.getLastReturnedInnovationNumber());

        // connect node 2 to 3, should be 3
        assertEquals(3, innovationHistory.getNewConnectionMutationInnovationNumber(2, 3));
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(3, innovationHistory.getLastReturnedInnovationNumber());

        // connect node 2 to 1, should be 2
        assertEquals(2, innovationHistory.getNewConnectionMutationInnovationNumber(2, 1));
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(2, innovationHistory.getLastReturnedInnovationNumber());

        // connect node 1 to 38, which collides with 2 to 1
        assertEquals(5, innovationHistory.getNewConnectionMutationInnovationNumber(1, 38));
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(5, innovationHistory.getLastReturnedInnovationNumber());

        // connect node 2 to 1, should be 2
        assertEquals(2, innovationHistory.getNewConnectionMutationInnovationNumber(2, 1));
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(2, innovationHistory.getLastReturnedInnovationNumber());


        innovationHistory.reset();
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(2, innovationHistory.getLastReturnedInnovationNumber());

        assertEquals(0, innovationHistory.getNewConnectionMutationInnovationNumber(0, 1));
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(0, innovationHistory.getLastReturnedInnovationNumber());

        assertEquals(1, innovationHistory.getNewConnectionMutationInnovationNumber(2, 1));
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(1, innovationHistory.getLastReturnedInnovationNumber());

        assertEquals(2, innovationHistory.getNewConnectionMutationInnovationNumber(2, 3));
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(2, innovationHistory.getLastReturnedInnovationNumber());

        assertEquals(0, innovationHistory.getNewConnectionMutationInnovationNumber(0, 1));
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(0, innovationHistory.getLastReturnedInnovationNumber());

        assertEquals(3, innovationHistory.getNewConnectionMutationInnovationNumber(1, 0));
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(3, innovationHistory.getLastReturnedInnovationNumber());

        assertEquals(2, innovationHistory.getNewConnectionMutationInnovationNumber(2, 3));
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(2, innovationHistory.getLastReturnedInnovationNumber());

        assertEquals(1, innovationHistory.getNewConnectionMutationInnovationNumber(2, 1));
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(1, innovationHistory.getLastReturnedInnovationNumber());

        assertEquals(4, innovationHistory.getNewConnectionMutationInnovationNumber(1, 38));
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(4, innovationHistory.getLastReturnedInnovationNumber());

        assertEquals(1, innovationHistory.getNewConnectionMutationInnovationNumber(2, 1));
        assertThrows(IllegalStateException.class, innovationHistory::getLastReturnedNodeId);
        assertEquals(1, innovationHistory.getLastReturnedInnovationNumber());
    }

    /**
     * Test repeatability of genome mutation.
     */
    @Test
    public void genomeMutationTest0 () {
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.OUTPUT);
        Connection c0 = new Connection(0, 0, 1);
        Genome g = new Genome().addNodes(n0, n1).addConnections(c0);
        InnovationHistory h = new InnovationHistory(1, 0);
        for (int i = 0; i < 10; i++) {
            Genome clone = g.clone();
            Mutation.addNodeMutation(clone, h, ThreadLocalRandom.current());

            Node[] nodes = clone.getNodes().asArray();
            assertEquals(3, nodes.length);
            assertEquals(0, nodes[0].getId());
            assertEquals(0, nodes[0].getLayer());
            assertEquals(2, nodes[1].getId());
            assertEquals(1, nodes[1].getLayer());
            assertEquals(1, nodes[2].getId());
            assertEquals(2, nodes[2].getLayer());

            List<Connection> cons = clone.getConnections().asOrderedList();
            assertEquals(3, cons.size());
            assertEquals(0, cons.get(0).getInnovationNumber());
            assertEquals(0, cons.get(0).getInNodeId());
            assertEquals(1, cons.get(0).getOutNodeId());
            assertFalse(cons.get(0).isEnabled());
            assertEquals(1, cons.get(1).getInnovationNumber());
            assertEquals(0, cons.get(1).getInNodeId());
            assertEquals(2, cons.get(1).getOutNodeId());
            assertTrue(cons.get(1).isEnabled());
            assertEquals(2, cons.get(2).getInnovationNumber());
            assertEquals(2, cons.get(2).getInNodeId());
            assertEquals(1, cons.get(2).getOutNodeId());
            assertTrue(cons.get(2).isEnabled());

            Mutation.addNodeMutation(clone, cons.get(1), h);

            nodes = clone.getNodes().asArray();
            assertEquals(4, nodes.length);
            assertEquals(0, nodes[0].getId());
            assertEquals(0, nodes[0].getLayer());
            assertEquals(3, nodes[1].getId());
            assertEquals(1, nodes[1].getLayer());
            assertEquals(2, nodes[2].getId());
            assertEquals(2, nodes[2].getLayer());
            assertEquals(1, nodes[3].getId());
            assertEquals(3, nodes[3].getLayer());

            assertEquals(5, cons.size());
            assertEquals(0, cons.get(0).getInnovationNumber());
            assertEquals(0, cons.get(0).getInNodeId());
            assertEquals(1, cons.get(0).getOutNodeId());
            assertFalse(cons.get(0).isEnabled());
            assertEquals(1, cons.get(1).getInnovationNumber());
            assertEquals(0, cons.get(1).getInNodeId());
            assertEquals(2, cons.get(1).getOutNodeId());
            assertFalse(cons.get(1).isEnabled());
            assertEquals(2, cons.get(2).getInnovationNumber());
            assertEquals(2, cons.get(2).getInNodeId());
            assertEquals(1, cons.get(2).getOutNodeId());
            assertTrue(cons.get(2).isEnabled());
            assertEquals(3, cons.get(3).getInnovationNumber());
            assertEquals(0, cons.get(3).getInNodeId());
            assertEquals(3, cons.get(3).getOutNodeId());
            assertTrue(cons.get(3).isEnabled());
            assertEquals(4, cons.get(4).getInnovationNumber());
            assertEquals(3, cons.get(4).getInNodeId());
            assertEquals(2, cons.get(4).getOutNodeId());
            assertTrue(cons.get(4).isEnabled());
        }
    }

    @Test
    public void connectionAgeTest () {
        InnovationHistory history = new InnovationHistory();
        assertEquals(0, history.getConnectionAge(0, 0));
        assertEquals(0, history.getConnectionAge(4, 5));

        history.getNewConnectionMutationInnovationNumber(0, 0);
        assertEquals(0, history.getConnectionAge(0, 0));

        history.incrementConnectionAges();
        assertEquals(1, history.getConnectionAge(0, 0));

        assertEquals(1, history.getConnectionAge(4, 5));
        history.getNewConnectionMutationInnovationNumber(4, 5);
        assertEquals(0, history.getConnectionAge(4, 5));

        history.incrementConnectionAges();
        assertEquals(2, history.getConnectionAge(0, 0));
        assertEquals(1, history.getConnectionAge(4, 5));
        assertEquals(2, history.getConnectionAge(6, 9));
    }
}
