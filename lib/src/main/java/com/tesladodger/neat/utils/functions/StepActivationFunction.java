package com.tesladodger.neat.utils.functions;


/**
 * Generalized Heaviside step activation function: if the input is higher or equal to a certain
 * threshold (given by {@link StepActivationFunction#offset}), return
 * {@link StepActivationFunction#highValue}, otherwise return
 * {@link StepActivationFunction#lowValue}.
 *
 * @author tesla
 * @version 1.0
 */
public class StepActivationFunction implements ActivationFunction {

    /** Offset of the step in the x axis. */
    public double offset = 0.0;

    /** Low value of the step (when input is less than {@link StepActivationFunction#offset}. */
    public double lowValue = 0.0;

    /** High value of the step (when input is more than {@link StepActivationFunction#offset}. */
    public double highValue = 1.0;

    @Override
    public double apply (double value) {
        return value < offset ? lowValue : highValue;
    }
}
