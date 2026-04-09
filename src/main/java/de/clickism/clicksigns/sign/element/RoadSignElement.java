package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.util.Alignment;
import de.clickism.clicksigns.util.texture.Texture;
import net.minecraft.network.FriendlyByteBuf;

import java.awt.*;

/**
 * An element that can be placed on a road sign.
 */
public abstract sealed class RoadSignElement permits TextElement, SymbolElement {
    private final int localX;
    private final int localY;
    private final Alignment alignment;

    /**
     * Creates a new road sign element at the given local coordinates.
     *
     * @param localX    Local X coordinate
     * @param localY    Local Y coordinate
     * @param alignment Alignment of the element
     */
    public RoadSignElement(int localX, int localY, Alignment alignment) {
        this.localX = localX;
        this.localY = localY;
        this.alignment = alignment;
    }

    /**
     * Gets the local X coordinate of this element.
     * Local coordinates are relative to the bottom-left corner of the road sign, with (0, 0) being the bottom-left corner.
     *
     * @return Local X coordinate
     */
    public int localX() {
        return localX;
    }

    /**
     * Gets the local Y coordinate of this element.
     * Local coordinates are relative to the bottom-left corner of the road sign, with (0
     *
     * @return Local Y coordinate
     */
    public int localY() {
        return localY;
    }

    /**
     * Gets the alignment of this element.
     * The alignment determines how the element should be positioned.
     *
     * @return the alignment of this element
     */
    public Alignment alignment() {
        return alignment;
    }

    public static final FriendlyByteBuf.Writer<RoadSignElement> WRITER = (buf, element) -> {
        buf.writeInt(typeOf(element));
        buf.writeInt(element.localX());
        buf.writeInt(element.localY());
        buf.writeInt(element.alignment().ordinal());
        if (element instanceof TextElement text) {
            buf.writeFloat(text.scale());
            buf.writeInt(text.color().getRGB());
            buf.writeUtf(text.text());
        } else if (element instanceof SymbolElement symbol) {
            Texture.WRITER.accept(buf, symbol.texture());
        }
    };

    public static final FriendlyByteBuf.Reader<RoadSignElement> READER = (buf) -> {
        int type = buf.readInt();
        int localX = buf.readInt();
        int localY = buf.readInt();
        Alignment alignment = Alignment.values()[buf.readInt()];
        if (type == 1) {
            float scale = buf.readFloat();
            int color = buf.readInt();
            String text = buf.readUtf();
            return new TextElement(localX, localY, alignment, text, new Color(color, true), scale);
        } else {
            Texture texture = Texture.READER.apply(buf);
            return new SymbolElement(localX, localY, alignment, texture);
        }
    };

    private static int typeOf(RoadSignElement element) {
        return element instanceof TextElement ? 1 : 0;
    }
}
