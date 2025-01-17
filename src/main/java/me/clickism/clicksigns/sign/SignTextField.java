/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns.sign;

import net.minecraft.util.math.Vec2f;

public class SignTextField {
    private static final float TEXT_PADDING = .5f;

    public final Vec2f guiPos;
    public final Vec2f renderPos;
    public final float maxWidth;
    public final float scale;

    private final int colorIndex;
    private final Alignment alignment;

    public enum Alignment {
        LEFT(new Vec2f(TEXT_PADDING, 0)),
        CENTER(Vec2f.ZERO),
        RIGHT(new Vec2f(-TEXT_PADDING, 0));

        public final Vec2f paddingOffset;

        Alignment(Vec2f paddingOffset) {
            this.paddingOffset = paddingOffset;
        }
    }

    public SignTextField(Vec2f guiPos, float maxWidth, int colorIndex, Alignment alignment, float scale) {
        this.guiPos = guiPos;
        this.renderPos = guiPos
                .add(alignment.paddingOffset)
                .add(switch (alignment) {
                    case LEFT -> Vec2f.ZERO;
                    case CENTER -> new Vec2f(maxWidth / 2, 0);
                    case RIGHT -> new Vec2f(maxWidth, 0);
                });
        this.maxWidth = maxWidth;
        this.colorIndex = colorIndex;
        this.alignment = alignment;
        this.scale = scale;
    }

    public int getColor(RoadSignTexture texture) {
        return texture.getColor(colorIndex);
    }

    public float getAlignedX(float textWidth) {
        return switch (alignment) {
            case LEFT -> 0;
            case CENTER -> -textWidth / 2;
            case RIGHT -> -textWidth;
        };
    }
}
