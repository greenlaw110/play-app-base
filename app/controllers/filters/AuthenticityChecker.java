package controllers.filters;

import play.mvc.Before;
import play.mvc.Controller;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 13/02/13
 * Time: 12:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthenticityChecker extends Controller implements IFilter {
    @Before(priority=FPB_AUTHENTITY)
    public static void check() {
        String m = request.method;
        if ("POST".equalsIgnoreCase(m) || "PUT".equalsIgnoreCase(m) || "DELETE".equalsIgnoreCase(m)) {
            checkAuthenticity();
        }
    }
}
