package controllers.filters;


import play.Logger;
import play.Play;
import play.classloading.enhancers.ControllersEnhancer.ByPass;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.modules.betterlogs.NoTrace;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Finally;
import play.mvc.Http.Header;
import utils.UserAgent;

public class UADetector extends Controller implements IFilter {

    public static final String KEY = "__ua__";
    
    @Before(priority = FPB_UA_DETECTOR)
    public static void detect() {
        UserAgent ua = probe();
        current(ua);
    }
    
    @Finally(priority = FPF_UA_DETECTOR)
    public static void cleanUp() {
        UserAgent.reset();
    }
    
    static UserAgent probe() {
        if (null == request) return null; // not in http request handling thread
        Header h = request.headers.get("user-agent");
        String userAgent = (null == h) ? null : h.value();
        Logger.trace("user agent: %s", userAgent);
        return UserAgent.set(userAgent);
    }
    
    @ByPass
    @NoTrace
    public static UserAgent get() {
        UserAgent ua = current();
        if (null == ua) ua = probe();
        return ua;
    }
    
    @ByPass
    @NoTrace
    public static UserAgent current() {
        return (UserAgent)request.args.get(KEY);
    }
    
    @ByPass
    public static void current(UserAgent ua) {
        request.args.put(KEY, ua);
        renderArgs.put(KEY, ua);
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
    
    // TODO add template path for more devices (BB, Opera mini, Symbian ...)
    @OnApplicationStart
    public static class BootLoader extends Job<Object> {
        @Override
        public void doJob() {
            Controller.registerTemplateNameResolver(new ITemplateNameResolver(){
                @Override
                public String resolveTemplateName(String templateName) {
                    if (Boolean.parseBoolean(Play.configuration.getProperty("app.mobileSupport", "false"))) {
                        return isMobile() ? "mobile/" + templateName : templateName;
                    } else {
                        return templateName;
                    }
                }
            });
        }
    }

}
