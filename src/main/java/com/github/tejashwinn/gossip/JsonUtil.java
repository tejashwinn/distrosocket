package com.github.tejashwinn.gossip;



import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;


public class JsonUtil {
    static final Jsonb jsonb = JsonbBuilder.create();


    public static String toJson(Object o) {
        return jsonb.toJson(o);
    }


    public static <T> T fromJson(String s, Class<T> cls) {
        return jsonb.fromJson(s, cls);
    }
}