package org.osgl.web;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class UserAgent {

    public static enum OS {
        MAC_OS, IOS, WIN32, WIN64, LINUX, DROID, SYMBIAN, BLACKBERRY, J2ME, SUN_OS, BOT, UNKNOWN
    }
    private OS os_ = null;
    public OS getOS() {
        return os_;
    }

    public static enum Device {
        IPHONE,
        IPAD,
        IPOD,
        DROID,
        BLACKBERRY,
        SONYERICSSON,
        NOKIA,
        PC,
        MOBILE,
        BOT,
        UNKNOWN
    }
    private Device device_ = null;
    public Device getDevice() {
        return device_;
    }
    public final boolean is(Device device) {
        return (device_ == device);
    }

    public final boolean isMobile() {
        final Device[] da = {
            Device.IPHONE,
            Device.IPOD,
            Device.DROID,
            Device.BLACKBERRY,
            Device.SONYERICSSON,
            Device.NOKIA
        };
        for (Device d: da) {
            if (device_ == d) return true;
        }
        return false;
    }

    public static enum Browser {
        IE_6, IE_7, IE_8, IE_9, IE_10,
        CHROME, SAFARI, FIREFOX_3, FIREFOX, OPERA, UCWEB, BOT, UNKNOWN
    }

    private Browser browser_ = Browser.UNKNOWN;
    public final Browser getBrowser() {
        return browser_;
    }

    public final boolean isIE678() {
        Browser b = browser_;
        return Browser.IE_6 == b || Browser.IE_7 == b || Browser.IE_8 == b;
    }

    public final boolean isIE9Up() {
        Browser b = browser_;
        return Browser.IE_9 == b || Browser.IE_10 == b;
    }

    public final boolean isIE() {
        return browser_.name().contains("IE");
    }

    public final boolean isFirefox3() {
        return browser_ == Browser.FIREFOX_3;
    }

    public final boolean isFirefox4Up() {
        return browser_ == Browser.FIREFOX && browser_ != Browser.FIREFOX_3;
    }

    public final boolean isFirefox() {
        return browser_.name().contains("FIREFOX");
    }

    public final boolean isOpera() {
        return browser_ == Browser.OPERA;
    }
    
    public final boolean isWebKit() {
        return str_.contains("WebKit");
    }
    
    public final boolean isSafari() {
        return browser_ == Browser.SAFARI;
    }

    public final boolean isChrome() {
        return browser_ == Browser.CHROME;
    }

    public final boolean  isUCWeb() {
        return browser_ == Browser.UCWEB;
    }

    private String str_;

    @Override
    public final String toString() {
        return str_;
    }

    private static Map<String, UserAgent> cache_ = new HashMap<String, UserAgent>();
    public static UserAgent parse(String userAgent) {
        UserAgent ua = cache_.get(userAgent);
        if (null != ua) return ua;
        ua = new UserAgent(userAgent);
        cache_.put(userAgent, ua);
        return ua;
    }

    /**
     * Construct the instance from http header: user-agent
     * @param userAgent
     */
    private UserAgent(String userAgent) {
        parse_(userAgent);
        str_ = userAgent;
    }

    private static enum P {
        /*
         * Note the sequence of the enum DOSE matter!
         */
        J2ME(Pattern.compile(".*(MIDP|J2ME|CLDC).*"), Device.MOBILE, null, OS.J2ME),
        UCWEB(Pattern.compile(".*UCWEB.*"), Device.MOBILE, Browser.UCWEB, null),
        WIN32(Pattern.compile(".*(Windows|W32).*"), Device.PC, null, OS.WIN32),
        WIN64(Pattern.compile(".*(WOW64|Win64).*"), Device.PC, null, OS.WIN64),
        LINUX(Pattern.compile(".*Linux.*"), null, null, OS.LINUX),
        MAC(Pattern.compile(".*Mac OS.*"), Device.PC, null, OS.MAC_OS),
        SOS(Pattern.compile(".*SunOS.*"), Device.PC, null, OS.SUN_OS),
        IPHONE(Pattern.compile(".*iPhone.*"), Device.IPHONE, Browser.SAFARI, OS.IOS),
        IPAD(Pattern.compile(".*iPad.*"), Device.IPAD, Browser.SAFARI, OS.IOS),
        IPOD(Pattern.compile(".*iPod.*"), Device.IPOD, Browser.SAFARI, OS.IOS),
        ANDROID(Pattern.compile(".*Android.*"), Device.DROID, null, OS.DROID),
        BLACKBERRY(Pattern.compile(".*BlackBerry.*"), Device.BLACKBERRY, null, OS.BLACKBERRY),
        SYMBIAN(Pattern.compile(".*Symbian.*", Pattern.CASE_INSENSITIVE), null, null, OS.SYMBIAN),
        SONYERICSSON(Pattern.compile(".*SonyEricsson.*"), Device.SONYERICSSON, null, null),
        NOKIA(Pattern.compile(".*Nokia.*", Pattern.CASE_INSENSITIVE), Device.NOKIA, null, null),
        IE6(Pattern.compile(".*MSIE\\s+[6]\\.0.*"), Device.PC, Browser.IE_6, null),
        IE7(Pattern.compile(".*MSIE\\s+[7]\\.0.*"), Device.PC, Browser.IE_7, null),
        IE8(Pattern.compile(".*MSIE\\s+[8]\\.0.*"), Device.PC, Browser.IE_8, null),
        IE9(Pattern.compile(".*MSIE\\s+(9)\\.0.*"), Device.PC, Browser.IE_9, null),
        IE10(Pattern.compile(".*MSIE\\s+(10|11|12)\\.0.*"), null, Browser.IE_10, null),
        FIREFOX(Pattern.compile(".*Firefox.*"), null, Browser.FIREFOX, null),
        FIREFOX3(Pattern.compile(".*Firefox/3.*"), null, Browser.FIREFOX_3, null),
        SAFARI(Pattern.compile(".*Safari.*"), null, Browser.SAFARI, null),
        CHROME(Pattern.compile(".*Chrome.*"), null, Browser.CHROME, null),
        OPERA(Pattern.compile(".*Opera.*"), null, Browser.OPERA, null),
        BOT(Pattern.compile(".*(Googlebot|msn-bot|msnbot|Bot|bot|Baiduspider|SeznamBot|facebookexternalhit).*", Pattern.CASE_INSENSITIVE), Device.BOT, Browser.BOT, OS.BOT);

        private final Pattern p_;
        private Device d_ = Device.UNKNOWN;
        private Browser b_;
        private OS o_ = OS.UNKNOWN;
        P(Pattern pattern, Device device, Browser browser, OS os) {
            p_ = pattern;
            d_ = device;
            b_ = browser;
            o_ = os;
        }
        boolean matches(String ua) {
            return p_.matcher(ua).matches();
        }
        void test(String str, UserAgent ua) {
            if (matches(str)) {
                if (null != d_) {
                    ua.device_ = d_;
                }

                if (null != b_) {
                    ua.browser_ = b_;
                }

                if (null != o_) {
                    ua.os_ = o_;
                }
            }
        }
    }

    private void parse_(String userAgent) {
        for (P p: P.values()) {
            p.test(userAgent, this);
        }
    }

    public static final String KEY = "__ua__";
    /**
     * Use valueOf instead
     * @param userAgent
     * @return
     */
    @Deprecated
    public static final UserAgent set(String userAgent) {
        return valueOf(userAgent);
    }
    public static final UserAgent valueOf(String userAgent) {
        return UserAgent.parse(userAgent);
    }

    public static void main(String[] args) {
        String s = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.20 (KHTML, like Gecko) Chrome/11.0.669.0 Safari/534.20";
        UserAgent ua = set(s);
        assert_(ua.getBrowser() == Browser.CHROME, "1");

        s = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; Zune 4.0)";
        ua = set(s);
        assert_(!ua.is(Device.IPHONE), "4");
        assert_(ua.getBrowser() == Browser.IE_8, "4");

        s = "Mozilla/5.0 (Linux; U; Android 3.0; en-us; Xoom Build/HRI39) AppleWebKit/534.13 (KHTML, like Gecko) Version/4.0 Safari/534.13";
        ua = set(s);
        assert_(ua.is(Device.DROID), "2");
        assert_(ua.getBrowser() == Browser.SAFARI, "3");

        s = "Mozilla/5.0 (X11; U; Linux x86_64; fr; rv:1.9.2.3) Gecko/20100403 Fedora/3.6.3-4.fc13 Firefox/3.6.3";
        ua = set(s);
        assert_(ua.isFirefox3(), "firefox 3");

        System.out.println("success!");
    }

    private static void assert_(boolean b, String reason) {
        if (!b) throw new RuntimeException("assert failed: " + reason);
    }
}
