package me.clickism.clicksigns.sign;

import net.minecraft.util.Identifier;
import org.joml.Vector2f;

import java.util.List;

public final class RoadSign {

    private final Identifier templateId;
    private final int textureIndex;
    private final List<String> texts;
    private final Alignment alignment;

    public RoadSign(
            Identifier templateId,
            int textureIndex,
            List<String> texts,
            Alignment alignment
    ) {
        this.templateId = templateId;
        this.textureIndex = textureIndex;
        this.texts = texts;
        this.alignment = alignment;
    }

    public Identifier templateId() {
        return templateId;
    }

    public int getTextureIndex() {
        return textureIndex;
    }

    public List<String> getTexts() {
        return texts;
    }


    public Alignment getAlignment() {
        return alignment;
    }

    public enum Alignment {
        TOP_LEFT(new Vector2f(-1, 1)),
        TOP_CENTER(new Vector2f(0, 1)),
        TOP_RIGHT(new Vector2f(1, 1)),
        CENTER_LEFT(new Vector2f(-1, 0)),
        CENTER(new Vector2f(0, 0)),
        CENTER_RIGHT(new Vector2f(1, 0)),
        BOTTOM_LEFT(new Vector2f(-1, -1)),
        BOTTOM_CENTER(new Vector2f(0, -1)),
        BOTTOM_RIGHT(new Vector2f(1, -1));

        private final Vector2f offset;

        Alignment(Vector2f offset) {
            this.offset = offset;
        }

        public float getXOffset() {
            return offset.x;
        }

        public float getYOffset() {
            return offset.y;
        }
    }

}