/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns.sign;

import me.clickism.clicksigns.util.VersionHelper;
import net.minecraft.util.Identifier;

public class RoadSignTexture {
    private final Identifier front;
    private final Identifier back;
    private final int[] colors;
    private final String name;

    public RoadSignTexture(Identifier front, Identifier back, int[] colors, String name) {
        this.front = front;
        this.back = back;
        this.colors = colors;
        this.name = name;
    }

    public Identifier getFrontTexture() {
        return front;
    }

    public Identifier getBackTexture() {
        return back;
    }

    public String getName() {
        return name;
    }

    public int getColor(int index) {
        if (colors.length <= index) {
            return 0;
        }
        int color = colors[index];
        return VersionHelper.normalizeColor(color);
    }
}
