/**
 * Config.java
 * 
 * Bindings controller provide filters to add miscellaneous variables to RenderArgs
 *  
 * @version 1.0 greenlaw110@gmail.com - intial version
 */

package controllers.filters;

import java.util.Properties;

import javax.inject.Inject;

import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope.RenderArgs;
import api.IApplication;
import api.IUser;

public class Config extends Controller implements IFilter {

    public static boolean ssl;
    public static String scheme;
    public static boolean postSsl;
    public static String postScheme;
    public static String domain;
    public static String context;
    public static String homeUrl; // equals to protocol://homePath
    public static String homePath;

    public static boolean debugOn = false;
    public static String developers = null;
    
    @Inject
    public static IApplication app = null;

//    private static void errorIfNull_(String key, String val) {
//        if (null == val)
//            throw new ConfigurationException(String.format("%s not configured",
//                    key));
//        Logger.info("loading config [%s]: %s", key, val);
//    }
//
//    private static void info_(String key, String val) {
//        Logger.info("loading config[%s]: %s", key, val);
//    }
//
    @OnApplicationStart
    public static class BootLoader extends Job<Object> {
        @Override
        public void doJob() {
            Properties c = Play.configuration;
            ssl = Boolean.parseBoolean(c.getProperty("app.ssl", "false"));
            postSsl = Boolean.parseBoolean(c
                    .getProperty("app.post_ssl", "true"));
            scheme = ssl ? "https" : "http";
            postScheme = postSsl ? "https" : "http";
            domain = c.getProperty("app.domain");
            //errorIfNull_("app.domain", domain);
            context = c.getProperty("app.context", "");
            //errorIfNull_("app.context", context);
            homePath = Play.configuration.getProperty("app.homepath",
                    String.format("%s%s", domain, context));
            //errorIfNull_("app.homepath", homePath);
            if (homePath.endsWith("/"))
                homePath = homePath.substring(0, homePath.length() - 1);
            // strip duplicated / at end of URL
            if (homePath.endsWith("/"))
                homePath = homePath.substring(0, homePath.length() - 1);
            homeUrl = Play.configuration.getProperty("app.homeurl",
                    String.format("%s://%s", scheme, homePath));
            //errorIfNull_("app.homeurl", homeUrl);
            debugOn = Boolean.parseBoolean(Play.configuration.getProperty(
                    "app.debug", "false"));
            if (debugOn) {
                developers = Play.configuration.getProperty("app.developers",
                        "");
            }
        }
    }

    @Before(priority = FPB_CONFIG_10)
    public static void addBindings() {
        RenderArgs r = renderArgs;
        r.put("context", context);
        r.put("domain", domain);
        r.put("homePath", homePath);
        r.put("homeUrl", homeUrl);
        r.put("scheme", scheme);
        r.put("postScheme", postScheme);
        r.put("string", utils.S.instance);
    }
    
    @Before(priority = FPB_CONFIG_100)
    public static void setDebug() {
        if (!debugOn) return;
        IUser me = app.currentUser();
        if (null == me) return;
        renderArgs.put("debug", developers.indexOf(me.getId().toString()) != -1);
    }

}
