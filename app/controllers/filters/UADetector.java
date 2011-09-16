package controllers.filters;


import javax.inject.Inject;

import play.Logger;
import play.classloading.enhancers.ControllersEnhancer.ByPass;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Finally;
import play.mvc.Http.Header;
import utils.UserAgent;
import api.IApplication;

public class UADetector extends Controller implements IFilter {
    
    @Before(priority = FPB_UA_DETECTOR)
    public static void detect() {
        UserAgent ua = probe();
        renderArgs.put("ua", ua);
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
    
    public static UserAgent get() {
        UserAgent ua = UserAgent.get();
        if (null == ua) ua = probe();
        return ua;
    }
    
    public static boolean isMobile() {
        return get().isMobile();
    }

    @ByPass
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
                    return isMobile() ? "mobile/" + templateName : templateName;
                }
            });
        }
    }

}
