package com.tesladodger.neat.utils.structures;

import com.tesladodger.neat.Connection;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


/**
 * Special purpose {@code hash table}, that returns all {@link Connection}s departing from a
 * given node id. Not having to search for every connection that departs from a node, which is a
 * necessary operation for {@link com.tesladodger.neat.Genome#calculateOutput}, for all nodes,
 * significantly reduces the time complexity of that method.
 *
 * <p>There's no actual hashing involved, the in-node id of the connection is the key, so the
 * size of table should be equal to the number of nodes in the genome, plus 40% to account for
 * mutation. For this reason, node IDs should start at 0 and be incremented, so that an array out
 * of bounds exception is avoided and no indexes are wasted in the internal array. The structure
 * will grow if needed, but it's best to prevent
 * that since it's expensive.
 *
 * @author tesla
 * @version 1.0
 */
public class ConnectionHashTable {

    /** Default initial capacity. */
    private static final int DEFAULT_CAPACITY = 14;

    /**
     * Each index of this array of {@link ConnectionBucket}s corresponds to the id of the in-node
     * in all {@link Connection}s of that bucket.
     */
    private ConnectionBucket[] buckets;

    /**
     * Copy of this list in the form of a LinkedList, ordered by
     * {@link Connection#getInnovationNumber()}.
     */
    private final LinkedList<Connection> orderedConnections;

    /** Total number of connections. */
    private int size;

    /**
     * Default constructor.
     *
     * <p>Constructs a table with a default initial capacity of 14.
     */
    public ConnectionHashTable () {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Construct a table with an initial capacity.
     *
     * <p>Note the initial capacity is not the maximum number of connections, it's the highest
     * node ID. This number will be equal to the number of nodes in the genome minus one, if node
     * id numbering follows convention, starting at 0. Not starting at 0 implies wasted indexes
     * in this table.
     *
     * @param initialCapacity of this table;
     */
    public ConnectionHashTable (int initialCapacity) {
        int initialSize = Math.max(14, initialCapacity+1);
        buckets = new ConnectionBucket[initialSize];
        orderedConnections = new LinkedList<>();
        size = 0;
    }

    /**
     * Add a connection to this table.
     *
     * @param connection to add;
     */
    public void addConnection (Connection connection) {
        addConnection0(connection);
        addConnection1(connection);
        size++;
    }

    /**
     * Add a new connection to the buckets.
     *
     * @param con connection to add;
     */
    void addConnection0 (Connection con) {
        int index = con.getInNodeId();
        if (index >= buckets.length) {
            increaseCapacity((int) (index + (index * .4)));
        }
        if (buckets[index] == null) {
            buckets[index] = new ConnectionBucket();
        }
        buckets[index].add(con);
    }

    /**
     * Add a connection to the ordered list.
     *
     * @param con to add;
     */
    void addConnection1 (Connection con) {
        ListIterator<Connection> it = orderedConnections.listIterator();
        while (true) {
            if (!it.hasNext()) {
                it.add(con);
                return;
            }
            Connection next = it.next();
            if (next.compareTo(con) < 0) {
                it.previous();
                it.add(con);
                return;
            }
        }
    }

    /**
     * Add multiple connections to this table.
     *
     * @param connections to add;
     */
    public void addConnections (Connection... connections) {
        for (Connection con : connections) {
            addConnection(con);
        }
    }

    /**
     * Increase the capacity of this table to a new highest node ID.
     *
     * @param newCapacity highest node-id;
     */
    void increaseCapacity (int newCapacity) {
        ConnectionBucket[] newBuckets = new ConnectionBucket[newCapacity];
        System.arraycopy(buckets, 0, newBuckets, 0, buckets.length);
        buckets = newBuckets;
    }

    /**
     * Get an iterator for the connections in this table with the given id.
     *
     * @param inNodeId to get connections from;
     *
     * @return iterator for connections, empty if there are none;
     */
    public Iterable<Connection> getConnectionsFrom (int inNodeId) {
        return inNodeId >= capacity() || buckets[inNodeId] == null ?
                new ConnectionBucket() :
                buckets[inNodeId];
    }

    /**
     * Check whether this table contains a connection with the given in and out node IDs.
     *
     * @param inNodeId id of the input node;
     * @param outNodeId id of the output node;
     *
     * @return true if there is a connection with those characteristics in this list, false
     * otherwise;
     */
    public boolean containsConnection (int inNodeId, int outNodeId) {
        if (inNodeId >= capacity() || buckets[inNodeId] == null) {
            return false;
        }
        for (Connection c : buckets[inNodeId]) {
            if (c.getOutNodeId() == outNodeId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the {@link Connection}s on this table in an array.
     *
     * <p>The caller is free to modify the array without modifying this table.
     *
     * @return array of connections;
     */
    public Connection[] asArray () {
        return orderedConnections.toArray(new Connection[size()]);
    }

    /**
     * Get the {@link Connection}s on this table, ordered by
     * {@link Connection#getInnovationNumber()}.
     *
     * <p>Returns an unmodifiable view of the internal list in this table.
     *
     * @return Unmodifiable List containing the ordered connections;
     */
    public List<Connection> asOrderedList () {
        return Collections.unmodifiableList(orderedConnections);
    }

    /**
     * @return number of {@link Connection}s on this table;
     */
    public int size () {
        return size;
    }

    /**
     * @return capacity (highest node id) of this table;
     */
    int capacity () {
        return buckets.length;
    }

    /**
     * @return true if there are no connections on this table, false otherwise;
     */
    public boolean isEmpty () {
        return size == 0;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionHashTable x = (ConnectionHashTable) o;
        if (size != x.size) return false;
        Connection[] ta = asArray();
        Connection[] xa = x.asArray();
        for (int i = 0; i < size; i++) {
            if (!ta[i].equals(xa[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString () {
        if (isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        Iterator<Connection> it = asOrderedList().iterator();
        for (;;) {
            sb.append(it.next());
            if (!it.hasNext()) {
                return sb.append(']').toString();
            }
            sb.append(',').append(' ');
        }
    }

    /**
     * Group of connections that have the same in-node.
     *
     * <p>Implemented as singly-linked list, only supports iterating over the elements.
     *
     * <p>No particular order is imposed to the elements returned by the iterator.
     */
    static class ConnectionBucket implements Iterable<Connection> {

        private ConnectionBucketElement root;

        int size;

        private void add (Connection con) {
            root = new ConnectionBucketElement(con, root);
            size++;
        }

        @Override
        public Iterator<Connection> iterator () {
            return new ConnectionBucketIterator(root);
        }

        static class ConnectionBucketIterator implements Iterator<Connection> {

            private ConnectionBucketElement next;

            private ConnectionBucketIterator (ConnectionBucketElement root) {
                next = root;
            }

            @Override
            public boolean hasNext () {
                return next != null;
            }

            @Override
            public Connection next () {
                Connection value = next.value;
                next = next.next;  // nice
                return value;
            }
        }

        /**
         * An element in a bucket: contains a value and a reference to the next element.
         */
        static class ConnectionBucketElement {
            private final Connection value;
            private final ConnectionBucketElement next;

            private ConnectionBucketElement (Connection value, ConnectionBucketElement next) {
                this.value = value;
                this.next = next;
            }
        }
    }
}
