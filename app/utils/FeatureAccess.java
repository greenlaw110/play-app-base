package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import api.IApplication;
import api.IUser;

@OnApplicationStart
public class FeatureAccess extends Job<Object> {
    public static final String CONF_DEVELOPERS = "app.fa.developers";
    public static final String CONF_TESTERS = "app.fa.testers";
    public static enum Role {
        DEV, TESTER
    }
    public static final Map<Role, List<String>> map_ = new HashMap<Role, List<String>>();
    @Override
    public void doJob() {
        // developers
        String s = Play.configuration.getProperty(CONF_DEVELOPERS, "");
        String[] sa = s.split("[,:\\s]");
        List<String> l = new ArrayList(Arrays.asList(sa));
        map_.put(Role.DEV, Arrays.asList(sa));
        
        // testers
        s = Play.configuration.getProperty(CONF_TESTERS, "");
        sa = s.split("[,;\\s]");
        l = new ArrayList(Arrays.asList(sa));
        l.addAll(map_.get(Role.DEV)); // developer has all tester privilige
        map_.put(Role.TESTER, l);
    }
    
    @Inject
    static IApplication app = null;
    public static boolean hasAccess(Role role) {
        List<String> l = map_.get(role);
        if (null == l) return false;
        IUser me = app.currentUser();
        if (null == me) return false;
        return l.contains(me.getId().toString());
    }
}
