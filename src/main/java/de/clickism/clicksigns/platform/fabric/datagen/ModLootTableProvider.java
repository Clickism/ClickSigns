package de.clickism.clicksigns.platform.fabric.datagen;

import de.clickism.clicksigns.ClickSignsBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

/**
 * Loot table generator
 */
class ModLootTableProvider extends FabricBlockLootTableProvider {

    public ModLootTableProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate() {
        dropSelf(ClickSignsBlocks.ROAD_SIGN.get());
    }
}
