/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clicksigns.sign;

public enum Arrows {
    FORWARD,
    LEFT,
    RIGHT;

    public static Arrows fromString(String str) {
        return switch (str.toLowerCase()) {
            case "l" -> LEFT;
            case "r" -> RIGHT;
            case "f" -> FORWARD;
            default -> throw new IllegalArgumentException();
        };
    }
}
