/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clicksigns.sign;

public enum RoadSignCategory {
    TEMPLATE,
    PART,
    CUSTOM;

    public static RoadSignCategory fromString(String string) {
        return switch (string) {
            case "TEMPLATE" -> TEMPLATE;
            case "PART" -> PART;
            default -> CUSTOM;
        };
    }
}
