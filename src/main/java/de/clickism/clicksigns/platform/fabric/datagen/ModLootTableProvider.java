package de.clickism.clicksigns.platform.fabric.datagen;

import de.clickism.clicksigns.ClickSignsBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;

//? if >= 1.21.1 {
import net.minecraft.core.HolderLookup;
import java.util.concurrent.CompletableFuture;
//?}

/**
 * Loot table generator
 */
class ModLootTableProvider extends FabricBlockLootSubProvider {

    public ModLootTableProvider(
            FabricPackOutput output
            //? if >= 1.21.1
            ,CompletableFuture<HolderLookup.Provider> registryLookup
            ) {
        super(
                output
                //? if >= 1.21.1
                ,registryLookup
        );
    }

    @Override
    public void generate() {
        dropSelf(ClickSignsBlocks.ROAD_SIGN.get());
    }
}
