/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns.datagen;

import me.clickism.clicksigns.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;
import java.util.concurrent.CompletableFuture;

import static me.clickism.clicksigns.ClickSigns.unwrap;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput
            //? if >=1.20.5
            /*, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup*/
    ) {
        super(dataOutput
                //? if >=1.20.5
                /*, registryLookup*/
        );
    }

    @Override
    public void generate() {
        addDrop(unwrap(ModBlocks.ROAD_SIGN));
    }
}