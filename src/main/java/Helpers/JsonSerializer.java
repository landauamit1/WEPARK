package Helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.lang.reflect.Type;

public class JsonSerializer {
    private static final Gson gson = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setDateFormat("MMM dd, yyyy hh:mm:ss aaa")
            .create();

    public static Gson getGson() {
        return gson;
    }

    public static String serialize(Object o) {
        return gson.toJson(o);
    }
    public static String serialize(Object o,Type type) {
        return gson.toJson(o,type);
    }
    public static <T> T deserialize(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T deserialize(Reader json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }


    public static <T> T deserialize(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }
}
