/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns.datagen;

import me.clickism.clicksigns.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

import static me.clickism.clicksigns.ClickSigns.unwrap;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        //? if >=1.21.6 {
        /*valueLookupBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.ROAD_SIGN);
        *///?} else {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(unwrap(ModBlocks.ROAD_SIGN));
        //?}
    }
}