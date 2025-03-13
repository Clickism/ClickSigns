/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns.sign;

import net.minecraft.util.Identifier;

public record RoadSignPack(String id, String name) {
    public Identifier identifier(String path) {
        return Identifier.of(id, path);
    }
}
