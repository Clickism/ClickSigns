package de.clickism.clicksigns.sign.reload.sign;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.reload.SignReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class PackListener implements SignReloadListener {
    private static final String PACK_FILE = "pack.json";

    @Override
    public void onReload(ResourceManager manager) {
        SignRegistries.PACK_NAMES.clear();
        forEachResource(manager, "", PACK_FILE, (location, resource) -> {
            var pack = fromJsonOrNull(resource, PackJson.class);
            if (pack == null) return;
            // Check that the file name is exactly "pack.json"
            var path = location.getPath();
            if (!path.equals(fromRoot(PACK_FILE))) {
                ClickSigns.LOGGER.error(
                    "Pack file {} in must be located in 'signs/pack.json'. Ignoring...",
                    location
                );
            }
            var namespace = location.getNamespace();
            SignRegistries.PACK_NAMES.put(namespace, pack.name());
        });
    }

    protected record PackJson(
        String name
    ) {
    }
}
