package de.clickism.clicksigns.sign.template.local;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.template.Template;
import net.minecraft.client.Minecraft;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles loading/saving of personal templates stored in .minecraft/sign_templates
 */
public class LocalTemplateManager {
    private static final String LOCAL_TEMPLATE_DIR = "sign_templates";

    private final Map<Path, Template> templates = new ConcurrentHashMap<>();

    private final Path root;
    private final LocalTemplateLoader loader;

    public LocalTemplateManager() {
        this.root = Minecraft.getInstance().gameDirectory.toPath().resolve(LOCAL_TEMPLATE_DIR);
        this.loader = new LocalTemplateLoader(root);
    }

    public void initialize() {
        try {
            loader.processAll(templates::put);
        } catch (Exception e) {
            ClickSigns.LOGGER.error("Failed to load local templates from path: {}", root, e);
        }
    }

    public void reload() {
        templates.clear();
        initialize();
    }

    public Collection<Template> templates() {
        return templates.values();
    }

    public void saveAsTemplate(Template.Meta meta, RoadSign sign, boolean includeTexts) {
        var path = root.resolve(meta.name().toLowerCase() + ".template.json");
        int counter = 1;
        while (path.toFile().exists()) {
            path = root.resolve(meta.name().toLowerCase() + "_" + counter + ".template.json");
            counter++;
        }
        saveAsTemplate(path, meta, sign, includeTexts);
    }

    public void saveAsTemplate(Path path, Template.Meta meta, RoadSign sign, boolean includeTexts) {
        loader.saveAsTemplate(path, meta, sign, includeTexts);
    }
}
