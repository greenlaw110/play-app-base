package com.greenlaw110.play;

import play.jobs.Job;

/**
 * A contextual job inherits the parent thread's context
 */
public class ContextualJob<V> extends Job<V> {

    private JobContext origin_ = JobContext.fork();

    @Override
    public boolean init() {
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
