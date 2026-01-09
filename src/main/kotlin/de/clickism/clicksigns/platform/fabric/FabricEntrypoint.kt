package de.clickism.clicksigns.platform.fabric
//? if fabric {
import de.clickism.clicksigns.ClickSigns
import net.fabricmc.api.ModInitializer

class FabricEntrypoint : ModInitializer {
    override fun onInitialize() {
        ClickSigns.initialize()
    }
}
//?}