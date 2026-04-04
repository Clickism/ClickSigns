package de.clickism.clicksigns;

import de.clickism.clicksigns.registry.ModBlockEntityTypes;
import de.clickism.clicksigns.registry.ModBlocks;
import net.minecraft.resources.ResourceLocation;

public class ClickSigns {
    public static final String MOD_ID = "clicksigns";

    public static void initialize() {
        ModBlocks.initialize();
        ModBlockEntityTypes.initialize();
    }

    public static ResourceLocation identifier(String path) {
        return ResourceLocation.tryBuild(MOD_ID, path);
    }
}
