/**
 * JavaExtensions.java
 * 
 * Add handy methods to objects used in view
 * 
 * @version 1.0 greenlaw110@gmail.com - initial version
 */
package templates;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import play.i18n.Messages;
import play.modules.betterlogs.NoTrace;
import utils.S;

@NoTrace
public class JavaExtensions extends play.templates.JavaExtensions {
    public static String maxLength(String string, Integer len) {
        return S.max(string, len);
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
    
    public static String csv(String string) {
        return S.csvEncode(string);
    }
    
    public static SortedSet sort(Set<? extends Comparable> set) {
        return new TreeSet(set);
    }
    
    public static Set unique(Iterable itr) {
        Set set = new HashSet();
        for (Object o: itr) {
            set.add(o);
        }
        return set;
    }

}
