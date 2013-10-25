package org.osgl.play;

import play.PlayPlugin;
import play.mvc.Http;

import java.lang.reflect.Method;

/**
 * Some common utils
 */
public class Utils extends PlayPlugin {
    @Override
    public void beforeActionInvocation(Method actionMethod) {
        Http.Request request = Http.Request.current();
        // populate ip address with real ip address when deploy with nginx
        if (request.headers.containsKey("x-real-ip")) {
            request.remoteAddress = request.headers.get("x-real-ip").value();
        }
    }

    @Override
    public void beforeInvocation() {
        if (!JobContext.initialized()) JobContext.init();
        // else: JobContext initialized because I am in a ContextualJob
    }

    @Override
    public void invocationFinally() {
        JobContext.clear();
    }
}
