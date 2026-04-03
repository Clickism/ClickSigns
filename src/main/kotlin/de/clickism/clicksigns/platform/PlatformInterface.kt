package de.clickism.clicksigns.platform

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour

//? if fabric {
import de.clickism.clicksigns.platform.fabric.FabricPlatform
import net.minecraft.world.item.CreativeModeTabs

typealias Platform = FabricPlatform
//?} elif neoforge {
/*import de.clickism.clicksigns.platform.neoforge.NeoforgePlatform

typealias Platform = NeoforgePlatform
*///?} elif forge {
/*import de.clickism.clicksigns.platform.forge.ForgePlatform

typealias Platform = ForgePlatform
*///?}

fun interface ObjectSupplier<T> {
    fun get(): T
}

interface PlatformInterface {
    fun <T : Item> registerItem(
        name: String,
        settings: Item.Properties,
        itemSupplier: (Item.Properties) -> T
    ): ObjectSupplier<T>

    fun <T : Block> registerBlock(
        name: String,
        settings: BlockBehaviour.Properties,
        blockSupplier: (BlockBehaviour.Properties) -> T
    ): ObjectSupplier<T>

    fun <T : Block> registerBlockWithItem(
        name: String,
        settings: BlockBehaviour.Properties, blockSupplier: (BlockBehaviour.Properties) -> T
    ): ObjectSupplier<T>

    // TODO: Add to creative tab
}