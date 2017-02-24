/**
 * Zentech-Inc.com
 * Copyright (C) 2016 All Rights Reserved.
 */
package com.mjj.util.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

/**
 * @author wujn
 * @version $Id RedisClient.java, v 0.1 2016-08-01 15:26 wujn Exp $$
 */
public class RedisClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClient.class);

    public static <T> T invoke(JedisSentinelPool pool, RedisClientInvoker<T> clients) {
        T obj = null;
        Jedis jedis = null;
        try {
            synchronized (RedisClient.class) {
                jedis = pool.getResource();
            }
            obj = clients.invoke(jedis);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            if (jedis != null) {
                synchronized (RedisClient.class) {
                    if (jedis != null) {
                        jedis.close();
                    }
                }
            }
        }
        return obj;
    }
}
