package de.clickism.clicksigns;

import de.clickism.clicksigns.registry.ModBlockEntityTypes;
import de.clickism.clicksigns.registry.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClickSigns {
    public static final String MOD_ID = "clicksigns";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void initialize() {
        ModBlocks.initialize();
        ModBlockEntityTypes.initialize();
    }

    public static ResourceLocation identifier(String path) {
        return ResourceLocation.tryBuild(MOD_ID, path);
    }
}
