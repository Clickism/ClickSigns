package de.clickism.clicksigns

import de.clickism.clicksigns.registry.ModBlocks
import net.minecraft.resources.ResourceLocation

object ClickSigns {
    const val MOD_ID = "clicksigns"

    fun initialize() {
        ModBlocks.initialize()
    }

    fun identifier(name: String) = ResourceLocation.tryBuild(MOD_ID, name) ?: error("Invalid resource name: $name")
}