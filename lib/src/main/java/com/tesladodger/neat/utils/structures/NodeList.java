package com.tesladodger.neat.utils.structures;

import com.tesladodger.neat.Node;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * Special purpose Singly-Linked List that stores ordered Nodes.
 *
 * @author tesla
 */
public class NodeList implements Iterable<Node> {

    private Member root;

    /** Number of nodes on this list. */
    private int size;

    /**
     * Default constructor, creates an empty list.
     */
    public NodeList () {
        size = 0;
    }

    /**
     * Add a {@link Node} to the list.
     *
     * <p>The added node will be placed in the correct index, according to
     * {@link Node#compareTo(Node)}.
     *
     * @param node to add;
     */
    public void add (Node node) {
        if (isEmpty() || root.node.compareTo(node) < 0) {
            root = new Member(node, root);
        } else {
            Member current = root;
            while (current.next != null && current.next.node.compareTo(node) > 0) {
                current = current.next;
            }
            current.next = new Member(node, current.next);
        }
        size++;
    }

    /**
     * Get the node with the given nodeId.
     *
     * @param nodeId of the node to return;
     *
     * @return node, null if not present;
     */
    public Node get (int nodeId) {
        for (Node n : this) {
            if (n.getId() == nodeId) {
                return n;
            }
        }
        return null;
    }

    /**
     * Returns a collection containing all the outputs in this list.
     *
     * @return list of outputs;
     */
    public List<Node> getOutputs () {
        List<Node> result = new LinkedList<>();
        for (Node n : this) {
            if (n.getType().equals(Node.Type.OUTPUT)) {
                result.add(n);
            }
        }
        return result;
    }

    /**
     * Assert if there's any node with a given {@link Node#getId()} on this list.
     *
     * @param nodeId to search for;
     *
     * @return true if node is present, false otherwise;
     */
    public boolean containsId (int nodeId) {
        for (Node n : this) {
            if (n.getId() == nodeId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sort the elements in this list according to {@link Node#compareTo(Node)}.
     *
     * <p>If the elements in this list are modified, the order might be broken. This can happen
     * when fixing the layer of nodes after mutation.
     */
    public void sort () {
        Node[] temp = asArray();
        clear();
        for (Node n : temp) {
            add(n);
        }
    }

    /**
     * @return number of {@link Node}s on this list;
     */
    public int size () {
        return size;
    }

    /**
     * Remove all elements from this list.
     */
    public void clear () {
        root = null;
        size = 0;
    }

    /**
     * Returns a shallow copy of this list as a Node[].
     *
     * @return array with the elements of this list;
     */
    public Node[] asArray () {
        Node[] result = new Node[size];
        int i = 0;
        for (Node n : this) {
            result[i++] = n;
        }
        return result;
    }

    /**
     * @return true if there are no connections on this table, false otherwise;
     */
    public boolean isEmpty () {
        return size == 0;
    }

    /**
     * Returns an iterator for this list's nodes.
     *
     * <p>The order of the returned nodes is the one established by the
     * {@link Node#compareTo(Node)} method.
     *
     * @return iterator for this list;
     */
    @Override
    public Iterator<Node> iterator () {
        return new NodeListIterator();
    }

    /**
     * Iterator for {@link NodeList}. Only supports {@code next}.
     */
    private class NodeListIterator implements Iterator<Node> {
        private Member next;

        private NodeListIterator () {
            next = root;
        }

        @Override
        public boolean hasNext () {
            return next != null;
        }

        @Override
        public Node next () {
            if (!hasNext()) {
                throw new NoSuchElementException("The iterator has no next element.");
            }

            Node result = next.node;
            next = next.next;  // nice
            return result;
        }
    }

    /**
     * Represents a member of this list. Contains a Node and a reference to the next member.
     */
    private static class Member {
        private final Node node;
        private Member next;

        private Member (Node node, Member next) {
            this.node = node;
            this.next = next;
        }
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeList x = (NodeList) o;
        if (size != x.size) return false;
        Iterator<Node> it = iterator();
        Iterator<Node> xit = x.iterator();
        while (it.hasNext()) {
            if (!it.next().equals(xit.next())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString () {
        Iterator<Node> it = iterator();
        if (!it.hasNext()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            sb.append(it.next());
            if (!it.hasNext()) {
                return sb.append(']').toString();
            }
            sb.append(',').append(' ');
        }
    }
}
