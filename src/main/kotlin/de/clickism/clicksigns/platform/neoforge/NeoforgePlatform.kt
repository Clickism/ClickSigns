package de.clickism.clicksigns.platform.neoforge
//? if neoforge {
/*import de.clickism.clicksigns.ClickSigns
import de.clickism.clicksigns.platform.ObjectSupplier
import de.clickism.clicksigns.platform.PlatformInterface
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredRegister

object NeoforgePlatform : PlatformInterface {
    val ITEMS_REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM, ClickSigns.MOD_ID)
    val BLOCKS_REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK, ClickSigns.MOD_ID)

    override fun <T : Item> registerItem(
        name: String,
        settings: Item.Properties,
        itemSupplier: (Item.Properties) -> T
    ): ObjectSupplier<T> {
        val itemKey = ResourceKey.create(Registries.ITEM, ClickSigns.identifier(name))
        val holder = ITEMS_REGISTRY.register(name) { -> itemSupplier(settings.setId(itemKey)) }
        return ObjectSupplier { holder.get() }
    }

    override fun <T : Block> registerBlock(
        name: String,
        settings: BlockBehaviour.Properties,
        blockSupplier: (BlockBehaviour.Properties) -> T
    ): ObjectSupplier<T> {
        val blockKey = ResourceKey.create(Registries.BLOCK, ClickSigns.identifier(name))
        val holder = BLOCKS_REGISTRY.register(name) { -> blockSupplier(settings.setId(blockKey)) }
        return ObjectSupplier { holder.get() }
    }

    override fun <T : Block> registerBlockWithItem(
        name: String,
        settings: BlockBehaviour.Properties,
        blockSupplier: (BlockBehaviour.Properties) -> T
    ): ObjectSupplier<T> {
        val block = registerBlock(name, settings, blockSupplier)
        val itemKey = ResourceKey.create(Registries.ITEM, ClickSigns.identifier(name))
        ITEMS_REGISTRY.register(name) { ->
            BlockItem(
                block.get(),
                Item.Properties().setId(itemKey).useBlockDescriptionPrefix()
            )
        }
        return block
    }
}
*///?}