package org.osgl.play;

import org.osgl.IContextInitializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A thread local job context to allow developer passing context
 * from a parent thread to a forked job environment
 */
public class JobContext {

    private static final List<IContextInitializer> initializers = new ArrayList();

    public static void registerInitializer(IContextInitializer initializer) {
        if (initialized()) throw new IllegalStateException();
        initializers.add(initializer);
    }

    private static final ThreadLocal<JobContext> current_ = new ThreadLocal<JobContext>();

    private static final Map<String, Object> m() {
        return current_.get().bag_;
    }

    /**
     * Whether JobContext of current thread initialized
     * @return
     */
    public static final boolean initialized() {
        return null != current_.get();
    }

    /**
     * Init JobContext of current thread
     */
    static final void init() {
        clear();
        current_.set(new JobContext());
        for (IContextInitializer i: initializers) {
            i.initContext();
        }
    }

    /**
     * Clear JobContext of current thread
     */
    static final void clear() {
        JobContext ctxt = current_.get();
        if (null != ctxt) {
            ctxt.bag_.clear();
            current_.remove();
        }
    }

    /**
     * Get value by key from the JobContext of current thread
     * @param key
     * @return
     */
    public static final Object get(String key) {
        return m().get(key);
    }

    /**
     * Generic version of getting value by key from the JobContext of current thread
     * @param key
     * @return
     */
    public static final <T> T get(String key, Class<T> clz) {
        return (T)m().get(key);
    }

    /**
     * Set value by key to the JobContext of current thread
     * @param key
     * @return
     */
    public static final void put(String key, Object val) {
        m().put(key, val);
    }

    /**
     * Remove value by key from the JobContext of current thread
     * @param key
     * @return
     */
    public static final void remove(String key) {
        m().remove(key);
    }

    /**
     * Make a copy of JobContext of current thread
     * @return
     */
    static final JobContext copy() {
        JobContext ctxt = new JobContext();
        ctxt.bag_.putAll(current_.get().bag_);
        return ctxt;
    }

    /**
     * Initialize current thread's JobContext using specified copy
     * @param origin
     */
    static final void init(JobContext origin) {
        current_.set(origin);
    }

    private Map<String, Object> bag_ = new HashMap<String, Object>();

}
