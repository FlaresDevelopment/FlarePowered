package net.flarepowered.core.data.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

public class JsonFile<T> {

    File path;
    public HashMap<String, T> rawData = new HashMap<>();

    public JsonFile(File path) {
        this.path = path;
    }
    public void setObject(String key, T object, Class<?> type) {
        readData(type);
        if(rawData.containsKey(key))
            rawData.replace(key, object);
        rawData.put(key, object);
    }

    public T getObject(String key, Class<?> type) {
        readData(type);
        Gson gson = new Gson();
        if(rawData.containsKey(key))
            return rawData.get(key);
        return null;
    }

    public void readData(Class<?> type) {
        Gson gson = new Gson();
        Type token = new TypeToken<HashMap<String, ?>>(){}.getType();
        Type mapType = newParameterizedType(HashMap.class, String.class, type);
        try {
            this.rawData = gson.fromJson(new FileReader(path), mapType);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveChanges() {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(rawData, writer);
        } catch (IOException e) {
            System.out.println("Failed to write to file: " + e.getMessage());
        }
    }
    public static ParameterizedType newParameterizedType(final Class<?> rawType, final Type... typeArguments) {
        return new ParameterizedType() {
            public Type getRawType() {
                return rawType;
            }

            public Type[] getActualTypeArguments() {
                return typeArguments;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }
}