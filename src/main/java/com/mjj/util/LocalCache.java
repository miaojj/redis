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
 * @version $Id LocalCache.java, v 0.1 2016-08-01 14:34 wujn Exp $$
 */
public class LocalCache implements Cache {


    @Override
    public void put(String key, Object value) {

    }

    @Override
    public void put(String key, Object value, int seconds) {

    }

    @Override
    public <T extends Serializable> void putList(String key, List<T> list) {

    }

    @Override
    public <T extends Serializable> void putList(String key, List<T> list, int seconds) {

    }

    @Override
    public <T extends Serializable> List<T> getList(String key, Class<T> clazz) {
        return null;
    }

    @Override
    public <T extends Serializable> void removeList(String key) {

    }

    @Override
    public <T> T get(String key, Class<T> tClass) {
        return null;
    }

    @Override
    public boolean exists(String key) {
        return false;
    }

    @Override
    public void remove(String key) {

    }

    @Override
    public void removeList(String... keys) {

    }

    @Override
    public void clear() {

    }

    @Override
    public List<String> findKeys(String pattern) {
        return null;
    }

    @Override
    public long getExpires(String key) {
        return 0;
    }

    @Override
    public <T> T getHash(String key, String field, Class<T> tClass) {
        return null;
    }


    @Override
    public void setHash(String key, String field, Serializable value) {

    }

    @Override
    public <T> Map<String, T> getAllHash(String key, Class<T> tClass) {
        return null;
    }

    @Override
    public Boolean hashKeyExists(String key, String field) {
        return null;
    }

    @Override
    public void putNeverExpires(String key,Object value) {

    }

    @Override
    public RedisLock getRedisLock(String key) {
        return null;
    }

    @Override
    public void trimList(String key, long start, long end) {

    }

    @Override
    public <T extends Serializable> List<T> getList(String key, long start, long end, Class<T> clazz) {
        return null;
    }

    @Override
    public void push(String key, Object value) {

    }

    @Override
    public <T> T pop(String key, Class<T> tClass) {
        return null;
    }
}
