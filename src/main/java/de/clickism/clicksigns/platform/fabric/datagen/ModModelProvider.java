package de.clickism.clicksigns.platform.fabric.datagen;

import de.clickism.clicksigns.ClickSignsBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;

/**
 * Model generator for blocks and items.
 */
class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        generator.createTrivialCube(ClickSignsBlocks.ROAD_SIGN.get());
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        generator.generateFlatItem(
                ClickSignsBlocks.ROAD_SIGN.get().asItem(),
                ModelTemplates.FLAT_ITEM
        );
    }
}
