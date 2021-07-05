package com.tesladodger.neat.evolution;

import com.tesladodger.neat.Connection;
import com.tesladodger.neat.Genome;
import com.tesladodger.neat.utils.Parameters;

import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class MutateWeightsTest {

    @RepeatedTest(10)
    public void mutateWeightsTest0 () {
        Parameters params = new Parameters();
        params.weightMutationPower = 0;
        params.newRandomWeightValueProbability = .5;
        params.weightLowerBound = -10;
        params.weightUpperBound = 10;

        Genome genome = new Genome();
        Connection c0 = new Connection(0, 0, 0);
        for (int i = 0; i < 1000; i++) {
            genome.addConnections(c0.clone());
        }

        Mutation.mutateWeights(genome, params, ThreadLocalRandom.current());
        int count = 0;
        double sum = 0;
        for (Connection con : genome.getConnections().asOrderedList()) {
            if (con.getWeight() != 0) {
                assertTrue(con.getWeight() < 10 || con.getWeight() >= -10);
                count++;
                sum += con.getWeight();
            }
        }

        assertEquals(500, count, 60);
        assertEquals(0, sum / count, 1);
    }

    @RepeatedTest(10)
    public void mutateWeightsTest1 () {
        Parameters params = new Parameters();
        params.weightMutationPower = 0;
        params.newRandomWeightValueProbability = .5;
        params.weightLowerBound = 10;
        params.weightUpperBound = 20;

        Genome genome = new Genome();
        Connection c0 = new Connection(0, 0, 0);
        for (int i = 0; i < 1000; i++) {
            genome.addConnection(c0.clone());
        }

        Mutation.mutateWeights(genome, params, ThreadLocalRandom.current());
        int count = 0;
        double sum = 0;
        for (Connection con : genome.getConnections().asOrderedList()) {
            if (con.getWeight() != 0) {
                assertTrue(con.getWeight() < 20 || con.getWeight() >= 10);
                count++;
                sum += con.getWeight();
            }
        }

        assertEquals(500, count, 60);
        assertEquals(15, sum / count, 1);
    }

    @RepeatedTest(10)
    public void mutateWeightsTest2 () {
        Parameters params = new Parameters();
        params.weightMutationPower = 10;
        params.newRandomWeightValueProbability = 0;

        Genome genome = new Genome();
        Connection c0 = new Connection(0, 0, 0);
        for (int i = 0; i < 1000; i++) {
            genome.addConnection(c0.clone());
        }

        Mutation.mutateWeights(genome, params, ThreadLocalRandom.current());
        // todo
    }
}
