package com.tesladodger.neat.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class SoftmaxFunctionTest {

    @Test
    public void apply0 () {
        double[] input = {1, 2, 3, 4, 1, 2, 3};
        double[] expected = {0.02364054, 0.06426166, 0.1746813, 0.474833, 0.02364054,
                0.06426166, 0.1746813};
        SoftmaxFunction f = new SoftmaxFunction();
        double[] result = f.apply(input);
        assertArrayEquals(expected, result, 0.00000001);
    }

    @Test
    public void apply1 () {
        double[] input = {1, 2, 3, 4};
        double[] expected = {0.03205860328008499, 0.08714431874203257, 0.23688281808991013,
                0.6439142598879722};
        SoftmaxFunction f = new SoftmaxFunction();
        double[] result = f.apply(input);
        assertArrayEquals(expected, result, 0.00000000000000001);
    }

    @Test
    public void apply2 () {
        double[] input = {0.1, 0.2, 0.3, 0.4, 0.1, 0.2, 0.3};
        double[] expected = {0.125, 0.138, 0.153, 0.169, 0.125, 0.138, 0.153};
        SoftmaxFunction f = new SoftmaxFunction();
        double[] result = f.apply(input);
        assertArrayEquals(expected, result, 0.001);
    }
}