package com.tesladodger.neat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ConnectionTest {

    @Test
    public void conConstructorTest () {
        Connection con = new Connection(1, 2, 3);
        assertEquals(1, con.getInnovationNumber());
        assertEquals(2, con.getInNodeId());
        assertEquals(3, con.getOutNodeId());
        assertEquals(0.0, con.getWeight());
        assertTrue(con.isEnabled());
        assertEquals("Connection{" +
                "innovNum=1, " +
                "in=2, " +
                "out=3, " +
                "weight=0.0, " +
                "enabled=true}", con.toString());

        Connection con2 = new Connection(2, 3, 4, 0.5);
        assertEquals(2, con2.getInnovationNumber());
        assertEquals(3, con2.getInNodeId());
        assertEquals(4, con2.getOutNodeId());
        assertEquals(0.5, con2.getWeight());
        assertTrue(con2.isEnabled());
        assertEquals("Connection{" +
                "innovNum=2, " +
                "in=3, " +
                "out=4, " +
                "weight=0.5, " +
                "enabled=true}", con2.toString());

        Connection con3 = new Connection(3, 4, 5, 0.2, false);
        assertEquals(3, con3.getInnovationNumber());
        assertEquals(4, con3.getInNodeId());
        assertEquals(5, con3.getOutNodeId());
        assertEquals(0.2, con3.getWeight());
        assertFalse(con3.isEnabled());
        assertEquals("Connection{" +
                "innovNum=3, " +
                "in=4, " +
                "out=5, " +
                "weight=0.2, " +
                "enabled=false}", con3.toString());
    }

    @Test
    public void conEqualsTest () {
        Connection c0 = new Connection(0, 1, 2);

        Connection c1 = new Connection(0, 1, 2);
        assertEquals(c0, c1);

        Connection c2 = new Connection(1, 1, 2);
        assertNotEquals(c0, c2);

        Connection c3 = new Connection(0, 3, 2);
        assertNotEquals(c0, c3);

        Connection c4 = new Connection(0, 1, 4);
        assertNotSame(c0, c4);

        c1.setWeight(-1);
        assertNotEquals(c0, c1);

        Connection c5 = new Connection(0, 1, 2, 0.0, false);
        assertNotEquals(c0, c5);
    }

    @Test
    public void conCloneTest () {
        Connection con = new Connection(2, 0, 1);
        assertEquals("Connection{innovNum=2, in=0, out=1, weight=0.0, " +
                "enabled=true}", con.toString());

        Connection clone = con.clone();
        assertEquals(con, clone);
        assertNotSame(con, clone);
    }
}
