package de.clickism.clicksigns.platform.forge
//? if forge {
/*import de.clickism.clicksigns.ClickSigns
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runForDist


@Mod(ClickSigns.MOD_ID)
@Mod.EventBusSubscriber
object ForgeEntrypoint {
    init {
        ClickSigns.initialize()
        ForgePlatform.ITEMS_REGISTRY.register(MOD_BUS)
        ForgePlatform.BLOCKS_REGISTRY.register(MOD_BUS)

        val obj = runForDist(
            clientTarget = {
                MOD_BUS.addListener(::onClientSetup)
                Minecraft.getInstance()
            },
            serverTarget = {
                MOD_BUS.addListener(::onServerSetup)
                "test"
            })
    }

    fun onClientSetup(event: FMLClientSetupEvent) {
    }

    fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
    }
}
*///?}