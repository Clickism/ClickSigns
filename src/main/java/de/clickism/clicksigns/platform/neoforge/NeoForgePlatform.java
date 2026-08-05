package de.clickism.clicksigns.platform.neoforge;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.platform.ReloadListener;
import de.clickism.clicksigns.platform.network.Network;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Forge platform implementation
 */
public class NeoForgePlatform implements Platform {
    /**
     * The fabric platform instance
     */
    public static final NeoForgePlatform INSTANCE = new NeoForgePlatform();

    private static final DeferredRegister<Item> ITEMS_REGISTRY =
            DeferredRegister.create(BuiltInRegistries.ITEM, ClickSigns.MOD_ID);

    private static final DeferredRegister<Block> BLOCKS_REGISTRY =
            DeferredRegister.create(BuiltInRegistries.BLOCK, ClickSigns.MOD_ID);

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE_REGISTRY =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ClickSigns.MOD_ID);

    private final List<TabEntry> tabEntries = new ArrayList<>();
    private final List<ReloadListener> reloadListeners = new ArrayList<>();

    private NeoForgePlatform() {
        // Singleton class
    }

    /**
     * Initializes the forge platform
     *
     * @param bus event bus
     */
    public void initialize(IEventBus bus) {
        ITEMS_REGISTRY.register(bus);
        BLOCKS_REGISTRY.register(bus);
        BLOCK_ENTITY_TYPE_REGISTRY.register(bus);
        bus.register(this); // Register events
    }

    @Override
    public Network getNetwork() {
        return NeoForgeNetwork.INSTANCE;
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(
            String name,
            Item.Properties settings,
            ItemFactory<T> itemSupplier
    ) {
        var holder = ITEMS_REGISTRY.register(name, () -> itemSupplier.create(settings));
        return () -> holder.get();
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(
            String name,
            BlockBehaviour.Properties settings,
            BlockFactory<T> blockSupplier
    ) {
        var holder = BLOCKS_REGISTRY.register(name, () -> blockSupplier.create(settings));
        return () -> holder.get();
    }

    @Override
    public <T extends Block> Supplier<T> registerBlockWithItem(
            String name,
            BlockBehaviour.Properties settings,
            BlockFactory<T> blockSupplier
    ) {
        var block = registerBlock(name, settings, blockSupplier);

        ITEMS_REGISTRY.register(name, () ->
                new BlockItem(
                        block.get(),
                        new Item.Properties()
                )
        );

        return block;
    }

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(
            String name,
            BlockEntityFactory<T> factory,
            Supplier<Block> block
    ) {
        return BLOCK_ENTITY_TYPE_REGISTRY.register(
                name,
                () -> BlockEntityType.Builder.of(factory::create, block.get()).build(null)
        );
    }

    @Override
    public void addItemToCreativeTab(ResourceKey<CreativeModeTab> tab, Supplier<? extends Item> item) {
        tabEntries.add(new TabEntry(tab, item));
    }

    @Override
    public void addReloadListener(ReloadListener listener) {
        reloadListeners.add(listener);
    }

    @SubscribeEvent
    public void onReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) manager -> {
            reloadListeners.forEach(listener -> listener.onReload(manager));
        });
    }

    @SubscribeEvent
    public void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        var tab = event.getTabKey();
        tabEntries.stream()
                .filter(entry -> entry.tab.equals(tab))
                .forEach(entry -> event.accept(entry.item.get()));
    }

    /**
     * A creative tab entry
     *
     * @param tab  the creative tab
     * @param item the item to add
     */
    private record TabEntry(ResourceKey<CreativeModeTab> tab, Supplier<? extends Item> item) {}
}