package de.clickism.clicksigns.util;

import com.google.gson.Gson;
import de.clickism.clicksigns.ClickSigns;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;

/**
 * Utility interface for handling JSON resources in reload listeners.
 */
public interface ResourceJsonHandler {
    /**
     * Gson instance for JSON parsing in reload listeners
     */
    Gson GSON = new Gson();

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
}
