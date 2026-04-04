package de.clickism.clicksigns.platform.fabric;

import de.clickism.clicksigns.ClickSigns;

import de.clickism.clicksigns.platform.Platform;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

/**
 * Fabric platform implementation
 */
public class FabricPlatform implements Platform {

    public static final FabricPlatform INSTANCE = new FabricPlatform();

    private FabricPlatform() {
        // Singleton class
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(
            String name,
            Item.Properties settings,
            ItemFactory<T> itemSupplier
    ) {
        var itemKey = ResourceKey.create(Registries.ITEM, ClickSigns.identifier(name));
        var item = itemSupplier.create(settings);
        var registeredItem = Registry.register(
                BuiltInRegistries.ITEM,
                itemKey,
                item
        );
        return () -> registeredItem;
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(
            String name,
            BlockBehaviour.Properties settings,
            BlockFactory<T> blockSupplier
    ) {
        var blockKey = ResourceKey.create(Registries.BLOCK, ClickSigns.identifier(name));
        var block = blockSupplier.create(settings);
        var registeredBlock = Registry.register(
                BuiltInRegistries.BLOCK,
                blockKey,
                block
        );
        return () -> registeredBlock;
    }

    @Override
    public <T extends Block> Supplier<T> registerBlockWithItem(
            String name,
            BlockBehaviour.Properties settings,
            BlockFactory<T> blockSupplier
    ) {
        var block = registerBlock(name, settings, blockSupplier);
        registerItem(name, new Item.Properties(), props -> new BlockItem(block.get(), props));
        return block;
    }

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(
            String name,
            BlockEntityFactory<T> factory,
            Supplier<Block> block
    ) {
        return () -> FabricBlockEntityTypeBuilder.create(factory::create, block.get()).build();
    }

    @Override
    public void addItemToCreativeTab(ResourceKey<CreativeModeTab> tab, Supplier<? extends Item> item) {
        ItemGroupEvents.modifyEntriesEvent(tab)
                .register(entries -> entries.accept(item.get()));
    }
}
