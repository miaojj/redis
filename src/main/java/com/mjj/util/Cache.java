/**
 * Zentech-Inc.com
 * Copyright (C) 2016 All Rights Reserved.
 */
package com.mjj.util;


import com.mjj.util.redis.RedisLock;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author wujn
 * @version $Id Cache.java, v 0.1 2016-08-01 14:34 wujn Exp $$
 */
public interface Cache {

    void put(String key, Object value);

    void put(String key, Object value, int seconds);

    <T extends Serializable> void putList(String key, List<T> list);

    <T extends Serializable> void putList(String key, List<T> list, int seconds);

    <T extends Serializable> List<T> getList(String key, Class<T> clazz);

    <T extends Serializable> void removeList(String key);

    <T> T get(String key, Class<T> tClass);

    boolean exists(String key);

    void remove(String key);

    void removeList(String... keys);

    void clear();

    List<String> findKeys(String pattern);

    long getExpires(String key);

    <T> T getHash(String key, String field, Class<T> tClass);

    void setHash(String key, String field, Serializable value);

    <T> Map<String, T> getAllHash(String key, Class<T> tClass);

    Boolean hashKeyExists(String key, String field);

    void putNeverExpires(String key, Object value);

    RedisLock getRedisLock(String key);

    void trimList(String key, long start, long end);

    <T extends Serializable> List<T> getList(String key, long start, long end, Class<T> clazz);

    void push(String key, Object value);

    <T> T pop(String key, Class<T> tClass);
}
