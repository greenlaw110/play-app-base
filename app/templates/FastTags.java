/**
 * FastTags.java
 *
 * Defines common used tags
 *
 * @version 1.0 greenlaw110@gmail.com - initial version
 */
package templates;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.util.Map;

import play.exceptions.TagInternalException;
import play.exceptions.TemplateExecutionException;
import play.i18n.Messages;
import play.templates.GroovyTemplate.ExecutableTemplate;
import play.templates.JavaExtensions;
import com.greenlaw110.play.utils.FeatureAccess;
import com.greenlaw110.play.utils.FeatureAccess.Role;
import com.greenlaw110.utils.S;

@play.templates.FastTags.Namespace("app")
public class FastTags extends play.templates.FastTags {
    public static void _jsMessages(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) {
        String msgs = args.get("arg").toString();
        if (S.isEmpty(msgs)) {
            throw new TemplateExecutionException(template.template, fromLine, "Specify messages", new TagInternalException("Specify a template name"));
        }

        final String SEP = "[ :]";
        String[] sa = msgs.split(SEP);
        out.print("var _MESSAGES = {__:'PLACEHOLDER'");
        for (String s: sa) {
            out.print("," + s + ":'" + Messages.get(s) + "'");
        }
        out.print("};");
    }

    public static void _devOnly(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) {
        if (FeatureAccess.hasAccess(Role.DEV)) {
            out.print(JavaExtensions.toString(body));
        }
    }

    public static void _tstOnly(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) {
        if (FeatureAccess.hasAccess(Role.TESTER)) {
            out.print(JavaExtensions.toString(body));
        }
    }

    protected static Object getObj(Map<?, ?> args, String key, Object def) {
        Object arg = args.get(key);
        return null == arg ? def : arg;
    }

    protected static String getStr(Map<?, ?> args, String key, String def) {
        Object arg = args.get(key);
        return null == arg ? def : arg.toString();
    }

    protected static boolean getBool(Map<?, ?> args, String key, boolean def) {
        Object arg = args.get(key);
        if (arg instanceof Boolean) {
            return (Boolean)arg;
        } else if (null != arg) {
            return Boolean.valueOf(arg.toString());
        }
        return def;
    }

    protected static int getInt(Map<?, ?> args, String key, int def) {
        Object arg = args.get(key);
        if (arg instanceof Integer) {
            return (Integer)arg;
        } else if (null != arg) {
            return Integer.valueOf(arg.toString());
        }
        return def;
    }
}
