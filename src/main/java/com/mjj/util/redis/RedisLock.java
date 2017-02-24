/**
 * Zentech-Inc
 * Copyright (C) 2016 All Rights Reserved.
 */
package com.mjj.util.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Random;

/**
 * @author zhujh
 * @version $Id RedisLock.java, v 0.1 2016-10-08 13:07 zhujh Exp $$
 */
public class RedisLock {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLock.class);
    //加锁标志
    private static final String LOCKED = "TRUE";
    private static final long ONE_MILLI_NANOS = 1000000L;
    //默认超时时间（毫秒）
    private static final long DEFAULT_TIME_OUT = 3000;
    private static final Random r = new Random();
    //锁的超时时间（秒），过期删除
    private static final int EXPIRE = 3 * 60;

    private Jedis jedis;
    private String key;
    //锁状态标志
    private boolean locked = false;

    public RedisLock(String key,Jedis jedis) {
        this.key = key;
        this.jedis = jedis;
    }

    /**
     * 获取锁，超时时间单位毫秒
     * @param timeout
     * @return
     */
    public boolean lock(long timeout) {
        long nano = System.nanoTime();
        timeout *= ONE_MILLI_NANOS;
        try {
            while ((System.nanoTime() - nano) < timeout) {
                if (jedis.setnx(key, LOCKED) == 1) {
                    jedis.expire(key, EXPIRE);
                    locked = true;
                    return locked;
                }
                // 短暂休眠，nano避免出现活锁
                Thread.sleep(3, r.nextInt(500));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
        }
        return false;
    }

    /**
     * 获取锁，默认超时时间3000毫秒
     * @return
     */
    public boolean lock() {
        return lock(DEFAULT_TIME_OUT);
    }

    /**
     * 无论是否加锁成功，必须调用
     */
    public void unlock() {
        try {
            if (locked)
                jedis.del(key);
        } finally {
            if(jedis !=null){
                jedis.close();
            }
        }
    }
}
