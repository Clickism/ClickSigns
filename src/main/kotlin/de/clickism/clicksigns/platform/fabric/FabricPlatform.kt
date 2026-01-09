package de.clickism.clicksigns.platform.fabric
//? if fabric {
import de.clickism.clicksigns.ClickSigns
import de.clickism.clicksigns.platform.ObjectSupplier
import de.clickism.clicksigns.platform.PlatformInterface
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour

object FabricPlatform : PlatformInterface {
    override fun <T : Item> registerItem(
        name: String,
        settings: Item.Properties,
        itemSupplier: (Item.Properties) -> T
    ): ObjectSupplier<T> {
        val itemKey = ResourceKey.create(Registries.ITEM, ClickSigns.identifier(name))
        val item = itemSupplier(settings.setId(itemKey))
        val registeredItem = Registry.register(
            BuiltInRegistries.ITEM,
            itemKey,
            item
        )
        return ObjectSupplier { registeredItem }
    }

    override fun <T : Block> registerBlock(
        name: String,
        settings: BlockBehaviour.Properties,
        blockSupplier: (BlockBehaviour.Properties) -> T
    ): ObjectSupplier<T> {
        val blockKey = ResourceKey.create(Registries.BLOCK, ClickSigns.identifier(name))
        val block = blockSupplier(settings.setId(blockKey))
        val registeredBlock = Registry.register(
            BuiltInRegistries.BLOCK,
            blockKey,
            block
        )
        return ObjectSupplier { registeredBlock }
    }

    override fun <T : Block> registerBlockWithItem(
        name: String,
        settings: BlockBehaviour.Properties,
        blockSupplier: (BlockBehaviour.Properties) -> T
    ): ObjectSupplier<T> {
        val block = registerBlock(name, settings, blockSupplier)
        registerItem(name, Item.Properties().useBlockDescriptionPrefix()) { BlockItem(block.get(), it) }
        return block
    }
}
//?}