package com.tesladodger.neat.evolution;

import com.tesladodger.neat.utils.Parameters;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class EvolutionUtilsTest {

    @Test
    public void calculateMutationPowerFunctionTest () {
        Parameters p = new Parameters();
        p.mutateRecentGenesAgeCutoff = 0;
        p.mutateRecentGenesSizeThreshold = 10;
        p.weightMutationPower = 2;

        Function<Integer, Double> f = EvolutionUtils.calculateMutationPowerFunction(p, 20);
        assertEquals(2, f.apply(0));
        assertEquals(2, f.apply(10));
        assertEquals(2, f.apply(100));

        p.mutateRecentGenesAgeCutoff = 10;
        p.mutateRecentGenesBias = .5;

        f = EvolutionUtils.calculateMutationPowerFunction(p, 20);
        assertEquals(2.5, f.apply(0), 0.00001);
        assertEquals(2.32, f.apply(2), 0.00001);
        assertEquals(2.18, f.apply(4), 0.00001);
        assertEquals(2.08, f.apply(6), 0.00001);
        assertEquals(2.02, f.apply(8), 0.00001);
        assertEquals(2, f.apply(10), 0.00001);
        assertEquals(2, f.apply(15), 0.00001);
        assertEquals(2, f.apply(20), 0.00001);
        assertEquals(2, f.apply(25), 0.00001);

        f = EvolutionUtils.calculateMutationPowerFunction(p, 9);
        assertEquals(2, f.apply(0), 0.00001);
        assertEquals(2, f.apply(4), 0.00001);
        assertEquals(2, f.apply(8), 0.00001);
        assertEquals(2, f.apply(10), 0.00001);
        assertEquals(2, f.apply(20), 0.00001);

        p.mutateRecentGenesAgeCutoff = 15;
        p.mutateRecentGenesBias = 10;
        p.weightMutationPower = 10;

        f = EvolutionUtils.calculateMutationPowerFunction(p, 20);
        assertEquals(20, f.apply(0), 0.00001);
        assertEquals(16.4, f.apply(3), 0.00001);
        assertEquals(13.6, f.apply(6), 0.00001);
        assertEquals(11.6, f.apply(9), 0.00001);
        assertEquals(10.4, f.apply(12), 0.00001);
        assertEquals(10, f.apply(15), 0.00001);
        assertEquals(10, f.apply(20), 0.00001);
        assertEquals(10, f.apply(25), 0.00001);

        p.weightMutationPower = 0;

        f = EvolutionUtils.calculateMutationPowerFunction(p, 20);
        assertEquals(10, f.apply(0), 0.00001);
        assertEquals(6.4, f.apply(3), 0.00001);
        assertEquals(3.6, f.apply(6), 0.00001);
        assertEquals(1.6, f.apply(9), 0.00001);
        assertEquals(0.4, f.apply(12), 0.00001);
        assertEquals(0, f.apply(15), 0.00001);
        assertEquals(0, f.apply(20), 0.00001);
        assertEquals(0, f.apply(25), 0.00001);
    }
}
