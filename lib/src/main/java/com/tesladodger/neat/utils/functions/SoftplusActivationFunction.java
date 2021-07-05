package com.tesladodger.neat.utils.functions;


/**
 * Softplus (Smooth ReLU) activation function.
 *
 * <p>Smooth approximation of the rectifier function, defined as:
 *
 * <p>{@code f(x) = ln(1 + e^(kx)) / k}
 *
 * @author tesla
 * @since v1.1
 */
public class SoftplusActivationFunction implements ActivationFunction {

    /**
     * Sharpness of the curve at the origin.
     *
     * <p>{@code f(x) = ln(1 + e^(kx)) / k}
     *
     * <p>where k is the sharpness.
     *
     * <p>A value of {@code 0} will result in division by zero.
     */
    public double sharpness = 1.0;

    @Override
    public double apply (double value) {
        return Math.log(1 + Math.exp(sharpness * value)) / sharpness;
    }
}
