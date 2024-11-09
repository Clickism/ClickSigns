package me.clickism.clicksigns.item;

import me.clickism.clicksigns.ClickSigns;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {

    private static Item registerItem(String id, Item item) {
        return Registry.register(Registries.ITEM, ClickSigns.identifier(id), item);
    }

    public static void registerItems() {
    }
}
