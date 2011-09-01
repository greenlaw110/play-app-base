/**
 * S.java
 * 
 * Defines handy methods for String manipulating
 * 
 * @version 1.0 greenlaw110@gmail.com - initial version
 */
package utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;

import play.Logger;
import play.exceptions.UnexpectedException;
import play.libs.Codec;
import play.modules.betterlogs.NoTrace;

@NoTrace
public class S {

    /**
     * Define an instance to be used in views 
     */
    public final static S instance = new S();

    /**
     * Get string representation of an object instance
     * 
     * @param o the instance to be displayed
     * @param quoted whether display quotation mark
     */
    public final static String str(Object o, boolean quoted) {
        return quoted ? String.format("\"%s\"", o) : null == o ? "" : o.toString();
    }

    /**
     * Join a list of object into a string
     * 
     * @param separator the symbol used to separate the listed itmes
     * @param list a list of object instances
     * @return a string representation of the listed objects
     */
    public static String join(String separator, Collection<?> list) {
        return join(separator, null, null, list);
    }

    /**
     * Join a list of object into a string, prefix and suffix will be added if supplied
     * 
     * @param separator the symbols used to separate the listed items and prefix and suffix
     * @param prefix the symbols prepended to the beginning of the item list
     * @param suffix the symbols appended to the end of the item list
     * @param list the string representation of the listed objects
     * @return a string representation of the listed objects
     */
    public static String join(String separator, String prefix, String suffix,
            Collection<?> list) {
        return join(separator, prefix, suffix, list, false, true);
    }

    /**
     * Join a list of object into a string, prefix and suffix will be added if supplied
     * 
     * @param separator the symbols used to separate the listed items and prefix and suffix
     * @param prefix the symbols prepended to the beginning of the item list
     * @param suffix the symbols appended to the end of the item list
     * @param list the string representation of the listed objects
     * @param quoted if true then each listed item will be quoted with quotation mark
     * @param separateFixes if false then no separator after prefix and no separator before suffix
     * @return a string representation of the listed objects
     */
    public static String join(String separator, String prefix, String suffix,
            Collection<?> list, boolean quoted, boolean separateFixes) {
        StringBuilder sb = new StringBuilder();

        if (null != prefix) {
            sb.append(prefix);
            if (separateFixes)
                sb.append(separator);
        }

        Iterator<?> itr = list.iterator();
        if (itr.hasNext())
            sb.append(str(itr.next(), quoted));
        while (itr.hasNext()) {
            sb.append(separator).append(str(itr.next(), quoted));
        }

        if (null != suffix) {
            if (separateFixes)
                sb.append(separator);
            sb.append(suffix);
        }
        return sb.toString();
    }

    /**
     * Join an array of strings into a string
     * 
     * @param separator the symbol used to separate the listed itmes
     * @param list the array of strings
     * @return a string joined
     */
    public static String join(String separator, String... list) {
        StringBuilder sb = new StringBuilder();

        if (list.length > 0) {
            sb.append(list[0]);
            for (int i = 1; i < list.length; ++i)
                sb.append(separator).append(list[i]);
        }

        return sb.toString();
    }

    /**
     * <p>Return a string no longer than specified max length.
     * 
     * <p>If the string supplied is longer than the specified max length
     * then only it's part that is less than the max length returned, appended
     * with "..."
     * 
     * @param s the original string
     * @param max the maximum length of the result
     * @return
     */
    public static String max(String s, int max) {
        return maxSize(s, max);
    }

    /**
     * <p>Return a string no longer than specified max length.
     * 
     * <p>If the string supplied is longer than the specified max length
     * then only it's part that is less than the max length returned, appended
     * with "..."
     * 
     * @param s the original string
     * @param max the maximum length of the result
     * @return
     */
    public static String maxSize(String s, int max) {
        if (null == s)
            return "";
        if (s.length() < (max - 3))
            return s;
        String s0 = s.substring(0, max);
        return s0 + "...";
    }

    /**
     * Decode Base64 encoded string
     * 
     * @param str
     * @return decoded string
     */
    public static String decodeBase64(String str) {
        try {
            return new String(Codec.decodeBASE64(str), "utf-8");
        } catch (UnsupportedEncodingException e) {
            // shouldn't occur
            throw new RuntimeException(e);
        }
    }

    /**
     * Encode a string using Base64 encoding
     * 
     * @param str
     * @return encoded string
     */
    public static String encodeBase64(String str) {
        return Codec.encodeBASE64(str);
    }

    /**
     * Determine if a string is all blank or empty or null
     * 
     * @param s
     * @return true if the string is null or empty or all blanks
     */
    public static boolean isEmpty(String s) {
        if (null == s || "".equals(s.trim()))
            return true;
        return false;
    }

    public static final int IGNORECASE = 0x00001000;
    public static final int IGNORESPACE = 0x00002000;

    /**
     * Return true if 2 strings are equals to each other
     * @param s1
     * @param s2
     * @return
     */
    public static boolean isEqual(String s1, String s2) {
        return isEqual(s1, s2, 0);
    }

    /**
     * Return true if 2 strings are equals to each other as per rule specified
     * 
     * @param s1
     * @param s2
     * @param modifier could be combination of {@link IGNORESPACE} or {@link IGNORECASE}
     * @return
     */
    public static boolean isEqual(String s1, String s2, int modifier) {
        if (null == s1) {
            return s2 == null;
        }
        if (null == s2)
            return false;
        if ((modifier & IGNORESPACE) != 0) {
            s1 = s1.trim();
            s2 = s2.trim();
        }
        if ((modifier & IGNORECASE) != 0) {
            return s1.equalsIgnoreCase(s2);
        } else {
            return s1.equals(s2);
        }
    }

    /**
     * perform URL encoding on a giving string
     * 
     * @param s
     * @return
     */
    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (Exception e) {
            throw new UnexpectedException(e);
        }
    }

    /**
     * perform csv encoding on a giving string
     * @param s
     * @return
     */
    // Escape a csv string following
    // http://en.wikipedia.org/wiki/Comma-separated_values
    public static String csvEncode(String s) {
        // fields that contain commas, double-quotes, or line-breaks must be
        // quoted
        if (s.contains("\"") || s.contains(",") || s.contains("\n")
                || s.contains("\r")) {
            // a quote within a field must be escaped with an additional quote
            // immediately preceding the literal quote.
            StringBuilder sb = new StringBuilder("\"").append(
                    s.replace("\"", "\"\"")).append("\"");
            return sb.toString();
        } else {
            return s;
        }
    }

}
