package de.clickism.clicksigns.platform;

import de.clickism.clicksigns.platform.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

import static net.minecraft.world.item.Item.*;

/**
 * Platform wrapper interface
 */
public interface Platform {
    /**
     * Gets the current platform instance
     *
     * @return Current platform instance
     */
    static Platform get() {
        //? if fabric {
        return de.clickism.clicksigns.platform.fabric.FabricPlatform.INSTANCE;
        //? } elif forge {
        /*return de.clickism.clicksigns.platform.forge.ForgePlatform.INSTANCE;
         *///? } else {
        /*throw new UnsupportedOperationException("No platform implementation found");
         *///? }
    }

    /**
     * Gets the network instance of the current platform
     *
     * @return Network instance
     */
    static Network network() {
        return get().getNetwork();
    }

    /**
     * Gets the network instance
     *
     * @return Network instance
     */
    Network getNetwork();

    /**
     * Registers a new item
     */
    <T extends Item> Supplier<T> registerItem(
            String name,
            Properties settings,
            ItemFactory<T> itemSupplier
    );

    /**
     * Registers a new block
     */
    <T extends Block> Supplier<T> registerBlock(
            String name,
            BlockBehaviour.Properties settings,
            BlockFactory<T> blockSupplier
    );

    /**
     * Registers a new block and its item
     */
    <T extends Block> Supplier<T> registerBlockWithItem(
            String name,
            BlockBehaviour.Properties settings,
            BlockFactory<T> blockSupplier
    );

    /**
     * Registers a new block entity type
     */
    <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntityType(
            String name,
            BlockEntityFactory<T> blockEntitySupplier,
            Supplier<Block> block
    );

    /**
     * Adds an item to a creative tab
     */
    void addItemToCreativeTab(ResourceKey<CreativeModeTab> tab, Supplier<? extends Item> item);

    /**
     * Factory for creating items
     *
     * @param <T> Type of the item
     */
    interface ItemFactory<T extends Item> {
        /**
         * Creates a new item instance
         *
         * @param settings Item settings
         * @return New item instance
         */
        T create(Properties settings);
    }

    /**
     * Factory for creating blocks
     *
     * @param <T> Type of the block
     */
    interface BlockFactory<T extends Block> {
        /**
         * Creates a new block instance
         *
         * @param settings Block settings
         * @return New block instance
         */
        T create(BlockBehaviour.Properties settings);
    }

    interface BlockEntityFactory<T extends BlockEntity> {
        /**
         * Creates a new block entity instance
         *
         * @return New block entity instance
         */
        T create(BlockPos pos, BlockState state);
    }

    /**
     * Adds a reload listener to the resource manager
     *
     * @param listener Reload listener to add
     */
    void addReloadListener(ReloadListener listener);

    /**
     * Reload listener interface
     */
    interface ReloadListener {
        /**
         * Called when the resource manager is reloaded
         */
        void onReload(ResourceManager manager);
    }
}
