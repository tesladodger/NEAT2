package com.tesladodger.neat.tools.save;

import com.tesladodger.neat.Connection;
import com.tesladodger.neat.Genome;
import com.tesladodger.neat.Node;
import com.tesladodger.neat.utils.structures.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;


/**
 * Contains static methods to save group of genomes to a file, and to load the created files into
 * respective objects.
 *
 * <p>The essential elements of the genomes are stored in a text file, in an xml-like format. As a
 * wise man once said: "You could shave off a few milliseconds by storing the data in bytes, but
 * this isn't Lockheed Martin. The most productive thing you're gonna do with this is make a few
 * hundred cars go around a track."
 *
 * @author tesla
 */
public class SaveFile {

    /**
     * Prevent instantiation.
     */
    private SaveFile () {}

    /**
     * Save a group of genomes to a file, in a format suitable for loading with
     * {@link SaveFile#loadGenomes(InputStream)}.
     *
     * @param outStream an output stream;
     * @param comments a description of the saved genomes;
     * @param genomes variable number of genomes to save;
     *
     */
    public static void saveGenomes (OutputStream outStream, String comments, Genome... genomes) {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outStream, StandardCharsets.UTF_8));
        if (comments != null) {
            writer.println('#' + comments);
        }
        for (Genome g : genomes) {
            writeGenome(g, writer);
        }
        writer.flush();
    }

    /**
     * Load a group of genomes from a file, saved using
     * {@link SaveFile#saveGenomes(OutputStream, String, Genome...)}.
     *
     * @param inStream an input stream;
     *
     * @return linked list with loaded genomes, null if the file has a structural mistake;
     * @throws IOException if there is a problem reading the file;
     */
    public static LinkedList<Genome> loadGenomes (InputStream inStream) throws IOException {
        LinkedList<Genome> result = new LinkedList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.equals("<Genome>")) {
                Genome g = readGenome(br);
                if (g == null) return null;
                else result.add(g);
            } else if (line.charAt(0) != '#'){
                return null;
            }
        }
        return result;
    }

    private static void writeGenome (Genome genome, PrintWriter writer) {
        writer.println("<Genome>");
        writeNodes(genome.getNodes(), writer);
        writeConnections(genome.getConnections().asOrderedList(), writer);
        writer.println("</Genome>");
    }

    private static void writeNodes (NodeList nodes, PrintWriter writer) {
        for (Node n : nodes) {
            writer.printf("<node id=%d type=%s layer=%d />\n", n.getId(), n.getType(),
                    n.getLayer());
        }
    }

    private static void writeConnections (List<Connection> connections, PrintWriter writer) {
        for (Connection c : connections) {
            writer.printf("<connection id=%d in=%d out=%d weight=%f enabled=%b />\n",
                    c.getInnovationNumber(), c.getInNodeId(), c.getOutNodeId(), c.getWeight(),
                    c.isEnabled());
        }
    }

    private static Genome readGenome (BufferedReader br) throws IOException {
        String line;
        Genome result = new Genome();
        while ((line = br.readLine()) != null) {
            String[] elements = line.split(" ");
            if (line.equals("</Genome>")) {
                return result;
            } else if (elements[0].equals("<node")) {
                result.addNode(readNode(elements));
            } else if (elements[0].equals("<connection")) {
                result.addConnection(readConnection(elements));
            } else {
                return null;
            }
        }
        return null;
    }

    private static Node readNode (String[] line) {
        int id = Integer.parseInt(line[1].substring(3));
        Node.Type type = Node.Type.valueOf(line[2].substring(5));
        int layer = Integer.parseInt(line[3].substring(6));
        return new Node(id, type, layer);
    }

    private static Connection readConnection (String[] line) {
        int id = Integer.parseInt(line[1].substring(3));
        int in = Integer.parseInt(line[2].substring(3));
        int out = Integer.parseInt(line[3].substring(4));
        double weight = Double.parseDouble(line[4].substring(7));
        boolean enabled = Boolean.parseBoolean(line[5].substring(8));
        return new Connection(id, in, out, weight, enabled);
    }
}
