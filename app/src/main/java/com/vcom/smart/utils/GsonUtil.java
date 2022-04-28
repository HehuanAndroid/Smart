package com.vcom.smart.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Lzz on 2018/4/16 11:02
 */

public class GsonUtil {

    private final Gson gson;

    private GsonUtil() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
    }

    private static class gsonUtilHolder {
        private static final GsonUtil instance = new GsonUtil();
    }

    /**
     * 使用方法前调用getInstance,获得gsonUtil实例
     */
    public static GsonUtil getInstance() {
        return gsonUtilHolder.instance;
    }

    /**
     * 将对象转换成json字符串
     */
    public String toJson(Object obj) {
        if (obj == null) {
            return "{}";
        }
        return gson.toJson(obj);
    }

    /**
     * 在json字符串中，根据key值找到value
     */
    public String getValue(String data, String key) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            return jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean getBooleanValue(String data, String key) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            return jsonObject.getBoolean(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将json格式转换成map对象
     */
    public Map<String, Object> json2Map(String json) {
        Map<String, Object> objMap = null;
        if (gson != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            objMap = gson.fromJson(json, type);
        }
        // objMap = new HashMap<String, Object>();
        return objMap;
    }

    /**
     * 将json转换成bean对象
     */
    public <T> T json2Bean(String json, Class<T> clazz) {
        T obj = null;
        if (gson != null) {
            obj = gson.fromJson(json, clazz);
        }
        return obj;
    }

    /**
     * 将json格式转换成List对象
     */
    public <T> T json2List(String json, Type type) {
        if (gson != null) {
            return gson.fromJson(json, type);
        }
        return null;
    }

    /**
     * obj 转为 map
     */
    public Map<String, Object> Obj2Map(Object obj) {
        if (obj != null) {
            return json2Map(toJson(obj));
        }
        return null;
    }

    public Gson getGson() {
        return gson;
    }

}
