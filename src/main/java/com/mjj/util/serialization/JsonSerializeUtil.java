/**
 * Zentech-Inc
 * Copyright (C) 2016 All Rights Reserved.
 */
package com.mjj.util.serialization;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

import java.util.List;
import java.util.Map;

/**
 * @author zhoujp
 * @version $Id JsonSerializeUtil.java, v 0.1 2016-08-01 17:00 zhoujp Exp $$
 */
public class JsonSerializeUtil {
    private static final SerializeConfig CONFIG;
    private static final SerializerFeature[] FEATURES = {SerializerFeature.WriteMapNullValue, // 输出空置字段
            SerializerFeature.WriteNullListAsEmpty, // list字段如果为null，输出为[]，而不是null
            SerializerFeature.WriteNullNumberAsZero, // 数值字段如果为null，输出为0，而不是null
            SerializerFeature.WriteNullBooleanAsFalse, // Boolean字段如果为null，输出为false，而不是null
            SerializerFeature.WriteNullStringAsEmpty // 字符类型字段如果为null，输出为""，而不是null
    };
    private static String dateFormat;

    static {
        dateFormat = "yyyy-MM-dd HH:mm:ss";
        CONFIG = new SerializeConfig();
        CONFIG.put(java.util.Date.class, new SimpleDateFormatSerializer(dateFormat)); // 使用和json-lib兼容的日期输出格式
        CONFIG.put(java.sql.Date.class, new SimpleDateFormatSerializer(dateFormat)); // 使用和json-lib兼容的日期输出格式
    }

    /**
     * POJO直接转化成json对象
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        return JSON.toJSONString(object, CONFIG, FEATURES);
    }
    /**
     * POJO直接转化成json对象{无特征配置}
     * @param object
     * @return
     */
    public static String toJSONNoFeatures(Object object) {
        return JSON.toJSONString(object, CONFIG);
    }

    /**
     * 转化成JSON对象
     * @param text
     * @return
     */
    public static Object toBean(String text) {
        return JSON.parse(text);
    }
    /**
     * 转化成JSON对象
     * @param text
     * @return
     */
    public static <T> T toBean(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }
    /**
     * 转化为数组
     * @param text
     * @param <T>
     * @return
     */
    public static <T> Object[] toArray(String text) {
        return toArray(text, null);
    }

    /**
     * 转化为数组
     * @param text
     * @param <T>
     * @return
     */
    public static <T> Object[] toArray(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz).toArray();
    }
    /**
     * 转化成List
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> toList(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz);
    }

    /**
     * 将string转化为序列化的json字符串
     * @param text
     * @return
     */
    public static Object textToJson(String text) {
        return JSON.parse(text);
    }

    /**
     * json字符串转化为map
     * @param s
     * @return
     */
    public static Map stringToMap(String s) {
        return JSONObject.parseObject(s);
    }

    /**
     * 将map转化为string
     * @param m
     * @return
     */
    public static String mapToString(Map m) {
        return JSONObject.toJSONString(m);
    }
}