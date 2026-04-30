package de.clickism.clicksigns.sign.template;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record TextVariant(
        String name,
        String color,
        @Nullable String backgroundColor
) {
}
