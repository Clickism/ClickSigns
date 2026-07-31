package de.clickism.clicksigns.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.clickism.clicksigns.ClickSigns;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;

/**
 * Utility interface for handling JSON resources in reload listeners.
 */
public interface JsonHandler {
    /**
     * Gson instance for JSON parsing in reload listeners
     */
    Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .create();

    /**
     * Parses a JSON resource into an instance of the specified class.
     *
     * @param resource the resource to parse
     * @param clazz    the class to parse the JSON into
     * @param <T>      the type of the class to parse the JSON into
     * @return an instance of the specified class containing the parsed data
     * @throws RuntimeException if an error occurs while parsing the JSON resource
     */
    default <T> T fromJsonOrThrow(Resource resource, Class<T> clazz) throws RuntimeException {
        try {
            return GSON.fromJson(resource.openAsReader(), clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON resource from pack: " + resource.sourcePackId(), e);
        }
    }

    /**
     * Parses a JSON resource into an instance of the specified class, returning null if an error occurs.
     *
     * @param resource the resource to parse
     * @param clazz    the class to parse the JSON into
     * @param <T>      the type of the class to parse the JSON into
     * @return an instance of the specified class containing the parsed data, or null if an error occurs while parsing the JSON resource
     */
    default <T> @Nullable T fromJsonOrNull(Resource resource, Class<T> clazz) {
        try {
            return GSON.fromJson(resource.openAsReader(), clazz);
        } catch (Exception e) {
            ClickSigns.LOGGER.error("Failed to parse JSON resource from pack: " + resource.sourcePackId(), e);
            return null;
        }
    }

    default <T> T fromJsonOrThrow(JsonObject json, Class<T> clazz) throws RuntimeException {
        try {
            return GSON.fromJson(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON object: " + json.toString(), e);
        }
    }

    default <T> T fromJsonOrNull(JsonObject json, Class<T> clazz) {
        try {
            return GSON.fromJson(json, clazz);
        } catch (Exception e) {
            ClickSigns.LOGGER.error("Failed to parse JSON object: " + json.toString(), e);
            return null;
        }
    }

    default JsonObject toJsonObject(Object obj) {
        try {
            return GSON.toJsonTree(obj).getAsJsonObject();
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON: " + obj, e);
        }
    }

    /**
     * Gets the "type" field from a JSON object.
     *
     * @param json the JSON object to get the "type" field from
     * @return the value of the "type" field as a string
     * @throws RuntimeException if the "type" field is missing from the JSON object
     */
    default String getTypeOrThrow(JsonObject json) throws RuntimeException {
        var typeObj = json.get("type");
        if (typeObj == null) {
            throw new RuntimeException("JSON object is missing \"type\" field: " + json);
        }
        return typeObj.getAsString();
    }
}
