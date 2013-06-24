package controllers.filters;


import org.osgl.web.UserAgent;
import play.Logger;
import play.classloading.enhancers.ControllersEnhancer.ByPass;
import play.modules.betterlogs.NoTrace;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Finally;
import play.mvc.Http.Header;
import play.mvc.Util;

public class UADetector extends Controller implements IFilter {

    public static final String KEY = "__ua__";

    @Before(priority = FPB_UA_DETECTOR)
    public static void detect() {
        get();
    }

    @Finally(priority = FPF_UA_DETECTOR)
    public static void cleanUp() {
        reset();
    }

    @ByPass
    @Util
    public static void reset() {
        request.args.remove(KEY);
    }

    static UserAgent probe() {
        if (null == request) return null; // not in http request handling thread
        Header h = request.headers.get("user-agent");
        String userAgent = (null == h) ? null : h.value();
        Logger.trace("user agent: %s", userAgent);
        return UserAgent.valueOf(userAgent);
    }

    @ByPass
    @NoTrace
    public static UserAgent get() {
        UserAgent ua = (UserAgent)request.args.get(KEY);
        if (null == ua) {
            ua = probe();
            request.args.put(KEY, ua);
            renderArgs.put(KEY, ua);
        }
        return ua;
    }

    @ByPass
    @NoTrace
    public static UserAgent current() {
        return get();
    }

    @ByPass
    @NoTrace
    public static boolean isMobile() {
        return get().isMobile();
    }

    @ByPass
    @NoTrace
    public static void mobileOnly() {
        if (!isMobile()) notFound();
    }

// REMOVE DEPENDENCY ON GREEN"S PLAY FORK
//    // TODO add template path for more devices (BB, Opera mini, Symbian ...)
//    @OnApplicationStart
//    public static class BootLoader extends Job<Object> {
//        @Override
//        public void doJob() {
//            Controller.registerTemplateNameResolver(new ITemplateNameResolver(){
//                @Override
//                public String resolveTemplateName(String templateName) {
//                    if (Boolean.parseBoolean(Play.configuration.getProperty("app.mobileSupport", "false"))) {
//                        return isMobile() ? "mobile/" + templateName : templateName;
//                    } else {
//                        return templateName;
//                    }
//                }
//            });
//        }
//    }

}
