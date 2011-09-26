package controllers.filters;

import play.mvc.Before;
import play.mvc.Controller;

public class AuthentityChecker extends Controller implements IFilter {
    @Before(priority=FPB_AUTHENTITY)
    public static void check() {
        String m = request.method;
        if ("POST".equalsIgnoreCase(m) || "PUT".equalsIgnoreCase(m) || "DELETE".equalsIgnoreCase(m)) {
            checkAuthenticity();
        }
    }
}
