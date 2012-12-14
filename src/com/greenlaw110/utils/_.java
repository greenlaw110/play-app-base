/**
 * _.java
 *
 * Defines handy methods for common java programming tasks
 *
 * @version 1.0 greenlaw110@gmail.com - initial version
 */
package com.greenlaw110.utils;

public class _ {
    /**
     * Defines an instance to be used in views
     */
    public final _ instance = new _();

    /**
     * Throw out NullPointerException if any one of the passed objects is null
     *
     * @param args the object instances to be tested
     */
    public final static void NPE(Object... args) {
        for (Object o: args) {
            if (null == o) throw new NullPointerException();
        }
    }

    public final static void illegalState() {
        throw new IllegalStateException();
    }

    public final static void illegalState(String message) {
        throw new IllegalStateException(message);
    }

    public final static String str(Object o, boolean quoted) {
        return S.str(o, quoted);
    }

    public final static String str(Object o) {
        return S.str(o, false);
    }

    public final static boolean isEqual(Object a, Object b) {
        if (a == b) return true;
        if (null == a) return b != null;
        else return a.equals(b);
    }

    public final static boolean isNotEqual(Object a, Object b) {
        return !isEqual(a, b);
    }

    /**
     * @deprecated use hc(Object... args) instead
     * @param property
     * @param hashCode
     * @return
     */
    public final static int hashCode(Object property, int hashCode) {
        return 31 * hashCode + ((null == property) ? 0 : property.hashCode());
    }

    public final static int hc(Object... args) {
        int i = 17;
        for (Object o: args) {
            i = 31 * i + ((null == o) ? 0 : o.hashCode());
        }
        return i;
    }

}