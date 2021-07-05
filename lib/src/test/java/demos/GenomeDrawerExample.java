package demos;

import com.tesladodger.neat.Connection;
import com.tesladodger.neat.Genome;
import com.tesladodger.neat.Node;
import com.tesladodger.neat.tools.drawer.GenomeDrawer;

import java.util.Scanner;


public class GenomeDrawerExample {

    private static void exampleA () {
        Node n0 = new Node(0, Node.Type.INPUT, 0);
        Node n1 = new Node(1, Node.Type.INPUT, 0);
        Node n2 = new Node(2, Node.Type.INPUT, 0);
        Node n3 = new Node(3, Node.Type.OUTPUT, 2);
        Node n4 = new Node(4, Node.Type.HIDDEN, 1);

        Connection c0 = new Connection(0, 0, 3, 5);
        Connection c1 = new Connection(1, 1, 3, -2, false);
        Connection c2 = new Connection(2, 2, 3, -10);
        Connection c3 = new Connection(3, 1, 4, 8);
        Connection c4 = new Connection(4, 4, 3, 2);
        Connection c5 = new Connection(5, 0, 4, -0.8);
        Connection c6 = new Connection(6, 3, 4, 5);
        Connection c7 = new Connection(7, 3, 3, -1);
        Connection c8 = new Connection(8, 3, 0, 5);
        Connection c9 = new Connection(9, 3, 2, -5);

        Genome genny = new Genome();
        genny.addNodes(n0, n1, n2, n3, n4);
        genny.addConnections(c0, c1, c2, c3, c4, c5, c6, c7, c8, c9);
        GenomeDrawer gd = new GenomeDrawer();
        gd.displayGenome(genny, "Example A");
    }

    private static void exampleB () {
        Node na0 = new Node(0, Node.Type.INPUT);
        Node na1 = new Node(1, Node.Type.INPUT);
        Node na2 = new Node(2, Node.Type.INPUT);
        Node na3 = new Node(3, Node.Type.OUTPUT, 2);
        Node na4 = new Node(4, Node.Type.HIDDEN, 1);
        Connection ca0 = new Connection(0, 0, 3, 0.2);
        Connection ca1 = new Connection(1, 1, 3, 0.0, false);
        Connection ca2 = new Connection(2, 2, 3, -0.2);
        Connection ca3 = new Connection(3, 1, 4, 2);
        Connection ca4 = new Connection(4, 4, 3, -3);
        Connection ca5 = new Connection(7, 0, 4, 10);
        Genome ga = new Genome();
        ga.addNodes(na0, na1, na2, na3, na4);
        ga.addConnections(ca0, ca1, ca2, ca3, ca4, ca5);
        GenomeDrawer gd = new GenomeDrawer();
        gd.displayGenome(ga, "Example B");
    }

    private static void exampleC () {
        Node nb0 = new Node(0, Node.Type.INPUT);
        Node nb1 = new Node(1, Node.Type.INPUT);
        Node nb2 = new Node(2, Node.Type.INPUT);
        Node nb3 = new Node(3, Node.Type.OUTPUT, 3);
        Node nb4 = new Node(4, Node.Type.HIDDEN, 1);
        Node nb5 = new Node(5, Node.Type.HIDDEN, 2);
        Connection cb0 = new Connection(0, 0, 3);
        Connection cb1 = new Connection(1, 1, 3, 0.0, false);
        Connection cb2 = new Connection(2, 2, 3);
        Connection cb3 = new Connection(3, 1, 4);
        Connection cb4 = new Connection(4, 4, 3, 0.0, false);
        Connection cb5 = new Connection(5, 4, 5);
        Connection cb6 = new Connection(6, 5, 3);
        Connection cb7 = new Connection(8, 2, 4);
        Connection cb8 = new Connection(9, 0, 5);
        Genome gb = new Genome();
        gb.addNodes(nb0, nb1, nb2, nb3, nb4, nb5);
        gb.addConnections(cb0, cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8);
        GenomeDrawer gd = new GenomeDrawer();
        gd.displayGenome(gb, "Example C");
    }

    public static void main (String[] args) {
        System.out.println("Indicate the examples you want printed, separated by a space: [a, b, " +
                "c]");
        Scanner sc = new Scanner(System.in);
        String[] ex = sc.nextLine().split(" ");
        sc.close();
        for (String x : ex) {
            switch (x) {
                case "a" -> exampleA();
                case "b" -> exampleB();
                case "c" -> exampleC();
            }
        }
    }
}
