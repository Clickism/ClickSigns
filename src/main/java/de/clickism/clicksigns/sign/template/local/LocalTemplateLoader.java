package de.clickism.clicksigns.sign.template.local;

import com.google.gson.JsonObject;
import de.clickism.clicksigns.sign.template.Template;
import de.clickism.clicksigns.sign.template.TemplateParser;
import de.clickism.clicksigns.util.JsonHandler;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;

public class LocalTemplateLoader implements JsonHandler {
    private static final TemplateParser TEMPLATE_PARSER = new TemplateParser();

    private final Path root;

    public LocalTemplateLoader(Path root) {
        this.root = root;
    }

    public void processAll(BiConsumer<Path, Template> consumer) throws IOException {
        try (var stream = Files.walk(root)) {
            stream
                    .filter(path -> path.toString().endsWith(".template.json"))
                    .forEach(path -> {
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

    private ResourceLocation pathToResourceLocation(Path path) {
        var relative = root.relativize(path);
        var name = relative.toString()
                .replace("\\", "/")
                .replace(".template.json", "");
        try {
            return new ResourceLocation("local", name);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create a valid ResourceLocation for local template: " + path, e);
        }
    }
}
