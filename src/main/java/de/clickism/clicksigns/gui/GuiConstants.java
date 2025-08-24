/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clicksigns.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GuiConstants {
    public static int PIXELS_PER_BLOCK = 16;
    public static int BUTTON_HEIGHT = 20;
    public static int BUTTON_WIDTH = 100;

    public static float NOT_SELECTED_ALPHA = .3f;
}
