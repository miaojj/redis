/**
 * Zentech-Inc.com
 * Copyright (C) 2016 All Rights Reserved.
 */
package com.mjj.util.redis;

import redis.clients.jedis.Jedis;

/**
 * @author wujn
 * @version $Id RedisClientInvoker.java, v 0.1 2016-08-01 15:26 wujn Exp $$
 */
public interface RedisClientInvoker<T>  {
    public T invoke(Jedis jedis);
}
