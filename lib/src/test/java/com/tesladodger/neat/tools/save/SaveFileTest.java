package com.tesladodger.neat.tools.save;

import com.tesladodger.neat.Connection;
import com.tesladodger.neat.Genome;
import com.tesladodger.neat.Node;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class SaveFileTest {

    String testPath = "saveFile/expected/";

    @Test
    public void writeGenomesTest0 () throws IOException, URISyntaxException {
        Node n0 = new Node(0, Node.Type.INPUT, 0);
        Node n1 = new Node(1, Node.Type.INPUT, 0);
        Node n2 = new Node(2, Node.Type.OUTPUT, 1);
        Connection c0 = new Connection(0, 0, 2, 0.5);
        Connection c1 = new Connection(1, 1, 2, -1);
        c1.disable();
        Genome g0 = new Genome();
        g0.addNodes(n0, n1, n2);
        g0.addConnections(c0, c1);

        File f = new File("ac0");
        OutputStream out = new FileOutputStream(f);
        assertDoesNotThrow(() -> SaveFile.saveGenomes(out, null, g0));
        out.close();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(testPath+"ex0");
        assertNotNull(url);

        assertEquals(
                Files.readString(Paths.get(url.toURI())),
                Files.readString(Paths.get("ac0")));
    }

    @Test
    public void writeGenomesTest1 () throws IOException, URISyntaxException {
        Node n0 = new Node(0, Node.Type.INPUT, 0);
        Node n1 = new Node(1, Node.Type.INPUT, 0);
        Node n2 = new Node(2, Node.Type.OUTPUT, 1);
        Connection c0 = new Connection(0, 0, 2, 0.5);
        Connection c1 = new Connection(1, 1, 2, -1);
        Genome g0 = new Genome();
        g0.addNodes(n0, n1, n2);
        g0.addConnections(c0, c1);

        Node n3 = new Node(0, Node.Type.INPUT, 0);
        Node n4 = new Node(2, Node.Type.OUTPUT, 1);
        Connection c2 = new Connection(2, 0, 2, 4);
        Genome g1 = new Genome();
        g1.addNodes(n3, n4);
        g1.addConnection(c2);

        File f = new File("ac1");
        OutputStream out = new FileOutputStream(f);
        assertDoesNotThrow(() -> SaveFile.saveGenomes(out, null, g0, g1));
        out.close();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(testPath+"ex1");
        assertNotNull(url);

        assertEquals(
                Files.readString(Paths.get(url.toURI())),
                Files.readString(Paths.get("ac1"))
        );
    }

    @Test
    public void loadGenomeTest0 () throws IOException, URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(testPath+"ex0");
        assertNotNull(url);
        Path path = Paths.get(url.toURI());
        File f = path.toFile();
        InputStream in = new FileInputStream(f);
        LinkedList<Genome> gs = SaveFile.loadGenomes(in);
        in.close();

        assertNotNull(gs);
        assertEquals(1, gs.size());
        Genome g = gs.get(0);

        Node n0 = new Node(0, Node.Type.INPUT, 0);
        Node n1 = new Node(1, Node.Type.INPUT, 0);
        Node n2 = new Node(2, Node.Type.OUTPUT, 1);
        Connection c0 = new Connection(0, 0, 2, 0.5);
        Connection c1 = new Connection(1, 1, 2, -1);
        c1.disable();
        Genome g0 = new Genome();
        g0.addNodes(n0, n1, n2);
        g0.addConnections(c0, c1);

        assertEquals(g0, g);
    }

    @Test
    public void loadGenomeTest1 () throws IOException, URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(testPath+"ex2");
        assertNotNull(url);
        Path path = Paths.get(url.toURI());
        File f = path.toFile();
        InputStream in = new FileInputStream(f);
        LinkedList<Genome> gs = SaveFile.loadGenomes(in);
        in.close();

        assertNotNull(gs);
        assertEquals(1, gs.size());
        Genome g = gs.get(0);

        Node n0 = new Node(0, Node.Type.INPUT, 0);
        Node n1 = new Node(1, Node.Type.INPUT, 0);
        Node n2 = new Node(2, Node.Type.OUTPUT, 2);
        Node n3 = new Node(3, Node.Type.HIDDEN, 1);
        Connection c0 = new Connection(0, 0, 2, -2, false);
        Connection c1 = new Connection(1, 1, 2, 3, true);
        Connection c2 = new Connection(2, 0, 3, 1, true);
        Connection c3 = new Connection(3, 3, 2, 0.5, true);
        Genome g0 = new Genome();
        g0.addNodes(n0, n1, n2, n3);
        g0.addConnections(c0, c1, c2, c3);

        assertEquals(g0, g);
    }

    @Test
    public void loadPopulation () throws IOException, URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(testPath+"ex1");
        assertNotNull(url);
        Path path = Paths.get(url.toURI());
        File f = path.toFile();
        InputStream in = new FileInputStream(f);
        LinkedList<Genome> population = SaveFile.loadGenomes(in);
        in.close();

        assertNotNull(population);

        Node n0 = new Node(0, Node.Type.INPUT, 0);
        Node n1 = new Node(1, Node.Type.INPUT, 0);
        Node n2 = new Node(2, Node.Type.OUTPUT, 1);
        Connection c0 = new Connection(0, 0, 2, 0.5);
        Connection c1 = new Connection(1, 1, 2, -1);
        Genome g0 = new Genome();
        g0.addNodes(n0, n1, n2);
        g0.addConnections(c0, c1);

        Node n3 = new Node(0, Node.Type.INPUT, 0);
        Node n4 = new Node(2, Node.Type.OUTPUT, 1);
        Connection c2 = new Connection(2, 0, 2, 4);
        Genome g1 = new Genome();
        g1.addNodes(n3, n4);
        g1.addConnection(c2);

        LinkedList<Genome> population0 = new LinkedList<>();
        population0.add(g0);
        population0.add(g1);

        assertEquals(population0, population);
    }
}
