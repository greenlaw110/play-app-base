package org.osgl.play;

import play.jobs.Job;

/**
 * A contextual job inherits the parent thread's context
 */
public class ContextualJob<V> extends Job<V> {

    /*
     * This keeps a copy of JobContext of current thread (which is the parent thread)
     */
    private JobContext origin_ = JobContext.copy();

    @Override
    public boolean init() {
        // copy the JobContext of parent thread into the current thread
        JobContext.init(origin_);
        return super.init();
    }

    @Override
    public void _finally() {
        super._finally();
        JobContext.clear();
    }

    protected static final void setContextProperty(String key, Object val) {
        JobContext.put(key, val);
    }

    protected static final Object getContextProperty(String key) {
        return JobContext.get(key);
    }

    protected static final <T> T getContextProperty(String key, Class<T> cls) {
        return JobContext.get(key, cls);
    }
}
