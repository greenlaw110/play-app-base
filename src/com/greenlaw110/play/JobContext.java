package com.greenlaw110.play;

import play.Invoker;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A thread local job context to allow developer passing context
 * from a parent thread to a forked job environment
 */
public class JobContext {

    private static final ThreadLocal<JobContext> current_ = new ThreadLocal<JobContext>();

    private static final Map<String, Object> m() {
        return current_.get().bag_;
    }

    public static final boolean initialized() {
        return null != current_.get();
    }

    public static final void init() {
        clear();
        current_.set(new JobContext());
    }

    public static final void clear() {
        JobContext ctxt = current_.get();
        if (null != ctxt) {
            ctxt.bag_.clear();
            current_.remove();
        }
    }

    public static final Object get(String key) {
        return m().get(key);
    }

    public static final <T> T get(String key, Class<T> clz) {
        return (T)m().get(key);
    }

    public static final void put(String key, Object val) {
        m().put(key, val);
    }

    public static final void remove(String key) {
        m().remove(key);
    }

    public static final JobContext fork() {
        JobContext ctxt = new JobContext();
        ctxt.bag_.putAll(current_.get().bag_);
        return ctxt;
    }

    public static final void init(JobContext origin) {
        current_.set(origin);
    }

    private Map<String, Object> bag_ = new HashMap<String, Object>();

}
