package de.clickism.clicksigns.sign.reload;

import net.minecraft.server.packs.resources.ResourceManager;

public class TemplateListener implements RoadSignReloadListener {
    @Override
    public void onReload(ResourceManager manager) {
        manager.listResources(
                fromRoot("templates"),
                identifier -> identifier.getPath().endsWith(".template.json")
        ).forEach((location, resource) -> {

        });
    }
}
