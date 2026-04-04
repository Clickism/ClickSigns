package de.clickism.clicksigns.platform.fabric;

import de.clickism.clicksigns.ClickSigns;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

/**
 * Fabric entrypoint
 */
public class FabricEntrypoint implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        ClickSigns.initialize();
    }

    @Override
    public void onInitializeClient() {

    }
}
