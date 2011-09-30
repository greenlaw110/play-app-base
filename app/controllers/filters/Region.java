package controllers.filters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import play.Play;
import play.classloading.enhancers.ControllersEnhancer.ByPass;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.modules.betterlogs.NoTrace;
import play.mvc.Before;
import play.mvc.Controller;

public class Region extends Controller implements IFilter{
    
    public static final String KEY = "__region__";
    
    @NoTrace
    public static String getDefaultRegion() {
        return Play.configuration.getProperty("app.region.default", "AU");
    }
    
    @NoTrace
    private static String getRegion_() {
        String region = params.get(Play.configuration.getProperty("app.region.param.name", "region"));
        if (null == region) region = session.get(KEY);
        if (null == region) {
            region = getDefaultRegion(); // default region
        }
        return region;
    }
    
    @NoTrace
    public static String current() {return get();}
    @NoTrace
    public static String get() {return (String)request.args.get(KEY);}

    @ByPass
    public static void current(String region){
        request.args.put(KEY, region);
        renderArgs.put(KEY, region);
        session.put(KEY, region);
    }
    
    @Before(priority = FPB_REGION)
    public static void resolveFromRequest() {
    	String region = getRegion_();
        region = region.toUpperCase();
    	current(region);
    }
    
    public static void clear() {
        request.args.remove(KEY);
        session.remove(KEY);
        renderArgs.put(KEY, null);
    }
    
    public static final String CONF_KEY = "app.regions";
    private static String[] regions_ = {};
    public static String[] names() {
        return Arrays.copyOf(regions_, regions_.length);
    }
    
    @OnApplicationStart
    public static class Bootstrap extends Job<Object> {
        @Override
        public void doJob() {
            String def = getDefaultRegion();
            String regions = Play.configuration.getProperty(CONF_KEY, def);
            Set<String> s = new HashSet<String>(); 
            s.addAll(Arrays.asList(regions.split("[,;\\s]")));
            s.add(def);
            regions_ = s.toArray(new String[]{});
        }
    }
    
}
