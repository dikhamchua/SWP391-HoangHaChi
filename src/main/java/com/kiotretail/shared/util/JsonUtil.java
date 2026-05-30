package com.kiotretail.shared.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * Utility wrapper around Gson for consistent JSON serialization/deserialization
 * across the application.
 *
 * <p>Uses ISO-8601 style date format: {@code yyyy-MM-dd'T'HH:mm:ss}.</p>
 */
public final class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create();

    private JsonUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Serialize the given object to a JSON string.
     *
     * @param obj the object to serialize
     * @return the JSON representation of {@code obj}
     */
    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * Deserialize the given JSON string into an instance of {@code clazz}.
     *
     * @param json  the JSON string
     * @param clazz the target class
     * @param <T>   the type of the desired object
     * @return the deserialized object
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    /**
     * Deserialize the given JSON string into an instance of the supplied
     * generic {@link Type}. Use this overload for parameterized types such as
     * {@code List<Foo>} via {@code TypeToken}.
     *
     * @param json the JSON string
     * @param type the target type
     * @param <T>  the type of the desired object
     * @return the deserialized object
     */
    public static <T> T fromJson(String json, Type type) {
        return GSON.fromJson(json, type);
    }
}
