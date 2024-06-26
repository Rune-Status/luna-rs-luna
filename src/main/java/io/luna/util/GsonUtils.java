package io.luna.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A static-utility class that contains functions for Google GSON.
 *
 * @author lare96
 */
public final class GsonUtils {

    /**
     * A general purpose {@link Gson} instance.
     */
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().disableInnerClassSerialization().
            setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    /**
     * Shortcut to function {@link Gson#fromJson(JsonElement, Class)}.
     */
    public static <T> T getAsType(JsonElement element, Class<T> clazz) {
        return GSON.fromJson(element, clazz);
    }

    /**
     * Shortcut to function {@link Gson#toJsonTree(Object)}.
     */
    public static JsonElement toJsonTree(Object obj) {
        return GSON.toJsonTree(obj);
    }

    /**
     * Shortcut to function {@link Gson#toJson(JsonElement, Appendable)}.
     */
    public static void writeJson(JsonElement element, Path path) throws IOException {
        try (FileWriter fw = new FileWriter(path.toFile())) {
            fw.append(GSON.toJson(element));
        }
    }

    /**
     * Shortcut function to {@link Gson#fromJson(Reader, Type)}.
     */
    public static <T> T readAsType(Path path, Class<T> clazz) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(path)) {
            return GSON.fromJson(br, clazz);
        }
    }

    /**
     * A private constructor to discourage external instantiation.
     */
    private GsonUtils() {
    }
}
