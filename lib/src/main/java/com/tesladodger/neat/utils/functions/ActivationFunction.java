package com.tesladodger.neat.utils.functions;


/**
 * Interface to write {@link com.tesladodger.neat.Node} activation functions. Some common
 * functions are already implemented in this package.
 *
 * @author tesla
 * @version 1.0
 */
public interface ActivationFunction {

    /**
     * Apply the activation function to the sum of the inputs.
     *
     * @param value sum of the inputs of the node;
     *
     * @return result of the function;
     */
    double apply (double value);
}
