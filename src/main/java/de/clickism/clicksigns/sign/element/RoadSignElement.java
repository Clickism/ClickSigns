package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.sign.registry.SymbolRegistry;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.util.Alignment;
import de.clickism.clicksigns.sign.texture.Texture;
import net.minecraft.network.FriendlyByteBuf;

/**
 * An element that can be placed on a road sign.
 */
public sealed interface RoadSignElement permits TextElement, SymbolElement {
    /**
     * Gets the local X coordinate of this element.
     * Local coordinates are relative to the bottom-left corner of the road sign, with (0, 0) being the bottom-left corner.
     *
     * @return Local X coordinate
     */
    int localX();

    /**
     * Gets the local Y coordinate of this element.
     * Local coordinates are relative to the bottom-left corner of the road sign, with (0
     *
     * @return Local Y coordinate
     */
    int localY();

    /**
     * Gets the alignment of this element.
     * The alignment determines how the element should be positioned.
     *
     * @return the alignment of this element
     */
    Alignment alignment();

    /**
     * Writer for packets
     */
    FriendlyByteBuf.Writer<RoadSignElement> WRITER = (buf, element) -> {
        buf.writeInt(element instanceof TextElement ? 1 : 0);
        buf.writeInt(element.localX());
        buf.writeInt(element.localY());
        buf.writeInt(element.alignment().ordinal());
        if (element instanceof TextElement text) {
            buf.writeFloat(text.scale());
            buf.writeUtf(text.color());
            var backgroundColor = text.backgroundColor() != null ? text.backgroundColor() : "";
            buf.writeUtf(backgroundColor);
            buf.writeUtf(text.text());
        } else if (element instanceof SymbolElement symbol) {
            buf.writeResourceLocation(symbol.symbol().identifier());
        }
    };

    /**
     * Reader for packets
     */
    FriendlyByteBuf.Reader<RoadSignElement> READER = (buf) -> {
        int type = buf.readInt();
        int localX = buf.readInt();
        int localY = buf.readInt();
        Alignment alignment = Alignment.values()[buf.readInt()];
        if (type == 1) {
            var scale = buf.readFloat();
            var color = buf.readUtf();
            var backgroundColor = buf.readUtf();
            if (backgroundColor.isEmpty()) {
                backgroundColor = null;
            }
            var text = buf.readUtf();
            return new TextElement(localX, localY, alignment, text, scale, color, backgroundColor);
        } else {
            var id = buf.readResourceLocation();
            var symbol = SymbolRegistry.getSymbol(id);
            return new SymbolElement(localX, localY, alignment, symbol);
        }
    };
}
