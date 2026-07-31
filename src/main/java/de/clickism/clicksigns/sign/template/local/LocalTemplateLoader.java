package de.clickism.clicksigns.sign.template.local;

import com.google.gson.JsonObject;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.template.Template;
import de.clickism.clicksigns.sign.template.TemplateParser;
import de.clickism.clicksigns.util.JsonHandler;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.function.BiConsumer;

import static de.clickism.clicksigns.sign.reload.TemplateListener.TEMPLATE_EXTENSION;

public class LocalTemplateLoader implements JsonHandler {
    private static final TemplateParser TEMPLATE_PARSER = new TemplateParser();

    private final Path root;

    public LocalTemplateLoader(Path root) {
        this.root = root;
    }

    public void processAll(BiConsumer<Path, Template> consumer) throws IOException {
        try (var stream = Files.walk(root)) {
            stream.filter(path -> path.toString().endsWith(TEMPLATE_EXTENSION)).forEach(path -> {
                try {
                    var template = loadTemplate(path);
                    consumer.accept(path, template);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load template from path: " + path, e);
                }
            });
        }
    }

    public Template loadTemplate(Path path) throws IOException {
        var jsonObject = GSON.fromJson(Files.readString(path), JsonObject.class);
        var location = pathToResourceLocation(path);
        return TEMPLATE_PARSER.parse(jsonObject, location, null);
    }

    public void saveAsTemplate(Path path, Template.Meta meta, RoadSign sign, boolean includeTexts) {
        try {
            var jsonObject = TEMPLATE_PARSER.toJson(meta, sign, includeTexts);
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(jsonObject));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save template to path: " + path, e);
        }
    }

    private ResourceLocation pathToResourceLocation(Path path) {
        var relative = root.relativize(path);
        var name = relative.toString().toLowerCase(Locale.ROOT).replace("\\", "/").replace(TEMPLATE_EXTENSION, "").replaceAll("[^a-z0-9/._-]", "_");
        try {
            return new ResourceLocation("local", name);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create a valid ResourceLocation for local template: " + path, e);
        }
    }
}
