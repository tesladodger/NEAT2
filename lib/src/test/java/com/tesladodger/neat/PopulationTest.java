package com.tesladodger.neat;

import com.tesladodger.neat.utils.InnovationHistory;
import com.tesladodger.neat.utils.Parameters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class PopulationTest {

    @Test
    public void exceptionTests () {
        Population population = new Population(new Parameters());
        assertThrows(IllegalStateException.class, population::getHighestFitness);
        assertThrows(IllegalStateException.class, population::getLastHighestFitness);
        assertThrows(IllegalStateException.class, population::getLastChampion);
    }

    @Test
    public void spawnTest0 () {
        Parameters params = new Parameters();
        params.averageWeightDifferenceCompatibilityCoefficient = 0.0f;
        params.disjointGenesCompatibilityCoefficient = 1f;
        params.excessGenesCompatibilityCoefficient = 1f;

        Population p = new Population(params);
        InnovationHistory h = new InnovationHistory();
        Genome g = new GenomeBuilder(h)
                .setNumberOfNodes(2, 1)
                .setFullyConnected(true)
                .build();

        LinkedList<Genome> gs = p.spawn(g, 10);

        assertEquals(10, gs.size());

        for (Genome genome : gs) {
            assertEquals(0.0, Genome.compatibilityBetween(g, genome, params), 0.00001);
            assertEquals(0.0, Genome.compatibilityBetween(genome, g, params), 0.00001);
        }
    }

    @RepeatedTest(10)
    @DisplayName("Test repeatability of spawn using the same random seed")
    public void spawnTest1 () {
        Population p = new Population(new Parameters());
        Genome g = new GenomeBuilder(new InnovationHistory())
                .setNumberOfNodes(10, 10)
                .setFullyConnected(true)
                .setHiddenLayers(10)
                .build();

        Random rand = new Random(11);
        LinkedList<Genome> gs0 = p.spawn(g, 20, rand);

        rand.setSeed(11);
        LinkedList<Genome> gs1 = p.spawn(g, 20, rand);

        assertEquals(20, gs0.size());
        assertEquals(20, gs1.size());

        Iterator<Genome> it1 = gs1.iterator();

        for (Genome genome : gs0) {
            assertEquals(genome, it1.next());
        }

        rand.setSeed(11);
        LinkedList<Genome> gs2 = p.spawn(g, 20, rand);

        Iterator<Genome> it0 = gs0.iterator();

        for (Genome genome : gs2) {
            assertEquals(it0.next(), genome);
        }
    }

    /**
     * Spawn test for large genomes.
     */
    @Test
    public void spawnTest2 () {
        Parameters params = new Parameters();
        params.averageWeightDifferenceCompatibilityCoefficient = 0.0f;
        params.disjointGenesCompatibilityCoefficient = 100f;
        params.excessGenesCompatibilityCoefficient = 100f;

        Population p = new Population(params);
        InnovationHistory h = new InnovationHistory();
        Genome g = new GenomeBuilder(h)
                .setNumberOfNodes(20, 20)
                .setFullyConnected(true)
                .build();

        LinkedList<Genome> gs = p.spawn(g, 50);

        assertEquals(50, gs.size());

        for (Genome genome : gs) {
            assertEquals(0.0, Genome.compatibilityBetween(g, genome, params), 0.00001);
            assertEquals(0.0, Genome.compatibilityBetween(genome, g, params), 0.00001);
        }
    }

    @Test
    public void speciateTest0 () {
        Genome g0 = new Genome();
        Genome g1 = new Genome();
        Genome g2 = new Genome();
        Genome g3 = new Genome();
        Genome g4 = new Genome();
        Genome g5 = new Genome();

        Connection c0 = new Connection(0, 0, 0);
        Connection c1 = new Connection(1, 0, 0);
        Connection c2 = new Connection(2, 0, 0);
        Connection c3 = new Connection(3, 0, 0);
        Connection c4 = new Connection(4, 0, 0);
        Connection c5 = new Connection(5, 0, 0);

        g0.addConnection(c0);
        g1.addConnection(c1);
        g2.addConnection(c2);
        g3.addConnection(c3);
        g4.addConnection(c4);
        g5.addConnection(c5);

        Parameters params = new Parameters();
        params.averageWeightDifferenceCompatibilityCoefficient = 0;
        params.disjointGenesCompatibilityCoefficient = 1;
        params.excessGenesCompatibilityCoefficient = 1;
        params.compatibilityThreshold = 1;

        Population p = new Population(params);
        LinkedList<Genome> genomes = new LinkedList<>();
        genomes.add(g0);
        genomes.add(g1);
        genomes.add(g2);
        genomes.add(g0.clone());
        genomes.add(g3);
        genomes.add(g1.clone());
        genomes.add(g3.clone());
        genomes.add(g4);
        genomes.add(g3.clone());
        genomes.add(g5);
        genomes.add(g4.clone());

        p.speciate(genomes);
        LinkedList<Species> species = p.getSpecies();

        assertEquals(6, species.size());

        assertEquals(2, species.get(0).size());
        assertSame(g0, species.get(0).getRepresentative());
        assertEquals(g0, species.get(0).getGenomes().get(1));
        assertNotSame(g0, species.get(0).getGenomes().get(1));

        assertEquals(2, species.get(1).size());
        assertSame(g1, species.get(1).getRepresentative());
        assertEquals(g1, species.get(1).getGenomes().get(1));
        assertNotSame(g1, species.get(1).getGenomes().get(1));

        assertEquals(1, species.get(2).size());
        assertSame(g2, species.get(2).getRepresentative());
        assertEquals(g2, species.get(2).getGenomes().get(0));
        assertSame(g2, species.get(2).getGenomes().get(0));

        assertEquals(3, species.get(3).size());
        assertSame(g3, species.get(3).getRepresentative());
        assertEquals(g3, species.get(3).getGenomes().get(1));
        assertNotSame(g3, species.get(3).getGenomes().get(1));
        assertEquals(g3, species.get(3).getGenomes().get(2));
        assertNotSame(g3, species.get(3).getGenomes().get(2));

        assertEquals(2, species.get(4).size());
        assertSame(g4, species.get(4).getRepresentative());
        assertEquals(g4, species.get(4).getGenomes().get(1));
        assertNotSame(g4, species.get(4).getGenomes().get(1));

        assertEquals(1, species.get(5).size());
        assertSame(g5, species.get(5).getRepresentative());
        assertEquals(g5, species.get(5).getGenomes().get(0));
        assertSame(g5, species.get(5).getGenomes().get(0));
    }

    @Test
    public void calculateAssignedOffspringTest () {
        Parameters params = new Parameters();

        Genome g0 = new Genome();
        g0.setFitness(6);
        Genome g1 = new Genome();
        g1.setFitness(2);
        Genome g2 = new Genome();
        g2.setFitness(4);
        Genome g3 = new Genome();
        g3.setFitness(1);
        Genome g4 = new Genome();
        g4.setFitness(5);

        Species s0 = new Species(params);
        Species s1 = new Species(params);
        Species s2 = new Species(params);
        Species s3 = new Species(params);
        Species s4 = new Species(params);
        Species s5 = new Species(params);
        s0.addGenome(g0);
        s1.addGenome(g1);
        s2.addGenome(g2);
        s3.addGenome(g3);
        s4.addGenome(g4);

        Population p = new Population(params);
        LinkedList<Species> sList = p.getSpecies();
        sList.add(s0);
        sList.add(s1);
        sList.add(s2);
        sList.add(s3);
        sList.add(s4);
        sList.add(s5);

        sList.parallelStream().forEach(Species::calculateAdjustedFitness);

        p.calculateAssignedOffspring(100);

        assertEquals(35, sList.get(0).getAssignedOffspring());
        assertEquals(11, sList.get(1).getAssignedOffspring());
        assertEquals(22, sList.get(2).getAssignedOffspring());
        assertEquals(5, sList.get(3).getAssignedOffspring());
        assertEquals(27, sList.get(4).getAssignedOffspring());
    }

    @RepeatedTest(3)
    @DisplayName("Repeatability of creation of next generation with same random seed")
    public void nextGenerationTest0 () {
        /* create two identical populations */
        Random rand0 = new Random(13);
        Population p0 = new Population(new Parameters());
        InnovationHistory h0 = new InnovationHistory();
        Genome g0 = new GenomeBuilder(h0)
                .setNumberOfNodes(10, 10)
                .setFullyConnected(true)
                .setHiddenLayers(10)
                .build(rand0);
        LinkedList<Genome> gs0 = p0.spawn(g0, 100, rand0);

        for (Genome genome : gs0) {
            genome.setFitness(rand0.nextDouble() * 20);
        }

        Random rand1 = new Random(13);
        Population p1 = new Population(new Parameters());
        InnovationHistory h1 = new InnovationHistory();
        Genome g1 = new GenomeBuilder(h1)
                .setNumberOfNodes(10, 10)
                .setFullyConnected(true)
                .setHiddenLayers(10)
                .build(rand1);
        LinkedList<Genome> gs1 = p1.spawn(g1, 100, rand1);

        for (Genome genome : gs1) {
            genome.setFitness(rand1.nextDouble() * 20);
        }

        /* successively create new generations and compare them */
        LinkedList<Genome> previousGen0 = gs0;
        LinkedList<Genome> previousGen1 = gs1;
        for (int i = 0; i < 50; i++) {
            /* create two generations */
            LinkedList<Genome> currentGen0 = p0.nextGeneration(previousGen0, h0, rand0);
            LinkedList<Genome> currentGen1 = p1.nextGeneration(previousGen1, h1, rand1);

            /* compare them */
            assertEquals(100, currentGen0.size());
            assertEquals(100, currentGen1.size());

            assertEquals(p0.getLastChampion(), p1.getLastChampion());
            assertNotSame(p0.getLastChampion(), p1.getLastChampion());

            assertEquals(p0.getHighestFitness(), p1.getHighestFitness());

            Iterator<Genome> it = currentGen0.iterator();
            int j = 0;
            for (Genome genome1 : currentGen1) {
                Genome genome0 = it.next();
                assertEquals(genome0, genome1, "Failed at generation " + i + ", comparison " + j);
                assertNotSame(genome0, genome1, "Failed at generation " + i + ", comparison " + j);
            }

            for (Genome g : currentGen0) {
                g.setFitness(rand0.nextDouble() * 20 * i);
            }
            for (Genome g : currentGen1) {
                g.setFitness(rand1.nextDouble() * 20 * i);
            }

            previousGen0 = currentGen0;
            previousGen1 = currentGen1;
        }
    }
}

