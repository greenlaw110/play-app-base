/**
 * JavaExtensions.java
 *
 * Add handy methods to objects used in view
 *
 * @version 1.0 greenlaw110@gmail.com - initial version
 */
package templates;

import org.osgl.util.C;
import org.osgl.util.S;
import play.i18n.Messages;
import play.modules.betterlogs.NoTrace;

import java.util.*;

@NoTrace
public class JavaExtensions extends play.templates.JavaExtensions {
    public static String maxLength(String string, Integer len) {
        return S.maxLength(string, len);
    }

    public static String days(Date date) {
        Date now = new Date();
        if (now.before(date)) {
            return "";
        }
        long delta = (now.getTime() - date.getTime()) / 1000;
        long days = delta / (24 * 60 * 60);
        if (days < 1) days = 1;
        return Messages.get("DAYS_OLD", days);
    }

    public static String underscore(String string) {
        return underscore(string, Boolean.TRUE);
    }

    public static String underscore(String string, Boolean lowercase) {
        string = noAccents(string);
        // Apostrophes.
        string = string.replaceAll("([a-z])'s([^a-z])", "$1s$2");
        string = string.replaceAll("[^\\w\\-]", "_").replaceAll("-{2,}", "_");
        // Get rid of any - at the start and end.
        string.replaceAll("-+$", "").replaceAll("^-+", "");

        return (lowercase ? string.toLowerCase() : string);
    }

    // ref: http://en.wikipedia.org/wiki/Comma-separated_values
    public static String csv(String s) {
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

    public static SortedSet sort(Set<? extends Comparable> set) {
        return new TreeSet(set);
    }

    public static Set unique(Iterable itr) {
        return C.set(itr);
    }

}
