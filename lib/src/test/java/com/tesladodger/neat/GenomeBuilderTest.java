package com.tesladodger.neat;

import com.tesladodger.neat.utils.InnovationHistory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SuppressWarnings("TextBlockMigration")
public class GenomeBuilderTest {

    @Test
    public void noHiddenNotConnectedTest0 () {
        InnovationHistory h = new InnovationHistory();

        Genome genome = new GenomeBuilder(h)
                .setFullyConnected(false)
                .setNumberOfNodes(3, 1)
                .build();

        assertEquals("Genome{\n\t" +
                "nodes=[Node{id=0, type=INPUT, layer=0}, " +
                "Node{id=1, type=INPUT, layer=0}, " +
                "Node{id=2, type=INPUT, layer=0}, " +
                "Node{id=3, type=OUTPUT, layer=1}],\n\t" +
                "connections=[]\n}", genome.toString());

        assertEquals(3, h.getCurrentHighestNodeId());
    }

    @Test
    public void noHiddenNotConnectedTest1 () {
        InnovationHistory h = new InnovationHistory();
        GenomeBuilder creator = new GenomeBuilder(h);
        creator.setFullyConnected(false).setNumberOfNodes(5, 5);
        Genome genome = creator.build();

        assertEquals("Genome{\n\t" +
                "nodes=[Node{id=0, type=INPUT, layer=0}, " +
                "Node{id=1, type=INPUT, layer=0}, " +
                "Node{id=2, type=INPUT, layer=0}, " +
                "Node{id=3, type=INPUT, layer=0}, " +
                "Node{id=4, type=INPUT, layer=0}, " +
                "Node{id=5, type=OUTPUT, layer=1}, " +
                "Node{id=6, type=OUTPUT, layer=1}, " +
                "Node{id=7, type=OUTPUT, layer=1}, " +
                "Node{id=8, type=OUTPUT, layer=1}, " +
                "Node{id=9, type=OUTPUT, layer=1}],\n\t" +
                "connections=[]\n}", genome.toString());

        assertEquals(9, h.getCurrentHighestNodeId());
    }

    @Test
    public void oneHiddenNotConnectedTest0 () {
        InnovationHistory h = new InnovationHistory();
        Genome genome = new GenomeBuilder(h)
                .setFullyConnected(false)
                .setNumberOfNodes(3, 1)
                .setHiddenLayers(2).build();

        assertEquals("Genome{\n\t" +
                "nodes=[Node{id=0, type=INPUT, layer=0}, " +
                "Node{id=1, type=INPUT, layer=0}, " +
                "Node{id=2, type=INPUT, layer=0}, " +
                "Node{id=4, type=HIDDEN, layer=1}, " +
                "Node{id=5, type=HIDDEN, layer=1}, " +
                "Node{id=3, type=OUTPUT, layer=2}],\n\t" +
                "connections=[]\n}", genome.toString());

        assertEquals(5, h.getCurrentHighestNodeId());
    }

    @Test
    public void twoHiddenNotConnectedTest0 () {
        InnovationHistory h = new InnovationHistory();
        Genome genome = new GenomeBuilder(h)
                .setFullyConnected(false)
                .setNumberOfNodes(3, 2)
                .setHiddenLayers(2, 1)
                .build();

        assertEquals("Genome{\n\t" +
                "nodes=[Node{id=0, type=INPUT, layer=0}, " +
                "Node{id=1, type=INPUT, layer=0}, " +
                "Node{id=2, type=INPUT, layer=0}, " +
                "Node{id=5, type=HIDDEN, layer=1}, " +
                "Node{id=6, type=HIDDEN, layer=1}, " +
                "Node{id=7, type=HIDDEN, layer=2}, " +
                "Node{id=3, type=OUTPUT, layer=3}, " +
                "Node{id=4, type=OUTPUT, layer=3}],\n\t" +
                "connections=[]\n}", genome.toString());

        assertEquals(7, h.getCurrentHighestNodeId());
        assertEquals(-1, h.getCurrentHighestInnovationNumber());
    }

    @Test
    public void fullyConnectedTest0 () {
        InnovationHistory h = new InnovationHistory();
        GenomeBuilder creator = new GenomeBuilder(h);
        Genome g = creator
                .setNumberOfNodes(2, 1)
                .setFullyConnected(true)
                .build();

        // test whether the innovation history was mistreated
        assertEquals(2, h.getCurrentHighestNodeId());
        assertEquals(1, h.getCurrentHighestInnovationNumber());

        assertEquals(2, g.numberOfInputs());
        assertEquals(1, g.numberOfOutputs());
        assertEquals(0, g.numberOfHidden());
        assertEquals(2, g.getConnections().size());
        assertTrue(g.getConnections().containsConnection(0, 2));
        assertTrue(g.getConnections().containsConnection(1, 2));
    }

    @RepeatedTest(5)
    @DisplayName("Genomes built with same random seed will be equal")
    public void randomSeedRepeatability () {
        Random rand = new Random(0);
        InnovationHistory h0 = new InnovationHistory();
        Genome g0 = new GenomeBuilder(h0)
                .setNumberOfNodes(10, 10)
                .setHiddenLayers(10)
                .setFullyConnected(true)
                .build(rand);

        rand.setSeed(0);
        InnovationHistory h1 = new InnovationHistory();
        Genome g1 = new GenomeBuilder(h1)
                .setNumberOfNodes(10, 10)
                .setHiddenLayers(10)
                .setFullyConnected(true)
                .build(rand);

        assertEquals(g0, g1);
        assertNotSame(g0, g1);

        Genome g2 = new GenomeBuilder(new InnovationHistory())
                .setNumberOfNodes(10, 10)
                .setHiddenLayers(10)
                .setFullyConnected(true)
                .build(new Random(0));

        assertEquals(g1, g2);
        assertNotSame(g1, g2);

        /*
         * Innovation histories were checked manually using the debugger
         */
    }
}
