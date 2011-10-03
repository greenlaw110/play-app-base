/**
 * Config.java
 * 
 * Bindings controller provide filters to add miscellaneous variables to RenderArgs
 *  
 * @version 1.0 greenlaw110@gmail.com - intial version
 */

package controllers.filters;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

import javassist.Modifier;

import javax.inject.Inject;

import play.Logger;
import play.Play;
import play.exceptions.ConfigurationException;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.modules.betterlogs.NoTrace;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope.RenderArgs;
import api.IApplication;
import api.IUser;

public class Config extends Controller implements IFilter {
    
    @NoTrace
    public static int getIntConf(String key, int def) {
        String s = Play.configuration.getProperty(key);
        if (null == s) return def;
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            throw new ConfigurationException("error get integer config " + key + ": " + e.getMessage());
        }
    }
    
    @NoTrace
    public static long getLongConf(String key, long def) {
        String s = Play.configuration.getProperty(key);
        if (null == s) return def;
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            throw new ConfigurationException("error get long config " + key + ": " + e.getMessage());
        }
    }
    
    @NoTrace
    public static float getFloatConf(String key, float def) {
        String s = Play.configuration.getProperty(key);
        if (null == s) return def;
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            throw new ConfigurationException("error get float config " + key + ": " + e.getMessage());
        }
    }
    
    @NoTrace
    public static boolean getBoolConf(String key, boolean def) {
        String s = Play.configuration.getProperty(key);
        if (null == s) return def;
        try {
            return Boolean.parseBoolean(s);
        } catch (Exception e) {
            throw new ConfigurationException("error get boolean config " + key + ": " + e.getMessage());
        }
    }
    
    @NoTrace
    public static String getStringConf(String key, String def) {
        return Play.configuration.getProperty(key, def);
    }

    public static boolean ssl = false;
    public static String scheme = "http";
    public static boolean postSsl = true;
    public static String postScheme = "https";
    public static String domain;
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
            homePath = Play.configuration.getProperty("app.homepath",
                    String.format("%s%s", domain, Play.ctxPath));
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
        r.put("_ctxPath", Play.ctxPath);
        r.put("_domain", domain);
        r.put("_homePath", homePath);
        r.put("_homeUrl", homeUrl);
        r.put("_scheme", scheme);
        r.put("_postScheme", postScheme);
        r.put("_S", utils.S.instance);
    }
    
    @Before(priority = FPB_CONFIG_100)
    public static void setDebug() {
        if (!debugOn) return;
        IUser me = app.currentUser();
        if (null == me) return;
        renderArgs.put("debug", developers.indexOf(me.getId().toString()) != -1);
    }

}
