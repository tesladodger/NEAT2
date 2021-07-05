package com.tesladodger.neat;

import com.tesladodger.neat.utils.Parameters;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public class SpeciesTest {

    @Test
    public void isCompatibleTest () {
        Parameters params = new Parameters();
        params.compatibilityThreshold = 3.0f;
        params.excessGenesCompatibilityCoefficient = 1;
        params.disjointGenesCompatibilityCoefficient = 1;
        params.averageWeightDifferenceCompatibilityCoefficient = 1;

        Species species = new Species(params);
        assertThrows(NullPointerException.class, () -> species.isCompatible(new Genome()));

        Connection c0 = new Connection(0, 0, 0, 0);
        Connection c1 = new Connection(0, 0, 0, 3);

        Genome g1 = new Genome();
        Genome g2 = new Genome();
        g1.addConnection(c0);
        g2.addConnection(c1);

        species.setRepresentative(g1);
        assertTrue(species.isCompatible(g2));

        Connection c2 = new Connection(1, 0, 0, 0);
        g2.addConnection(c2);
        assertFalse(species.isCompatible(g2));
    }

    @Test
    public void addGenomeOrderTest () {
        Genome g1 = new Genome();
        g1.setFitness(4);
        Genome g2 = new Genome();
        g2.setFitness(6);
        Genome g3 = new Genome();
        g3.setFitness(5);
        Genome g4 = new Genome();
        g4.setFitness(3);
        Genome g5 = new Genome();
        g5.setFitness(1);
        Genome g6 = new Genome();
        g6.setFitness(2);

        Parameters params = new Parameters();
        Species species = new Species(params);
        species.addGenome(g1);
        species.addGenome(g2);
        species.addGenome(g3);
        species.addGenome(g4);
        species.addGenome(g5);
        species.addGenome(g6);

        int x = 6;
        for (Genome g : species.getGenomes()) {
            assertEquals(x--, g.getFitness());
        }
    }

    @Test
    public void cullTest0 () {
        Genome g1 = new Genome();
        Genome g2 = new Genome();
        Genome g3 = new Genome();

        g1.setFitness(1);
        g2.setFitness(2);
        g3.setFitness(3);

        Parameters params = new Parameters();
        Species s = new Species(params);
        s.addGenome(g1);
        s.addGenome(g2);
        s.addGenome(g3);

        s.cull();

        assertEquals(2, s.size());
        List<Genome> genomes = s.getGenomes();
        assertSame(g3, genomes.get(0));
        assertSame(g2, genomes.get(1));
    }

    @Test
    public void cullTest1 () {
        Parameters params = new Parameters();
        Genome g1 = new Genome();
        Genome g2 = new Genome();
        Genome g3 = new Genome();
        Genome g4 = new Genome();
        Genome g5 = new Genome();
        Genome g6 = new Genome();

        g1.setFitness(1);
        g2.setFitness(2);
        g3.setFitness(3);
        g4.setFitness(4);
        g5.setFitness(5);
        g6.setFitness(6);

        Species s = new Species(params);
        s.addGenome(g1);
        s.addGenome(g2);
        s.addGenome(g3);
        s.addGenome(g4);
        s.addGenome(g5);
        s.addGenome(g6);

        s.cull();

        assertEquals(3, s.size());
        List<Genome> genomes = s.getGenomes();
        assertSame(g6, genomes.get(0));
        assertSame(g5, genomes.get(1));
        assertSame(g4, genomes.get(2));
    }

    @Test
    public void calculateAdjustedFitness () {
        Parameters params = new Parameters();
        Genome g0 = new Genome();
        Genome g1 = new Genome();
        Genome g2 = new Genome();
        Genome g3 = new Genome();
        Genome g4 = new Genome();
        g0.setFitness(12);
        g1.setFitness(5);
        g2.setFitness(2);
        g3.setFitness(8);
        g4.setFitness(4);
        Species s = new Species(params);
        s.addGenome(g0);
        s.addGenome(g1);
        s.addGenome(g2);
        s.addGenome(g3);
        s.addGenome(g4);

        s.calculateAdjustedFitness();
        assertEquals(6.2, s.getAdjustedFitness());

        s.clearGenomes();
        Genome g5 = new Genome();
        Genome g6 = new Genome();
        Genome g7 = new Genome();
        Genome g8 = new Genome();
        Genome g9 = new Genome();
        g5.setFitness(1.2);
        g6.setFitness(0.5);
        g7.setFitness(1);
        g8.setFitness(3.1);
        g9.setFitness(4.7);
        s.addGenome(g5);
        s.addGenome(g6);
        s.addGenome(g7);
        s.addGenome(g8);
        s.addGenome(g9);

        s.calculateAdjustedFitness();
        assertEquals(1, s.getGenerationsWithoutImprovement());
        assertEquals(2.1, s.getAdjustedFitness());
        assertEquals(12, s.getHighestFitness());
    }

    @Test
    public void generationsWithoutImprovementTest () {
        Parameters params = new Parameters();
        Species s = new Species(params);
        for (int i = 0; i < 10; i++) {
            Genome g = new Genome();
            g.setFitness(10-i);
            s.addGenome(g);
            s.calculateAdjustedFitness();
            assertEquals(i, s.getGenerationsWithoutImprovement());
            s.clearGenomes();
        }

        Genome g = new Genome();
        g.setFitness(11);
        s.addGenome(g);
        s.calculateAdjustedFitness();
        assertEquals(0, s.getGenerationsWithoutImprovement());

        s.clearGenomes();
        g.setFitness(11);
        s.addGenome(g);
        s.calculateAdjustedFitness();
        assertEquals(1, s.getGenerationsWithoutImprovement());
    }

    @Test
    public void chooseRepresentativeTest0 () {
        Parameters params = new Parameters();
        Genome g = new Genome();
        Species s = new Species(params, g);
        assertSame(g, s.chooseRepresentative(ThreadLocalRandom.current()));
        s.clearGenomes();
        Genome g0 = new Genome();
        s.addGenome(g0);
        assertSame(g0, s.chooseRepresentative(ThreadLocalRandom.current()));
    }

    @RepeatedTest(10)
    public void chooseRepresentativeTest1 () {
        Parameters params = new Parameters();
        Genome g = new Genome();
        Species s = new Species(params, g);
        s.clearGenomes();

        Genome g0 = new Genome();
        Genome g1 = new Genome();
        Genome g2 = new Genome();
        Genome g3 = new Genome();

        int r0 = 0;
        int r1 = 0;
        int r2 = 0;
        int r3 = 0;

        int i = 0;
        while (i++ < 1000) {
            s.addGenome(g0);
            s.addGenome(g1);
            s.addGenome(g2);
            s.addGenome(g3);
            Genome rep = s.chooseRepresentative(ThreadLocalRandom.current());
            s.clearGenomes();

            if (rep == g0) r0++;
            else if (rep == g1) r1++;
            else if (rep == g2) r2++;
            else if (rep == g3) r3++;
            else fail("Representative is not correct.");
        }

        assertEquals(250, r0, 60);
        assertEquals(250, r1, 60);
        assertEquals(250, r2, 60);
        assertEquals(250, r3, 60);
    }
}
