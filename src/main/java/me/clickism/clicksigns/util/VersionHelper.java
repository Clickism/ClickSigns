/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns.util;

import net.minecraft.client.gui.widget.IconWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class VersionHelper {
    
    public static IconWidget createIconWidget(int x, int y, Identifier texture, int iconWidth, int iconHeight) {
        //? if >=1.20.5 {
        return IconWidget.create(x, y, texture, iconWidth, iconHeight);
        //?} else
        /*return new IconWidget(x, y, iconWidth, iconHeight, texture);*/
    }

    public static int normalizeColor(int color) {
        //? if >=1.21.6 {
        return 0xFF000000 | color;
        //?} else
        /*return color;*/
    }

    public static void playSound(PlayerEntity player, SoundEvent soundEvent, SoundCategory category, float volume, float pitch) {
        //? if >=1.21.1 {
        player.playSoundToPlayer(soundEvent, category, volume, pitch);
        //?} else
        /*player.playSound(soundEvent, category, volume, pitch);*/
    }
}
