/**
 * Zentech-Inc
 * Copyright (C) 2016 All Rights Reserved.
 */
package com.mjj.util.serialization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author zhoujp
 * @version $Id JavaSerializeUtil.java, v 0.1 2016-07-28 19:26 zhoujp Exp $$
 */
public class JavaSerializeUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaSerializeUtil.class);

    public JavaSerializeUtil(){
        super();
    }

    /**
     * 克隆
     */
    public static Object clone(Serializable object) {
        return deserialize(serialize(object));
    }

    /**
     * 序列化
     * @param obj
     * @param outputStream
     */
    public static void serialize(Serializable obj, OutputStream outputStream) {
        if (outputStream == null) {
            throw new IllegalArgumentException("The OutputStream must not be null");
        }
        ObjectOutputStream out = null;
        try {
            // stream closed in the finally
            out = new ObjectOutputStream(outputStream);
            out.writeObject(obj);

        } catch (IOException ex) {
            LOGGER.error("serialize Error",ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(),ex);
            }
        }
    }

    /**
     * 序列化
     * @param obj
     * @return
     */
    public static byte[] serialize(Serializable obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        serialize(obj, baos);
        return baos.toByteArray();
    }

    /**
     * 反序列化
     * @param inputStream
     * @return
     */
    public static Object deserialize(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("The InputStream must not be null");
        }
        ObjectInputStream in = null;
        try {
            // stream closed in the finally
            in = new ObjectInputStream(inputStream);
            return in.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            LOGGER.error("serialize Error",ex);
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(),ex);
            }
        }
    }

    /**
     * 反序列化
     */
    public static Object deserialize(byte[] objectData) {
        if (objectData == null) {
            throw new IllegalArgumentException("The byte[] must not be null");
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
        return deserialize(bais);
    }
}