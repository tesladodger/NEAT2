package com.tesladodger.neat.tools.drawer;

import com.tesladodger.neat.Connection;
import com.tesladodger.neat.Genome;
import com.tesladodger.neat.Node;

import javax.swing.*;
import java.awt.*;


/**
 * Draws a {@link Genome} using {@link javax.swing}.
 *
 * @author tesla
 * @version 1.0
 */
public class GenomeDrawer {

    /** Size of the canvas the graph will be drawn on. */
    public int width = 700;

    /** Size of the canvas the graph will be drawn on. */
    public int height = 700;

    /**
     * Constructor for a genome drawer.
     *
     * <p>Can be used to draw multiple genomes, in independent frames.
     */
    public GenomeDrawer () {}

    /**
     * Create a frame with the phenotype.
     *
     * @param genome to display;
     * @param title of the frame;
     */
    public void displayGenome (Genome genome, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // calculate width according to phenotype
        width = Math.max(width, genome.getConnections().size() * GeneFigure.w);

        frame.setSize(new Dimension(width, height));
        frame.setResizable(false);

        NodeFigure[] nFigs = drawNodes(genome, frame, width, height);
        drawConnections(genome, frame, nFigs);
        drawPhenotype(genome, frame);
        frame.setVisible(true);
    }

    private static NodeFigure[] drawNodes (Genome genome, JFrame frame, int width, int height) {
        Node[] nodes = genome.getNodes().asArray();
        NodeFigure[] nFigs = new NodeFigure[nodes.length];

        // number of layers, step of each layer
        int layers = nodes[nodes.length-1].getLayer() + 1;
        float w =.25f * (layers - 2f) / 3 + 0.6f;
        int yStep = (int) (height * w) / (layers - 1);
        int y = height - ((int) (height * (1 - w)) >> 1);

        // draw nodes, for each layer
        int i = 0;
        for (int l = 0; l < layers; l++) {
            // calculate number of nodes in layer
            int nodesInLayer = 0;
            for (int j = i; j < nodes.length && nodes[j].getLayer() == l; j++) {
                nodesInLayer++;
            }

            // step of x coordinate of each node
            int xStep;
            int x;
            if (nodesInLayer == 1) {
                x = width >> 1;
                xStep = 0;
            } else {
                w = (.55f * (nodesInLayer - 2f) / 6f + 0.4f);
                xStep = (int) (width * w) / (nodesInLayer - 1);
                x = (int) (width * (1-w)) >> 1;
            }

            // add nodes to layer
            for (int n = 0; n < nodesInLayer; n++) {
                NodeFigure nF = new NodeFigure(x, y, nodes[i].getId(), nodes[i].getLayer());
                nFigs[i++] = nF;
                add(nF, frame);
                x += xStep;
            }

            // increment y coordinate
            y -= yStep;
        }

        return nFigs;
    }

    private static void drawConnections (Genome genome, JFrame frame, NodeFigure[] nFigs) {
        for (Connection con : genome.getConnections().asOrderedList()) {
            if (!con.isEnabled()) continue;
            // get the nodes
            NodeFigure in = getNode(nFigs, con.getInNodeId());
            NodeFigure out = getNode(nFigs, con.getOutNodeId());
            if (in == null || out == null) continue;
            // normal connection
            if (in.layer < out.layer) {
                add(new ConnectionFigure((float) con.getWeight(), in.x, in.y, out.x, out.y), frame);
            }
            // backwards connection
            else if (in.layer > out.layer) {
                add(new ArcConnectionFigure((float) con.getWeight(), out.x, out.y, in.x, in.y), frame);
            }
            // recursive connection (to the same node)
            else {
                add(new SelfConnectionFigure((float) con.getWeight(), in.x, in.y), frame);
            }
        }
    }

    private static NodeFigure getNode (NodeFigure[] nodes, int nodeId) {
        for (NodeFigure n : nodes) {
            if (n.id == nodeId) {
                return n;
            }
        }
        return null;
    }

    private static void drawPhenotype (Genome genome, JFrame frame) {
        int x = 0;
        for (Connection con : genome.getConnections().asOrderedList()) {
            add(new GeneFigure(
                    x, con.getInnovationNumber(), con.getInNodeId(), con.getOutNodeId(),
                    con.getWeight(), con.isEnabled()
            ), frame);
            x += GeneFigure.w;
        }
    }

    private static void add (GenomeFigure figure, JFrame frame) {
        frame.add(figure);
        frame.setVisible(true);
    }
}
