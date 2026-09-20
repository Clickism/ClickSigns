package de.clickism.clicksigns.sign.template;

import com.google.gson.JsonObject;
import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.util.JsonHandler;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.function.BiConsumer;

import static de.clickism.clicksigns.sign.reload.sign.TemplateListener.TEMPLATE_EXTENSION;

public class LocalTemplateLoader implements JsonHandler {
    public static final String LOCAL_TEMPLATE_NAMESPACE = "local";
    private static final TemplateParser TEMPLATE_PARSER = new TemplateParser();
    private final Path root;

    public LocalTemplateLoader(Path root) {
        this.root = root;
        // Ensure the root directory exists
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            ClickSigns.LOGGER.error("Failed to create local template root directory: {}", root, e);
        }
    }

    public void tryProcessAll(BiConsumer<Path, Template> consumer) {
        try (var stream = Files.walk(root)) {
            stream.filter(path -> path.toString().endsWith(TEMPLATE_EXTENSION)).forEach(path -> {
                try {
                    var template = loadTemplate(path);
                    consumer.accept(path, template);
                } catch (Exception e) {
                    ClickSigns.LOGGER.error("Failed to load template from path: {}: {}", path, e.getMessage());
                }
            });
        } catch (Exception e) {
            ClickSigns.LOGGER.error("Failed to process local templates in directory: {}", root, e);
        }
    }

    public Template loadTemplate(Path path) throws Exception {
        var jsonObject = GSON.fromJson(Files.readString(path), JsonObject.class);
        var location = pathToResourceLocation(path);
        return TEMPLATE_PARSER.parse(jsonObject, location, null);
    }

    public void deleteTemplate(Path path) throws Exception {
        Files.deleteIfExists(path);
    }

    public void saveAsTemplate(Path path, Template.Meta meta, RoadSign sign, boolean includeTexts) {
        try {
            var jsonObject = TEMPLATE_PARSER.toJson(meta, sign, includeTexts);
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(jsonObject));
        } catch (IOException e) {
            ClickSigns.LOGGER.error("Failed to save template to path: {}", path, e);
        }
    }

    private ResourceLocation pathToResourceLocation(Path path) {
        var relative = root.relativize(path);
        var name = relative.toString().toLowerCase(Locale.ROOT)
            .replace("\\", "/")
            .replace(TEMPLATE_EXTENSION, "")
            .replaceAll("[^a-z0-9/._-]", "_");
        try {
            return ResourceLocation.tryBuild(LOCAL_TEMPLATE_NAMESPACE, name);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create a valid ResourceLocation for local template: " + path, e);
        }
    }
}
