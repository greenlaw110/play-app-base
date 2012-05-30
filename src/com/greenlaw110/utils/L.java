/**
 * L.java
 *
 * Defines handy methods for List manipulating
 *
 * @version 1.0 greenlaw110@gmail.com - initial version
 */
package com.greenlaw110.utils;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class L {
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
}
