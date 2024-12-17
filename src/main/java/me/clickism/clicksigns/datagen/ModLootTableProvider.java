package me.clickism.clicksigns.datagen;

import me.clickism.clicksigns.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;
import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput
            //? if >=1.20.5
            , CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup
    ) {
        super(dataOutput
                //? if >=1.20.5
                , registryLookup
        );
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.ROAD_SIGN);
    }
}