/**
 * Zentech-Inc
 * Copyright (C) 2016 All Rights Reserved.
 */
package com.mjj.util.serialization;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

import java.io.Writer;
import java.util.List;

/**
 * @author zhoujp
 * @version $Id XmlSerializeUtil.java, v 0.1 2016-08-01 20:20 zhoujp Exp $$
 *          XML序列化
 */
public class XmlSerializeUtil {
    /**
     * Java对象转Xml字符串（序列化）
     * @param object
     * @return
     */
    public static String beanToXml(Object object){
        XStream stream = new XStream(new XppDriver() {
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new PrettyPrintWriter(out) {
                    public void startNode(String name) {
                        // 去掉包名
                        if (name.indexOf(".") > -1) {
                            name = name.substring(name.lastIndexOf(".") + 1);
                        }
                        super.startNode(name);
                    }
                };
            }
        });
        stream.registerConverter(new MuConverter());
        return stream.toXML(object);
    }

    /**
     * Xml字符串转Java对象（反序列化）
     * @param xml
     * @param rootType 根元素对应的Java类型
     * @param collectionTypes 集合类型
     * @return
     */
    public static Object xmlToBean(String xml,Class<?> rootType, List<Class<?>> collectionTypes){
        XStream stream = new XStream();
        stream.alias(rootType.getName(), rootType);
        for (Class<?> clazz : collectionTypes) {
            stream.alias(clazz.getSimpleName(), clazz);
        }
        stream.registerConverter(new MuConverter());
        return stream.fromXML(xml);
    }
}