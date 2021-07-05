package com.tesladodger.neat.utils.functions;


/**
 * Commonly used activation function (it's actually a logistic function, which is a type of
 * sigmoid). The function is:
 *
 * <p>1 / ( 1 + e^( - k * (x-x0) ) )
 *
 * <p>where k is the {@link SigmoidActivationFunction#logisticGrowthRate} and x0 is the
 * {@link SigmoidActivationFunction#offset}. The result ranges [0,1].
 *
 * @author tesla
 * @version 1.0
 */
public class SigmoidActivationFunction implements ActivationFunction {

    /**
     * Steepness of the logistic curve. This default value was taken from K.O.Stanley and R.
     * Miikkulainen's paper: Evolving Neural Networks through Augmenting Topologies.
     */
    public double logisticGrowthRate = 4.9;

    /** Offset from the x axis. */
    public double offset = 0.0;

    @Override
    public double apply (double value) {
        return 1.0 / (1.0 + Math.pow(Math.E, -logisticGrowthRate * (value - offset)));
    }
}
