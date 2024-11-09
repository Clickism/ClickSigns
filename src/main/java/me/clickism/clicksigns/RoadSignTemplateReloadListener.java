package me.clickism.clicksigns;

import me.clickism.clicksigns.serialization.RoadSignTemplateParser;
import me.clickism.clicksigns.sign.RoadSignTemplateRegistration;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;

public class RoadSignTemplateReloadListener implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return ClickSigns.identifier("sign_template_reloader");
    }

    @Override
    public void reload(ResourceManager manager) {
        RoadSignTemplateRegistration.clearTemplates();
        manager.findResources(
                "sign_templates",
                identifier -> identifier.getPath().endsWith(".json")
        ).forEach((identifier, resource) -> {
            try (InputStream stream = resource.getInputStream()) {
                RoadSignTemplateParser.parseAndRegister(stream);
            } catch (Exception e) {
                ClickSigns.LOGGER.error("Error occurred while loading resource json " + identifier.toString(), e);
            }
        });
    }
}
