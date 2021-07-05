package com.tesladodger.neat.utils.functions;

import com.tesladodger.neat.Node;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ActivationFunctionsTest {

    private Stream<DynamicTest> generateStream (List<Double> inputs, List<Double> outputs,
                                       ActivationFunction f, double delta) {
        return inputs.stream().map(input -> DynamicTest.dynamicTest(
                "Function " + f.getClass().getSimpleName() + " with value " + input,
                () ->  {
                    int index = inputs.indexOf(input);
                    assertEquals(outputs.get(index), f.apply(input), delta);
                }
        ));
    }

    @TestFactory
    public Stream<DynamicTest> sigmoidTest0 () {
        List<Double> inputs = Arrays.asList( 0.0, -10.0, 10.0,   0.2,  -0.2);
        List<Double> outputs = Arrays.asList(0.5,   0.0,  1.0, 0.727, 0.273);
        ActivationFunction f = new SigmoidActivationFunction();
        return generateStream(inputs, outputs, f, 0.001);
    }

    @TestFactory
    public Stream<DynamicTest> sigmoidTest1 () {
        List<Double> inputs = Arrays.asList( 0.0, -10.0, 10.0,    0.2,   -0.2);
        List<Double> outputs = Arrays.asList(0.5,    .0,  1.0, 0.5498, 0.4502);
        SigmoidActivationFunction f = new SigmoidActivationFunction();
        f.logisticGrowthRate = 1.0;
        return generateStream(inputs, outputs, f, 0.0001);
    }

    @TestFactory
    public Stream<DynamicTest> binaryTest () {
        List<Double> inputs = Arrays.asList( 0.0, -10.0, -5.0, -0.0001, 1.54, 3.698483498);
        List<Double> outputs = Arrays.asList(1.0,    .0,   .0,      .0,  1.0,         1.0);
        ActivationFunction f = new StepActivationFunction();
        return generateStream(inputs, outputs, f, 0.0);
    }

    @TestFactory
    public Stream<DynamicTest> rectifierTest () {
        List<Double> inputs = Arrays.asList( .0, -10.0, -5.0, -0.0001, 1.54, 3.698483498, 0.0001);
        List<Double> outputs = Arrays.asList(.0,    .0,   .0,      .0, 1.54, 3.698483498, 0.0001);
        ActivationFunction f = new RectifierActivationFunction();
        return generateStream(inputs, outputs, f, 0.0);
    }

    // test method reference
    static double passThrough (double value) {
        return value;
    }

    @Test
    public void methodReferenceTest () {
        List<Double> inputs = Arrays.asList(-20.0, -3.2, -15.3454, 0.001, -1.0005, 2.54, 5.16);
        Node n = new Node(0, Node.Type.INPUT);
        double sum = 0;
        for (Double input : inputs) {
            sum += input;
            n.addInput(input);
            assertEquals(sum, n.getOutput(ActivationFunctionsTest::passThrough));
        }
    }

    @Test
    public void lambdaFunctionTest () {
        Node n = new Node(0, Node.Type.INPUT);
        n.addInput(12);
        assertEquals(12, n.getOutput((x) -> x));
    }
}
