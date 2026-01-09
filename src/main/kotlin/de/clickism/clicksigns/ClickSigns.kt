package de.clickism.clicksigns

import de.clickism.clicksigns.registry.ModItems
import net.minecraft.resources.Identifier

object ClickSigns {
    const val MOD_ID = "clicksigns"

    fun initialize() {
        ModItems.initialize()
    }

    fun identifier(name: String) = Identifier.fromNamespaceAndPath(MOD_ID, name)
}