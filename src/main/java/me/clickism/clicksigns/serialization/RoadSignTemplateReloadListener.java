/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns.serialization;

import me.clickism.clicksigns.ClickSigns;
import me.clickism.clicksigns.sign.RoadSignTemplateRegistration;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;

public class RoadSignTemplateReloadListener implements SimpleSynchronousResourceReloadListener {
    private static final String SIGN_TEMPLATES_PATH = "sign_templates";

    @Override
    public Identifier getFabricId() {
        return ClickSigns.identifier("sign_template_reloader");
    }

    @Override
    public void reload(ResourceManager manager) {
        RoadSignTemplateRegistration.clearTemplates();
        manager.findResources(
                SIGN_TEMPLATES_PATH,
                identifier -> identifier.getPath().endsWith(".json")
        ).forEach((identifier, resource) -> {
            try (InputStream stream = resource.getInputStream()) {
                RoadSignTemplateParser.parseAndRegister(stream);
            } catch (Exception exception) {
                ClickSigns.LOGGER.error("Error occurred while loading resource json " + identifier.toString(), exception);
            }
        });
    }
}
