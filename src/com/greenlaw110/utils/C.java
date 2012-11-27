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
    public final static boolean isArrayOrIterable(Object o) {
        if (o.getClass().isArray()) return true;
        if (o instanceof Iterable) return true;
        return false;
    }

    /**
     * Return a unique set from a collection
     * @param l
     * @return
     */
    public final static Set unique(Collection l) {
        return new TreeSet(l);
    }

    public <T> Iterable<T> reverse(final List<T> l) {
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
