package com.greenlaw110.play;

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
}
