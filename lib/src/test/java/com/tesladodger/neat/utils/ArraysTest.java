package com.tesladodger.neat.utils;

import com.tesladodger.neat.Node;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ArraysTest {

    @Test
    public void shuffleTest0 () {
        Node[] nodes = new Node[5];
        for (int i = 0; i < 5; i++) {
            nodes[i] = new Node(i, Node.Type.INPUT);
        }

        Node[] toShuffle = new Node[5];
        System.arraycopy(nodes, 0, toShuffle, 0, 5);
        Arrays.shuffle(toShuffle, 0, 2, ThreadLocalRandom.current());
        //System.out.println(Arrays.toString(toShuffle));

        System.arraycopy(nodes, 0, toShuffle, 0, 5);
        Arrays.shuffle(toShuffle, 0, 5, ThreadLocalRandom.current());
        //System.out.println(Arrays.toString(toShuffle));
    }

    @RepeatedTest(30)
    public void shuffleTest1 () {
        Node[] nodes = new Node[1000];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node(i, Node.Type.INPUT);
        }

        Node[] loShuffle = new Node[nodes.length];
        System.arraycopy(nodes, 0, loShuffle, 0, loShuffle.length);
        Node[] hiShuffle = new Node[nodes.length];
        System.arraycopy(nodes, 0, hiShuffle, 0, hiShuffle.length);
        Node[] middleShuffle = new Node[nodes.length];
        System.arraycopy(nodes, 0, middleShuffle, 0, middleShuffle.length);

        Arrays.shuffle(loShuffle, 0, 500, ThreadLocalRandom.current());
        Arrays.shuffle(hiShuffle, 500, nodes.length, ThreadLocalRandom.current());
        Arrays.shuffle(middleShuffle, 250, 750, ThreadLocalRandom.current());

        assertTrue(shuffleCount(loShuffle, 0, 500) > 400);
        assertEquals(0, shuffleCount(loShuffle, 500, loShuffle.length));

        assertEquals(0, shuffleCount(hiShuffle, 0, 500));
        assertTrue(shuffleCount(hiShuffle, 500, hiShuffle.length) > 400);

        assertEquals(0, shuffleCount(middleShuffle, 0, 250));
        assertTrue(shuffleCount(middleShuffle, 250, 750) > 400);
        assertEquals(0, shuffleCount(middleShuffle, 750, middleShuffle.length));
    }

    /**
     * Number of <it>out of order</it> nodes in the array.
     *
     * @param nodes array to test;
     * @param lo low index (inclusive);
     * @param hi high index (exclusive);
     *
     * @return number of unordered nodes;
     */
    private int shuffleCount (Node[] nodes, int lo, int hi) {
        int count = 0;
        for (int i = lo; i < hi; i++) {
            if (nodes[i].getId() != i) {
                count++;
            }
        }
        return count;
    }
}
