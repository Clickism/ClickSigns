package de.clickism.clicksigns.platform.fabric.datagen;

import de.clickism.clicksigns.ClickSignsBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

/**
 * Register block tags for the mod.
 */
class ModBlockTagsProvider extends FabricTagsProvider.BlockTagsProvider {

    public ModBlockTagsProvider(
        FabricPackOutput output,
        CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider lookup) {
        //~ if >=26.1 'getOrCreateTagBuilder' -> 'getOrCreateRawBuilder'
        getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
            //? if >=26.1 {
            .addElement(BuiltInRegistries.BLOCK.getKey(ClickSignsBlocks.ROAD_SIGN.get()));
            //?} else
            //.add(ClickSignsBlocks.ROAD_SIGN.get());
    }
}
