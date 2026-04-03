package de.clickism.clicksigns.platform.forge
//? if forge {
import de.clickism.clicksigns.ClickSigns
import de.clickism.clicksigns.platform.ObjectSupplier
import de.clickism.clicksigns.platform.PlatformInterface
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ForgePlatform : PlatformInterface {
    val ITEMS_REGISTRY: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, ClickSigns.MOD_ID)
    val BLOCKS_REGISTRY: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, ClickSigns.MOD_ID)

    override fun <T : Item> registerItem(
        name: String,
        settings: Item.Properties,
        itemSupplier: (Item.Properties) -> T
    ): ObjectSupplier<T> {
        val holder = ITEMS_REGISTRY.register(name) { -> itemSupplier(settings) }
        return ObjectSupplier { holder.get() }
    }

    override fun <T : Block> registerBlock(
        name: String,
        settings: BlockBehaviour.Properties,
        blockSupplier: (BlockBehaviour.Properties) -> T
    ): ObjectSupplier<T> {
        val holder = BLOCKS_REGISTRY.register(name) { -> blockSupplier(settings) }
        return ObjectSupplier { holder.get() }
    }

    override fun <T : Block> registerBlockWithItem(
        name: String,
        settings: BlockBehaviour.Properties,
        blockSupplier: (BlockBehaviour.Properties) -> T
    ): ObjectSupplier<T> {
        val block = registerBlock(name, settings, blockSupplier)
        ITEMS_REGISTRY.register(name) { ->
            BlockItem(
                block.get(),
                Item.Properties()
            )
        }
        return block
    }
}
//?}