package com.tesladodger.neat.utils;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;


public class ParametersTest {

    @Test
    public void compareToTest () {
        Parameters p0 = new Parameters();
        Parameters p1 = new Parameters();
        assertEquals(p0, p1);
        assertNotSame(p0, p1);
        p0.disableGeneProbability = 0;
        assertNotEquals(p0, p1);
    }

    @Test
    public void serializeTest () throws IOException, ClassNotFoundException {
        Parameters p0 = new Parameters();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("ac2"));
        objectOutputStream.writeObject(p0);
        objectOutputStream.close();

        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("ac2"));
        Parameters p1 = (Parameters) objectInputStream.readObject();

        assertEquals(p0, p1);
        assertNotSame(p0, p1);
    }
}
