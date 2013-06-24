/**
 * Config.java
 *
 * Bindings controller provide filters to add miscellaneous variables to RenderArgs
 *
 * @version 1.0 greenlaw110@gmail.com - intial version
 */

package controllers.filters;

import org.osgl.play.api.IApplication;
import org.osgl.play.api.IUser;
import org.osgl.util.S;
import play.Play;
import play.exceptions.ConfigurationException;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.modules.betterlogs.NoTrace;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope.RenderArgs;

import javax.inject.Inject;
import java.util.Properties;
import java.util.regex.Pattern;

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
    public static String scheme = null;
    public static boolean postSsl = true;
    public static String postScheme = null;
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
            Play.configuration.put("application.baseUrl", homeUrl);
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
        r.put("_S", S.instance);
        r.put("_urlResolver", UrlResolver.instance);
    }

    @Before(priority = FPB_CONFIG_100)
    public static void setDebug() {
        if (!debugOn) return;
        IUser me = app.currentUser();
        if (null == me) return;
        renderArgs.put("debug", developers.indexOf(me.getId().toString()) != -1);
    }

    public static class UrlResolver {

        public static UrlResolver instance = new UrlResolver();

        private static Pattern p1 = Pattern.compile("(https?:)?//.*");
        private static Pattern p2 = null;
        private static Pattern p2() {
            if (null == p2) p2 = Pattern.compile("(http?:)?//" + Config.domain + ".*");
            return p2;
        }
        private static String s = null;
        private static String s() {
            if (null == s) s = new StringBuilder("//").append(Config.domain).append("/").toString();
            return s;
        }

        public static String fullUrl(String url, boolean includeScheme) {
            String scheme = includeScheme ? Config.scheme + ":" : "";
            if (S.isEmpty(url)) return "//" + Config.domain + "/";
            if (p1.matcher(url).matches() && ! p2().matcher(url).matches()) {
                return scheme + url.replaceFirst("(https?:)?//.*?/", s());
            } else {
                return scheme + "//" + Config.domain + (url.startsWith("/") ? url : "/" + url);
            }
        }

        public static String fullUrl(String url) {
            return fullUrl(url, false);
        }

        public static void main(String[] sa) {
            Config.domain = "simspets.apps2.pixolut.com";
            String s1 = "https://127.0.0.1:20004/pet/000001";
            String s2 = "http://127.0.0.1:20004/pet/000001";
            String s3 = "//127.0.0.1:20004/pet/000001";
            String s4 = "/pet/000001";
            String s5 = "pet/000001";

            String url = "//simspets.apps2.pixolut.com/pet/000001";
            test(s1, url);
            test(s2, url);
            test(s3, url);
            test(s4, url);
            test(s5, url);
        }

        public static void test(String url, String result) {
            if (!result.equals(fullUrl(url))) {
                System.out.println("fail: " + url + ", " + fullUrl(url));
            } else {
                System.out.println("pass");
            }
        }
        
    }
    
}
