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
import play.modules.betterlogs.NoTrace;
import play.templates.GroovyTemplate.ExecutableTemplate;
import play.templates.JavaExtensions;
import utils.FeatureAccess;
import utils.FeatureAccess.Role;
import utils.S;

@play.templates.FastTags.Namespace("app")
@NoTrace
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
}
