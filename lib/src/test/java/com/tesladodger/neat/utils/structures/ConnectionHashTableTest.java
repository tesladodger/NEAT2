package com.tesladodger.neat.utils.structures;

import com.tesladodger.neat.Connection;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static com.tesladodger.neat.utils.structures.ConnectionHashTable.ConnectionBucket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ConnectionHashTableTest {

    @Test
    public void testAddException () {
        Connection cFail = new Connection(0, -1, 0);
        assertThrows(IllegalArgumentException.class,
                () -> new ConnectionHashTable().addConnection(cFail));
    }

    @Test
    public void testGet () {
        Connection c1 = new Connection(1, 0, 3);
        Connection c2 = new Connection(2, 0, 4);
        Connection c3 = new Connection(3, 4, 3);
        Connection c4 = new Connection(4, 1, 4);
        Connection c5 = new Connection(5, 2, 5);
        Connection c6 = new Connection(6, 5, 3);

        ConnectionHashTable connections = new ConnectionHashTable(10);
        connections.addConnections(c1, c2, c3, c4, c5, c6);

        assertEquals(6, connections.size());

        ConnectionBucket consFromN0 = (ConnectionBucket) connections.getConnectionsFrom(0);
        assertEquals(2, consFromN0.size);
        Iterator<Connection> it = consFromN0.iterator();
        Connection c = it.next();
        assertEquals(0, c.getInNodeId());
        assertEquals(4, c.getOutNodeId());
        c = it.next();
        assertEquals(0, c.getInNodeId());
        assertEquals(3, c.getOutNodeId());
        assertFalse(it.hasNext());

        ConnectionBucket consFromN1 = (ConnectionBucket) connections.getConnectionsFrom(1);
        assertEquals(1, consFromN1.size);
        it = consFromN1.iterator();
        c = it.next();
        assertEquals(1, c.getInNodeId());
        assertEquals(4, c.getOutNodeId());
        assertFalse(it.hasNext());

        ConnectionBucket consFromN2 = (ConnectionBucket) connections.getConnectionsFrom(2);
        assertEquals(1, consFromN2.size);
        it = consFromN2.iterator();
        c = it.next();
        assertEquals(2, c.getInNodeId());
        assertEquals(5, c.getOutNodeId());

        Connection[] conArray = connections.asArray();
        assertEquals(connections.size(), conArray.length);

        assertSame(c1, conArray[0]);
        assertSame(c2, conArray[1]);
        assertSame(c3, conArray[2]);
        assertSame(c4, conArray[3]);
        assertSame(c5, conArray[4]);
        assertSame(c6, conArray[5]);
    }

    @Test
    public void testContains () {
        Connection c1 = new Connection(1, 0, 3);
        Connection c2 = new Connection(2, 0, 4);
        Connection c3 = new Connection(3, 4, 3);
        Connection c4 = new Connection(4, 1, 4);
        Connection c5 = new Connection(5, 2, 5);
        Connection c6 = new Connection(6, 5, 3);
        ConnectionHashTable connections = new ConnectionHashTable(10);
        connections.addConnections(c1, c2, c3, c4, c5, c6);

        assertTrue(connections.containsConnection(0, 3));
        assertFalse(connections.containsConnection(3, 0));
        assertTrue(connections.containsConnection(5, 3));
        assertFalse(connections.containsConnection(5, 2));
        assertTrue(connections.containsConnection(4, 3));
        assertFalse(connections.containsConnection(4, 4));
    }

    @Test
    public void testOrderedList () {
        Connection c1 = new Connection(1, 0, 3);
        Connection c2 = new Connection(2, 0, 4);
        Connection c3 = new Connection(3, 4, 3);
        Connection c4 = new Connection(4, 1, 4);
        Connection c5 = new Connection(5, 2, 5);
        Connection c6 = new Connection(6, 5, 3);

        ConnectionHashTable table = new ConnectionHashTable(6);
        table.addConnections(c5, c3, c6, c1, c2, c4);

        List<Connection> orderedList = table.asOrderedList();
        int in = 1;
        for (Connection con : orderedList) {
            assertEquals(in++, con.getInnovationNumber());
        }
    }

    @Test
    public void testUnmodifiableList () {
        Connection c1 = new Connection(1, 0, 3);
        Connection c2 = new Connection(2, 0, 4);
        Connection c3 = new Connection(3, 4, 3);
        Connection c4 = new Connection(4, 1, 4);
        Connection c5 = new Connection(5, 2, 5);
        Connection c6 = new Connection(6, 5, 3);

        ConnectionHashTable table = new ConnectionHashTable(6);
        table.addConnections(c5, c3, c6, c1, c2, c4);

        List<Connection> orderedList = table.asOrderedList();
        assertThrows(UnsupportedOperationException.class,
                () -> orderedList.add(new Connection(0, 0, 0)));
        assertThrows(UnsupportedOperationException.class,
                () -> orderedList.set(2, new Connection(0, 0, 0)));
    }

    @Test
    public void testIncreaseCapacity () {
        Connection c1 = new Connection(1, 0, 3);
        Connection c2 = new Connection(2, 1, 4);
        Connection c3 = new Connection(3, 2, 3);
        Connection c4 = new Connection(4, 3, 4);
        Connection c5 = new Connection(5, 4, 5);
        Connection c6 = new Connection(6, 5, 3);

        ConnectionHashTable table = new ConnectionHashTable();
        table.addConnections(c1, c2, c3, c4, c5, c6);

        Connection c7 = new Connection(7, 100, 8);
        table.addConnection(c7);

        assertEquals(140, table.capacity());
        assertEquals(7, table.size());

        Connection[] cons = table.asArray();
        assertEquals(7, cons.length);
        assertSame(c1, cons[0]);
        assertSame(c2, cons[1]);
        assertSame(c3, cons[2]);
        assertSame(c4, cons[3]);
        assertSame(c5, cons[4]);
        assertSame(c6, cons[5]);
        assertSame(c7, cons[6]);
    }
}
