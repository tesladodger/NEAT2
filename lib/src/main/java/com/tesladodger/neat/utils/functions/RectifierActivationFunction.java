package com.tesladodger.neat.utils.functions;


/**
 * Rectified Linear Unit (ReLU) activation function implementation.
 *
 * <p>It's defined as the positive part of the input:
 *
 * <p>{@code f(x) = max(0,x)}
 *
 * @author tesla
 */
public class RectifierActivationFunction implements ActivationFunction {

    @Override
    public double apply (double value) {
        return Math.max(0, value);
    }
}
