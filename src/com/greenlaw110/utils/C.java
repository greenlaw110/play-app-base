package com.greenlaw110.utils;

import java.util.*;

/**
 * A set of Collection relevant methods
 */
public class C {
    /**
     * Return true if the object specified can be used in for (T e: o)
     * @param o
     * @return
     */
    public static boolean isArrayOrIterable(Object o) {
        if (o.getClass().isArray()) return true;
        if (o instanceof Iterable) return true;
        return false;
    }

    /**
     * Return a unique set from a collection
     * @param l
     * @return
     */
    public static <T> Set unique(Collection<T> l) {
        return new TreeSet<T>(l);
    }

    /**
     * Generate an unmodifiable list from a list of arguments
     *
     * For example: List<String> ls = C.unmodifiableList("a", "b", "c");
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> List<T> unmodifiableList(T... t) {
        return Collections.unmodifiableList(Arrays.asList(t));
    }

    /**
     * Alias of unmodifiableList
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> List<T> list(T ... t) {
        return Collections.unmodifiableList(Arrays.asList(t));
    }

    public static <T> Iterable<T> reverse(final List<T> l) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    int cursor = l.size() - 1;
                    @Override
                    public boolean hasNext() {
                        return cursor > -1;
                    }

                    @Override
                    public T next() {
                        return l.get(cursor--);
                    }

                    @Override
                    public void remove() {
                        l.remove(cursor);
                    }
                };
            }
        };
    }

}
