package com.tesladodger.neat.utils;

import java.util.Random;


/**
 * Useful operations on arrays.
 *
 * @author tesla
 */
public class Arrays {

    /**
     * Prevent instantiation.
     */
    private Arrays () {}

    /**
     * Shuffle the elements of an array, using Fisher-Yates shuffle.
     *
     * <p>Call to this method where {@code rand} has the same seed guarantee the same result.
     *
     * @param a array to shuffle;
     * @param i start index (inclusive);
     * @param j end index (exclusive);
     * @param rand random instance;
     */
    public static void shuffle (Object[] a, int i, int j, Random rand) {
        for ( ; i < j-1; i++) {
            int x = rand.nextInt(j-i) + i;
            swap(a, i, x);
        }
    }

    private static void swap (Object[] a, int i, int j) {
        Object temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}
