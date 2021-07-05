package com.tesladodger.neat.tools;

import com.tesladodger.neat.utils.functions.ActivationFunction;


/**
 * Implements a unit softmax function, to normalize an array of doubles.
 *
 * <p>Intended to be used to normalize the outputs of
 * {@link com.tesladodger.neat.Genome#calculateOutput(double[], ActivationFunction)}, but be free
 * to use it for something else!
 *
 * @author tesla
 * @version 1.0
 */
public class SoftmaxFunction {

    /**
     * Base of the exponent.
     *
     * <p>If <it>0 < base < 1</it>, smaller input components will result in larger output
     * probabilities, and decreasing the value of {@code base} will create probability
     * distributions that are more concentrated around the positions of the smallest input
     * values. Conversely, if <it>base > 1</it>, larger input components will result in larger
     * output probabilities, and increasing the value of {@code base} will create probability
     * distributions that are more concentrated around the positions of the largest input values.
     */
    public double base = Math.E;

    /**
     * Apply the function to an array.
     *
     * <p>The value of index {@code i} will be {@code base^input[i]} divided by the sum of the
     * base to the power of the inputs.
     *
     * @param input array to normalize;
     *
     * @return new array with the result;
     */
    public double[] apply (double[] input) {
        double[] aux = new double[input.length];
        double val;
        double sum = 0;
        for (int i = 0; i < input.length; i++) {
            val = Math.pow(base, input[i]);
            aux[i] = val;
            sum += val;
        }
        double[] result = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            result[i] = aux[i] / sum;
        }
        return result;
    }
}
