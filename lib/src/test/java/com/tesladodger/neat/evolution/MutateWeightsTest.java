package com.tesladodger.neat.evolution;

import com.tesladodger.neat.Connection;
import com.tesladodger.neat.Genome;
import com.tesladodger.neat.utils.InnovationHistory;
import com.tesladodger.neat.utils.Parameters;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public class MutateWeightsTest {

    @RepeatedTest(10)
    public void mutateWeightsTest0 () {
        Parameters params = new Parameters();
        params.weightMutationPower = 0;
        params.newRandomWeightValueProbability = .5;
        params.weightLowerBound = -10;
        params.weightUpperBound = 10;
        params.mutateRecentGenesAgeCutoff = 0;

        Genome genome = new Genome();
        Connection c0 = new Connection(0, 0, 0);
        for (int i = 0; i < 1000; i++) {
            genome.addConnections(c0.clone());
        }

        Mutation.mutateWeights(genome, new InnovationHistory(), params, ThreadLocalRandom.current());
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
        params.mutateRecentGenesAgeCutoff = 0;
        params.newRandomWeightValueProbability = .5;
        params.weightLowerBound = 10;
        params.weightUpperBound = 20;

        Genome genome = new Genome();
        Connection c0 = new Connection(0, 0, 0);
        for (int i = 0; i < 1000; i++) {
            genome.addConnection(c0.clone());
        }

        Mutation.mutateWeights(genome, new InnovationHistory(), params, ThreadLocalRandom.current());
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
        params.newRandomWeightValueProbability = 0;

        InnovationHistory h = new InnovationHistory();

        Genome genome = new Genome();
        for (int i = 0; i < 1000; i++) {
            Connection c = new Connection(
                    h.getNewConnectionMutationInnovationNumber(i, 1000),
                    i, 1000);
            genome.addConnection(c);
            h.incrementConnectionAges();
        }

        params.mutateRecentGenesBias = 100;
        params.weightMutationPower = 0;
        params.mutateRecentGenesAgeCutoff = 500;

        Mutation.mutateWeights(genome, h, params, ThreadLocalRandom.current());

        int count = 0;
        for (Connection c : genome.getConnections().asOrderedList()) {
            if (c.getWeight() != 0.0) {
                count++;
                if (c.getInnovationNumber() <= 500) {
                    fail("Old gene with innovation number [" + c.getInnovationNumber() + "] was " +
                            "mutated.");
                }
            }
        }
        assertEquals(500, count, 50);
    }
}
