package com.tesladodger.neat;

import com.tesladodger.neat.utils.functions.SigmoidActivationFunction;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class NodeTest {

    @Test
    public void nodeConstructorTest () {
        Node node = new Node(2, Node.Type.INPUT);
        assertEquals(0, node.getLayer());
        assertEquals(2, node.getId());
        assertEquals(Node.Type.INPUT, node.getType());
        assertEquals("Node{id=2, type=INPUT, layer=0}", node.toString());
    }

    @Test
    public void nodeEqualsTest () {
        Node n1 = new Node(0, Node.Type.HIDDEN);
        Node n2 = new Node(0, Node.Type.HIDDEN);
        assertEquals(n2, n1);
        n1.addInput(2);
        assertEquals(n2, n1);
        assertNotSame(n2, n1);
        Node n3 = new Node(1, Node.Type.HIDDEN);
        assertNotEquals(n1, n3);
        Node n4 = new Node(1, Node.Type.HIDDEN, 2);
        assertNotEquals(n3, n4);
        assertSame(n4, n4);
    }

    @Test
    public void compareToTest () {
        Node n0 = new Node(1, Node.Type.INPUT);
        Node n1 = new Node(2, Node.Type.INPUT);

        assertTrue(n0.compareTo(n1) > 0);
        assertTrue(n1.compareTo(n0) < 0);

        Node n2 = n0.clone();
        assertEquals(0, n0.compareTo(n2));
        assertEquals(0, n2.compareTo(n0));

        Node n3 = new Node(0, Node.Type.OUTPUT);
        assertTrue(n3.compareTo(n0) < 0);
        assertTrue(n3.compareTo(n1) < 0);
        assertTrue(n3.compareTo(n2) < 0);
        assertTrue(n0.compareTo(n3) > 0);
        assertTrue(n1.compareTo(n3) > 0);
        assertTrue(n2.compareTo(n3) > 0);
    }

    @Test
    public void nodeCloneTest () {
        Node node = new Node(0, Node.Type.INPUT);
        Node clone = node.clone();
        assertEquals(node, clone);
        assertNotSame(node, clone);
    }

    @Test
    public void nodeOutputTest0 () {
        Node node = new Node(0, Node.Type.HIDDEN);
        node.addInput(0.2);
        SigmoidActivationFunction f = new SigmoidActivationFunction();
        f.logisticGrowthRate = 4.9;
        f.offset = 0.0;
        assertEquals(0.7271, node.getOutput(f), 0.0001);

        node.reset();
        assertEquals(0.5, node.getOutput(f));
    }

    @RepeatedTest(20)
    public void nodeOutputTest1 () {
        double r = ThreadLocalRandom.current().nextDouble() * 20 - 10;
        Node n = new Node(0, Node.Type.INPUT);
        n.addInput(r);
        assertEquals(r, n.getOutput((x) -> x));
    }
}
