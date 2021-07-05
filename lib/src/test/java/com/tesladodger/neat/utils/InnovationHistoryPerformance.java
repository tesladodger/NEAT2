package com.tesladodger.neat.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;


/**
 * Testing performance (insertions per unit of time and collisions) of different hashing functions.
 *
 * <p>While Cantor Pairing predictably gives the best collision avoidance, its insertion time is
 * worse. A good compromise is {@code a * prime0 + b * prime1}. I ended up choosing
 * {@code a ^ ((b << 16) | (b >> 16))} because bitwise operations look cool.
 */
public class InnovationHistoryPerformance {

    static double insertsPerMilli (TestHistory h, int inserts,
                                   BiFunction<Integer, Integer, Integer> hash) {
        int x = inserts;
        long t = System.currentTimeMillis();
        while (x-- > 0) {
            h.getInnovNum(ThreadLocalRandom.current().nextInt(100), x, hash);
        }
        t = System.currentTimeMillis() - t;
        return (double) inserts / (double) t;
    }

    static double averageInsertsPerMilli (int inserts, BiFunction<Integer, Integer, Integer> hash) throws Exception {
        double sum = 0;
        int tests = 20;
        TestHistory h = new TestHistory();
        for (int i = 0; i < 20; i++) {
            h = new TestHistory();
            for (int j = 0; j < 100_000; j++) {
                h.getInnovNum(
                        ThreadLocalRandom.current().nextInt(100), j, hash);
            }
            sum += insertsPerMilli(h, inserts, hash);
        }
        h.mutations.dumpStats();
        return sum / tests;
    }

    public static void main (String[] args) throws Exception {
        System.out.println("Testing function 1:");
        for (int i = 200_000; i <= 1_000_000; i += 100_000) {
            System.out.println();
            System.out.printf("Inserts: %8d   Average inserts/ms: %.1f\n", i,
                    averageInsertsPerMilli(i, (a, b) -> {
                        int result = 23;
                        result = result * 37 + a;
                        result = result * 37 + b;
                        return result;
                    }));
        }
        System.out.println();

        System.out.println("Testing function 2:");
        for (int i = 200_000; i <= 1_000_000; i += 100_000) {
            System.out.println();
            System.out.printf("Inserts: %8d   Average inserts/ms: %.1f\n", i,
                    averageInsertsPerMilli(i, (a, b) -> {
                        int result = 851 + a;
                        result = result * 37 + b;
                        return result;
                    }));
        }
        System.out.println();

        System.out.println("Testing function 3:");
        for (int i = 200_000; i <= 1_000_000; i += 100_000) {
            System.out.println();
            System.out.printf("Inserts: %8d   Average inserts/ms: %.1f\n", i,
                    averageInsertsPerMilli(i, (a, b) -> {
                        int result = 31 + a;
                        result = result * 37 + b;
                        return result;
                    }));
        }
        System.out.println();

        System.out.println("Testing function 4:");
        for (int i = 200_000; i <= 1_000_000; i += 100_000) {
            System.out.println();
            System.out.printf("Inserts: %8d   Average inserts/ms: %.1f\n", i,
                    averageInsertsPerMilli(i, (a, b) -> a * 37 + b * 31));
        }
        System.out.println();

        System.out.println("Testing function 5:");
        for (int i = 200_000; i <= 1_000_000; i += 100_000) {
            System.out.println();
            System.out.printf("Inserts: %8d   Average inserts/ms: %.1f\n", i,
                    averageInsertsPerMilli(i, (a, b) -> (int) ((a + b) * (a + b + 1) * .5 + b)));
        }
        System.out.println();

        System.out.println("Testing function 6:");
        for (int i = 200_000; i <= 1_000_000; i += 100_000) {
            System.out.println();
            System.out.printf("Inserts: %8d   Average inserts/ms: %.1f\n", i,
                    averageInsertsPerMilli(i, (a, b) -> a ^ ((b << 16) | (b >> 16))));
        }
        System.out.println();
    }

    static class TestHistory {
        SubHashMap<Key, Integer> mutations = new SubHashMap<>();
        int counter = 0;

        void getInnovNum (int in, int out, BiFunction<Integer, Integer, Integer> hash) {
            mutations.computeIfAbsent(new Key(in, out, hash), k -> ++counter);
        }

        static class Key {
            private final int inNodeId;
            private final int outNodeId;
            private final int hash;

            private Key (int inNodeId, int outNodeId, BiFunction<Integer, Integer, Integer> hash) {
                this.inNodeId = inNodeId;
                this.outNodeId = outNodeId;
                this.hash = hash.apply(inNodeId, outNodeId);
            }

            @Override
            public boolean equals (Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Key that = (Key) o;
                return inNodeId == that.inNodeId && outNodeId == that.outNodeId;
            }

            @Override
            public int hashCode () {
                return hash;
            }
        }
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    static class SubHashMap<K, V> extends HashMap<K, V> {

        public void dumpStats () throws Exception {

            Field f = HashMap.class.getDeclaredField("table");
            f.setAccessible(true);

            Map.Entry<K, V>[] table = (Map.Entry<K, V>[]) f.get(this);

            Class<?> hashMapEntryClass = null;
            for (Class<?> c : HashMap.class.getDeclaredClasses())
                if ("java.util.HashMap.Node".equals(c.getCanonicalName()))
                    hashMapEntryClass = c;

            Field nextField = hashMapEntryClass.getDeclaredField("next");
            nextField.setAccessible(true);

            int emptyBuckets = 0;
            int fullBuckets = 0;
            int entrySum = 0;
            for (Entry<K, V> entry : table) {
                if (entry == null) {
                    emptyBuckets++;
                } else {
                    fullBuckets++;
                    while (entry != null) {
                        entrySum++;
                        entry = (Entry<K, V>) nextField.get(entry);
                    }
                }
            }

            System.out.printf("Table Length: %8d    Empty Buckets: %3.2f    Entries/Bucket: %.2f\n",
                    table.length, (double) emptyBuckets / (double) table.length,
                    ((double) entrySum / (double) fullBuckets));
        }
    }
}
