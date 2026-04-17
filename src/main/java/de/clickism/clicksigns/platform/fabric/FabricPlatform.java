package de.clickism.clicksigns.platform.fabric;

import de.clickism.clicksigns.ClickSigns;

import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.platform.ReloadListener;
import de.clickism.clicksigns.platform.network.Network;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Fabric platform implementation
 */
public class FabricPlatform implements Platform {
    /**
     * The fabric platform instance
     */
    public static final FabricPlatform INSTANCE = new FabricPlatform();

    private static final ResourceLocation RELOAD_LISTENER_ID = ClickSigns.identifier("reload_listener");

    private final List<ReloadListener> reloadListeners = new ArrayList<>();

    private FabricPlatform() {
        // Singleton class
    }

    /**
     * Initializes the platform, registers reload listeners, etc.
     */
    public void initialize() {
        registerReloadListener();
    }

    @Override
    public Network getNetwork() {
        return FabricNetwork.INSTANCE;
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
        var type = FabricBlockEntityTypeBuilder.create(factory::create, block.get()).build();
        var registeredType = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ClickSigns.identifier(name), type);
        return () -> registeredType;
    }

    @Override
    public void addItemToCreativeTab(ResourceKey<CreativeModeTab> tab, Supplier<? extends Item> item) {
        ItemGroupEvents.modifyEntriesEvent(tab)
                .register(entries -> entries.accept(item.get()));
    }

    @Override
    public void addReloadListener(ReloadListener listener) {
        reloadListeners.add(listener);
    }

    private void registerReloadListener() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
                .registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public ResourceLocation getFabricId() {
                        return RELOAD_LISTENER_ID;
                    }

                    @Override
                    public void onResourceManagerReload(ResourceManager manager) {
                        reloadListeners.forEach(listener -> listener.onReload(manager));
                    }
                });
    }
}
