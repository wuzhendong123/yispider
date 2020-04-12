package org.yi.spider.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @program: parentDemo
 * @description:
 * @author: zhendong.wu
 * @create: 2019-08-31 14:36
 **/
public class JsonUtils {
    private static volatile ObjectMapper mapper = new ObjectMapper();
static {
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
}
    private JsonUtils() {};

    public static ObjectMapper getInstance() {
        return mapper;
    }

    public static byte[] toJsonBytes(Object obj) {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJsonString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        if (json == null) return null;
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toObject(String json, TypeReference<T> typeReference) {
        if (json == null) return null;
        try {
            return mapper.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toObject(byte[] json, Class<T> clazz) {
        if (json == null) return null;
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toObject(byte[] json, TypeReference<T> typeReference) {
        if (json == null) return null;
        try {
            return mapper.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toJsonToObject(Object from, Class<T> clazz) {
        return JsonUtils.toObject(JsonUtils.toJsonString(from), clazz);
    }
}
