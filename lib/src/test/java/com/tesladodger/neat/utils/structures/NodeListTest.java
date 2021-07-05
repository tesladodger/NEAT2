package com.tesladodger.neat.utils.structures;

import com.tesladodger.neat.Node;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class NodeListTest {

    @Test
    public void addTest () {
        Node n1 = new Node(1, Node.Type.INPUT);
        Node n2 = new Node(2, Node.Type.INPUT);
        Node n3 = new Node(3, Node.Type.INPUT);
        Node n4 = new Node(4, Node.Type.OUTPUT, 2);

        NodeList list = new NodeList();
        list.add(n4);
        list.add(n3);
        list.add(n1);
        list.add(n2);

        assertEquals(
                "[" +
                        "Node{id=1, type=INPUT, layer=0}, " +
                        "Node{id=2, type=INPUT, layer=0}, " +
                        "Node{id=3, type=INPUT, layer=0}, " +
                        "Node{id=4, type=OUTPUT, layer=2}" +
                        "]",
                list.toString()
        );

        list.add(new Node(5, Node.Type.HIDDEN, 1));
        assertEquals(
                "[" +
                        "Node{id=1, type=INPUT, layer=0}, " +
                        "Node{id=2, type=INPUT, layer=0}, " +
                        "Node{id=3, type=INPUT, layer=0}, " +
                        "Node{id=5, type=HIDDEN, layer=1}, " +
                        "Node{id=4, type=OUTPUT, layer=2}" +
                        "]",
                list.toString()
        );
    }

    @Test
    public void clearTest () {
        Node n1 = new Node(1, Node.Type.INPUT);
        Node n2 = new Node(2, Node.Type.INPUT);
        Node n3 = new Node(3, Node.Type.INPUT);
        Node n4 = new Node(4, Node.Type.OUTPUT, 2);
        NodeList list = new NodeList();
        list.add(n1);
        list.add(n2);
        list.add(n3);
        list.add(n4);

        list.clear();
        assertEquals(0, list.size());
        assertEquals("[]", list.toString());

        list.add(n1);
        list.add(n2);
        list.add(n3);
        list.add(n4);
        assertEquals(4, list.size());
        assertEquals(
                "[" +
                        "Node{id=1, type=INPUT, layer=0}, " +
                        "Node{id=2, type=INPUT, layer=0}, " +
                        "Node{id=3, type=INPUT, layer=0}, " +
                        "Node{id=4, type=OUTPUT, layer=2}" +
                        "]",
                list.toString()
        );
    }

    @Test
    public void sortTest () {
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.INPUT, 1);
        Node n2 = new Node(2, Node.Type.INPUT, 2);
        Node n3 = new Node(3, Node.Type.INPUT, 3);

        NodeList list = new NodeList();
        list.add(n0);
        list.add(n1);
        list.add(n2);
        list.add(n3);

        n0.setLayer(3);
        n1.setLayer(2);
        n2.setLayer(1);
        n3.setLayer(0);

        assertEquals("[" +
                "Node{id=0, type=INPUT, layer=3}, " +
                "Node{id=1, type=INPUT, layer=2}, " +
                "Node{id=2, type=INPUT, layer=1}, " +
                "Node{id=3, type=INPUT, layer=0}" +
                "]", list.toString());

        list.sort();
        assertEquals("[" +
                "Node{id=3, type=INPUT, layer=0}, " +
                "Node{id=2, type=INPUT, layer=1}, " +
                "Node{id=1, type=INPUT, layer=2}, " +
                "Node{id=0, type=INPUT, layer=3}" +
                "]", list.toString());
    }

    @Test
    public void containsIdTest () {
        NodeList list = new NodeList();
        assertFalse(list.containsId(0));
        assertFalse(list.containsId(4));

        list.add(new Node(0, Node.Type.INPUT));
        list.add(new Node(3, Node.Type.INPUT));

        assertTrue(list.containsId(0));
        assertFalse(list.containsId(4));
        assertTrue(list.containsId(3));
    }

    @Test
    public void getOutputsTest () {
        Node n0 = new Node(0, Node.Type.INPUT);
        Node n1 = new Node(1, Node.Type.OUTPUT);
        Node n2 = new Node(2, Node.Type.HIDDEN);
        Node n3 = new Node(3, Node.Type.OUTPUT);
        NodeList list = new NodeList();
        list.add(n0);
        list.add(n1);
        list.add(n2);
        list.add(n3);

        List<Node> outputs = list.getOutputs();
        assertEquals(2, outputs.size());
        assertSame(n1, outputs.get(0));
        assertSame(n3, outputs.get(1));
    }
}
