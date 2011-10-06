package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import play.modules.betterlogs.NoTrace;
import controllers.filters.UADetector;

public class UserAgent {
    
    public static enum Device {
        IPHONE (0x01),
        IPAD (0x02),
        IPOD (0x04),
        DROID (0x08);
        
        private int id_ = 0;
        private Device (int id) {
            id_ = id;
        }
        
        @NoTrace
        public int id() {
            return id_;
        }
    }
    private int device_ = 0;
    @NoTrace
    public final boolean is(Device device) {
        return (device_ & device.id()) != 0;
    }
    
    @NoTrace
    public final boolean isMobile() {
        final Device[] da = {
            Device.IPHONE,
            Device.IPOD,
            Device.DROID
        };
        for (Device d: da) {
            if ((device_ & d.id()) != 0) return true;
        }
        return false;
    }
    
    public static enum Browser {
        IE_6, IE_7, IE_8, IE_9, IE_10,
        CHROME, SAFARI, FIREFOX_3, FIREFOX, OPERA, UNKNOWN
    }
    
    private Browser browser_ = Browser.UNKNOWN;
    @NoTrace
    public final Browser getBrowser() {
        return browser_;
    }
    
    @NoTrace
    public final boolean isIE678() {
        Browser b = browser_;
        return Browser.IE_6 == b || Browser.IE_7 == b || Browser.IE_8 == b;
    }
    
    @NoTrace
    public final boolean isIE9Up() {
        Browser b = browser_;
        return Browser.IE_9 == b || Browser.IE_10 == b;
    }
    
    @NoTrace
    public final boolean isIE() {
        return browser_.name().contains("IE");
    }
    
    @NoTrace
    public final boolean isFirefox3() {
        return browser_ == Browser.FIREFOX_3;
    }
    
    @NoTrace
    public final boolean isFirefox4Up() {
        return browser_ == Browser.FIREFOX && browser_ != Browser.FIREFOX_3;
    }
    
    @NoTrace
    public final boolean isFirefox() {
        return browser_.name().contains("FIREFOX");
    }
    
    private String str_;
    
    @Override
    @NoTrace
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
        IPHONE(Pattern.compile(".*iPhone.*"), Device.IPHONE, null),
        ANDROID(Pattern.compile(".*Android.*"), Device.DROID, null),
        IE6(Pattern.compile(".*MSIE\\s+[6]\\.0.*"), null, Browser.IE_6),
        IE7(Pattern.compile(".*MSIE\\s+[7]\\.0.*"), null, Browser.IE_7),
        IE8(Pattern.compile(".*MSIE\\s+[8]\\.0.*"), null, Browser.IE_8),
        IE9(Pattern.compile(".*MSIE\\s+(9)\\.0.*"), null, Browser.IE_9),
        IE10(Pattern.compile(".*MSIE\\s+(10|11|12)\\.0.*"), null, Browser.IE_10),
        FIREFOX(Pattern.compile(".*Firefox.*"), null, Browser.FIREFOX),
        FIREFOX3(Pattern.compile(".*Firefox/3.*"), null, Browser.FIREFOX_3),
        SAFARI(Pattern.compile(".*Safari.*"), null, Browser.SAFARI),
        CHROME(Pattern.compile(".*Chrome.*"), null, Browser.CHROME),
        OPERA(Pattern.compile(".*Opera.*"), null, Browser.OPERA);
        
        private final Pattern p_;
        private Device d_;
        private Browser b_;
        P(Pattern pattern, Device device, Browser browser) {
            p_ = pattern;
            d_ = device;
            b_ = browser;
        }
        @NoTrace
        boolean matches(String ua) {
            return p_.matcher(ua).matches();
        }
        @NoTrace
        void test(String str, UserAgent ua) {
            if (matches(str)) {
                if (null != d_) {
                    ua.device_ |= d_.id();
                }
                
                if (null != b_) {
                    ua.browser_ = b_;
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
    @NoTrace
    public static final UserAgent current() {
        return get();
    }
    
    @NoTrace
    public static final UserAgent get() {
        return UADetector.current();
    }
    public static final UserAgent set(String userAgent) {
        UserAgent ua = UserAgent.parse(userAgent);
        //UADetector.current(ua);
        return ua;
    }
    public static final void reset() {
        UADetector.current(null);
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
