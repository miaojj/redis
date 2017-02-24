/**
 * Zentech-Inc
 * Copyright (C) 2016 All Rights Reserved.
 */
package com.mjj.util.serialization;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhoujp
 * @version $Id MuConverter.java, v 0.1 2016-08-02 10:41 zhoujp Exp $$
 */
public class MuConverter implements Converter {
    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    //判断字段是否属于要转换的类型
    @Override
    public boolean canConvert(Class paramClass) {
        return Date.class.isAssignableFrom(paramClass);
    }

    //对象转化为xml
    @Override
    public void marshal(Object object, HierarchicalStreamWriter writer, MarshallingContext context) {
        SimpleDateFormat format=new SimpleDateFormat(FORMAT);
        writer.setValue(format.format(object));
    }

    //xml转化为对象
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        try {
            SimpleDateFormat format=new SimpleDateFormat(FORMAT);
            return format.parse(reader.getValue());
        } catch (ParseException e) {
            throw new SerializationException(e);
        }
    }
}