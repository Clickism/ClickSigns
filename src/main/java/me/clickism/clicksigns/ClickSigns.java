/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns;

import de.clickism.modrinthupdatechecker.ModrinthUpdateChecker;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MinecraftVersion;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//? if forge
import net.minecraftforge.registries.RegistryObject;

public class ClickSigns {
    public static final String MOD_ID = "clicksigns";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Identifier ERROR_TEMPLATE_ID = identifier("error");

    public static String newerVersion = null;

    public static Identifier identifier(String name) {
        return Identifier.of(MOD_ID, name);
    }

    public static void checkUpdates() {
        String modVersion = FabricLoader.getInstance().getModContainer(ClickSigns.MOD_ID)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse(null);
        //? if >= 1.21.6 {
        /*String minecraftVersion = MinecraftVersion.CURRENT.name();
        *///?} else
        String minecraftVersion = MinecraftVersion.CURRENT.getName();
        new ModrinthUpdateChecker(ClickSigns.MOD_ID, "fabric", minecraftVersion).checkVersion(version -> {
            if (modVersion == null || ModrinthUpdateChecker.getRawVersion(modVersion).equals(version)) {
                return;
            }
            newerVersion = version;
            ClickSigns.LOGGER.info("Newer version available: {}", version);
        });
    }

    //? if fabric {
    /*public static <T> T unwrap(T block) {
        return block;
    }
    *///?}
    //? if forge {
    public static <T> T unwrap(RegistryObject<T> block) {
        return block.get();
    }
    //?}
}
