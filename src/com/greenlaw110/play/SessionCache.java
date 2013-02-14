package com.greenlaw110.play;

import play.cache.Cache;
import play.mvc.Scope;

/**
 * Provide API to access Cache based on session id
 */
public class SessionCache {

    private static String k(String key) {
        return key + "-" + Scope.Session.current().getId();
    }

    /**
     * Put a value into cache and associate it with key + session id, store the value indefinitely
     * <p>Existing value associated with the same key will get replaced</p>
     * @param key
     * @param val
     */
    public static void put(String key, Object val) {
        key = k(key);
        Cache.set(key, val);
    }

    /**
     * Put a value into cache and associate it with key + session id, store the value until expired
     * <p>Existing value associated with the same key will get replaced</p>
     * @param key
     * @param val
     * @param expiration Ex: 10s, 3mn, 8h
     */
    public static void put(String key, Object val, String expiration) {
        key = k(key);
        Cache.set(key, val, expiration);
    }

    public static boolean safePut(String key, Object val, String expiration) {
        key = k(key);
        return Cache.safeSet(key, val, expiration);
    }

    public static void putIfNotExists(String key, Object val) {
        key = k(key);
        Cache.add(key, val);
    }

    public static void putIfNotExists(String key, Object val, String expiration) {
        key = k(key);
        Cache.add(key, val, expiration);
    }

    public static boolean safePutIfNotExists(String key, Object val, String expiration) {
        key = k(key);
        return Cache.safeAdd(key, val, expiration);
    }

    public static void putIfExists(String key, Object val) {
        key = k(key);
        Cache.replace(key, val);
    }

    public static void putIfExists(String key, Object val, String expiration) {
        key = k(key);
        Cache.replace(key, val, expiration);
    }

    public static boolean safePutIfExists(String key, Object val, String expiration) {
        key = k(key);
        return Cache.safeReplace(key, val, expiration);
    }
    
    /**
     * Get value from the cache with the key + session id
     *
     * @param key
     * @return
     */
    public static <T> T get(String key) {
        key = k(key);
        return (T)Cache.get(key);
    }

    /**
     * Get value from the cache with the key + session id
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T get(String key, Class<T> clazz) {
        key = k(key);
        return (T)Cache.get(key);
    }

    /**
     * Delete value from cache with key + session id
     * @param key
     */
    public static void delete(String key) {
        key = k(key);
        Cache.delete(key);
    }

    public static void safeDelete(String key) {
        key = k(key);
        Cache.safeDelete(key);
    }

}
