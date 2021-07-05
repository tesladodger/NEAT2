package com.tesladodger.neat.utils.exceptions;


/**
 * Thrown when an operation is attempted on a {@link com.tesladodger.neat.Genome} that does not
 * respect the topological requirements of this library.
 *
 * <p>For example, when trying to fully connect the nodes of a genome without inputs or outputs,
 * this exception will be called.
 *
 * @author tesla
 */
public class IllegalTopologyException extends RuntimeException {

    /**
     * Constructs a new illegal topology exception with the specified detail message.
     *
     * @param message the detail message;
     */
    public IllegalTopologyException (String message) {
        super(message);
    }
}
