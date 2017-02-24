/**
 * Zentech-Inc.com
 * Copyright (C) 2016 All Rights Reserved.
 */
package com.mjj.util;


import com.alibaba.fastjson.JSON;
import com.mjj.util.redis.RedisClient;
import com.mjj.util.redis.RedisLock;
import com.mjj.util.serialization.JsonSerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Pipeline;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wujn
 * @version $Id RemoteCache.java, v 0.1 2016-08-01 14:34 wujn Exp $$
 */
@Component
public class RemoteCache implements Cache {

    /**
     * 默认缓存时间30分钟，单位是秒
     */
    private static final int DEFAULT_CACHE_SECONDS = 5 * 60;

    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteCache.class);

    /**
     * 连接池
     **/
    @Autowired
    private JedisSentinelPool jedisSentinelPool;


    @Override
    public void put(String key, Object value) {
        put(key, value, DEFAULT_CACHE_SECONDS);
    }

    @Override
    public void put(String key, Object value, int seconds) {
        validateParam(key, value);
        RedisClient.invoke(jedisSentinelPool, (jedis) -> {
                    jedis.set(key, JsonSerializeUtil.toJSONString(value));
                    if (seconds <= 0) {
                        jedis.expire(key, DEFAULT_CACHE_SECONDS);
                    } else {
                        jedis.expire(key, seconds);
                    }
                    return null;
                }
        );
    }

    @Override
    public <T extends Serializable> void putList(String key, List<T> list) {
        putList(key, list, DEFAULT_CACHE_SECONDS);
    }

    @Override
    public <T extends Serializable> void putList(String key, List<T> list, int seconds) {
        RedisClient.invoke(jedisSentinelPool, jedis -> {
                    try (Pipeline pipeline = jedis.pipelined()) {
                        for (T aList : list) {
                            pipeline.lpush(key, JsonSerializeUtil.toJSONString(aList));
                        }
                        if (seconds <= 0) {
                            pipeline.expire(key, DEFAULT_CACHE_SECONDS);
                        } else {
                            pipeline.expire(key, seconds);
                        }
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                    return null;
                }

        );
    }

    @Override
    public <T extends Serializable> List<T> getList(String key, Class<T> clazz) {
        return RedisClient.invoke(jedisSentinelPool, (jedis) -> {
                    List<String> list = jedis.lrange(key, 0, -1);
                    return list.stream().map(b -> JsonSerializeUtil.toBean(b, clazz)).collect(Collectors.toList());
                }
        );
    }

    @Override
    public void removeList(String key) {
        remove(key);
    }


    @Override
    public <T> T get(String key, Class<T> tClass) {
        validateKeyParam(key);
        String result = RedisClient.invoke(jedisSentinelPool, (jedis) -> jedis.get(key));
        if (result == null) {
            return null;
        }
        return JsonSerializeUtil.toBean(result, tClass);
    }

    @Override
    public boolean exists(String key) {
        validateKeyParam(key);
        return RedisClient.invoke(jedisSentinelPool, (jedis) -> jedis.exists(key));
    }

    @Override
    public void remove(String key) {
        validateKeyParam(key);
        if (exists(key)) {
            RedisClient.invoke(jedisSentinelPool, (jedis) -> jedis.del(key));
        }
    }

    @Override
    public void removeList(String... keys) {
        Assert.notNull(keys, "移除的keys不能为空");
        String[] innerKeys = new String[keys.length];
        System.arraycopy(keys, 0, innerKeys, 0, keys.length);
        RedisClient.invoke(jedisSentinelPool, (jedis) -> jedis.del(innerKeys));
    }

    @Override
    public void clear() {
        LOGGER.warn("缓存的clear方法被调用，所有缓存数据都被清除！");
        RedisClient.invoke(jedisSentinelPool, BinaryJedis::flushAll);
    }

    @Override
    public List<String> findKeys(String pattern) {
        Assert.hasText(pattern, "查找规则不能为空");
        Charset charset = Charset.forName("UTF-8");
        return RedisClient.invoke(jedisSentinelPool, jedis -> {
            Set<String> sets = jedis.keys(("*" + pattern + "*"));
            if (sets != null) {
                List<String> list = new ArrayList<>(sets.size());
                list.addAll(sets);
                return list;
            }
            return null;
        });
    }

    /**
     * Setter method for property <tt>jedisSentinelPool</tt>.
     *
     * @param jedisSentinelPool value to be assigned to property jedisSentinelPool
     */
    public void setJedisSentinelPool(JedisSentinelPool jedisSentinelPool) {
        this.jedisSentinelPool = jedisSentinelPool;
    }

    private void validateParam(String key, Object value) {
        Assert.hasText(key, "key不能为空");
        Assert.notNull(value, "value不能为空");
        Assert.notNull(jedisSentinelPool, "jedis连接初始化失败");
        Assert.isInstanceOf(Serializable.class, value, "value没有实现Serializable接口，无法序列化");
    }

    private void validateKeyParam(String key) {
        Assert.hasText(key, "key不能为空");
        Assert.notNull(jedisSentinelPool, "jedis连接初始化失败");
    }

    @Override
    public long getExpires(String key) {
        validateKeyParam(key);
        return RedisClient.invoke(jedisSentinelPool, (jedis) -> jedis.ttl(key));
    }

    @Override
    public <T> T getHash(String key, String field, Class<T> tClass) {
        validateKeyParam(key);
        String jsonStr = RedisClient.invoke(jedisSentinelPool, (jedis) -> jedis.hget(key, field));
        if (jsonStr != null) {
            return JSON.parseObject(jsonStr, tClass);
        }
        return null;
    }

    @Override
    public void setHash(String key, String field, Serializable value) {
        validateKeyParam(key);
        RedisClient.invoke(jedisSentinelPool, (jedis) -> jedis.hset(key, field, JsonSerializeUtil.toJSONString(value)));
    }

    @Override
    public <T> Map<String, T> getAllHash(String key, Class<T> tClass) {
        validateKeyParam(key);
        return RedisClient.invoke(jedisSentinelPool, (jedis) -> {
            Map<String, String> map = jedis.hgetAll(key);
            Map<String, T> resultMap = new HashMap<>();
            if (map != null) {
                for (Map.Entry<String, String> item : map.entrySet()) {
                    String newKey = item.getKey();
                    T newValue = JsonSerializeUtil.toBean(item.getValue(), tClass);
                    resultMap.put(newKey, newValue);
                }
                return resultMap;
            }
            return null;
        });
    }

    @Override
    public Boolean hashKeyExists(String key, String field) {
        validateParam(key, field);
        return RedisClient.invoke(jedisSentinelPool, (jedis) -> jedis.hexists(key, field));
    }

    @Override
    public void putNeverExpires(String key, Object value) {
        validateParam(key, value);
        RedisClient.invoke(jedisSentinelPool, (jedis) -> jedis.set(key, JsonSerializeUtil.toJSONString(value)));
    }

    @Override
    public RedisLock getRedisLock(String key) {
        validateKeyParam(key);
        return new RedisLock(key, jedisSentinelPool.getResource());
    }

    @Override
    public <T extends Serializable> List<T> getList(String key, long start, long end, Class<T> clazz) {
        validateKeyParam(key);
        return RedisClient.invoke(jedisSentinelPool, (jedis) -> {
                    List<String> list = jedis.lrange(key, start, end);
                    return list.stream().map(b -> JsonSerializeUtil.toBean(b, clazz)).collect(Collectors.toList());
                }
        );
    }

    @Override
    public void trimList(String key, long start, long end) {
        validateKeyParam(key);
        RedisClient.invoke(jedisSentinelPool, (jedis) -> jedis.ltrim(key, start, end));
    }



    /**
     * redis队列操作，键值不过期
     *
     * @param key
     * @param value
     */
    @Override
    public void push(String key, Object value) {
        validateParam(key, value);
        RedisClient.invoke(jedisSentinelPool, (jedis) -> {
                    jedis.lpush(key, JsonSerializeUtil.toJSONString(value));
                    return null;
                }
        );
    }

    /**
     * redis队列操作，键值不过期
     *
     * @param key
     * @param tClass
     * @param <T>
     * @return
     */
    @Override
    public <T> T pop(String key, Class<T> tClass) {
        validateKeyParam(key);
        String jsonStr = RedisClient.invoke(jedisSentinelPool, (jedis) -> jedis.rpop(key));
        if (jsonStr != null) {
            return JSON.parseObject(jsonStr, tClass);
        }
        return null;
    }
}
