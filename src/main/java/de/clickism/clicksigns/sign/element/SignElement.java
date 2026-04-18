package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.registry.SymbolRegistry;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.util.nbt.TypeKeyed;
import de.clickism.clicksigns.util.nbt.NbtReader;
import de.clickism.clicksigns.util.nbt.NbtWriter;
import net.minecraft.network.FriendlyByteBuf;

/**
 * An element that can be placed on a road sign.
 */
public sealed interface SignElement extends TypeKeyed permits TextElement, SymbolElement {
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
    FriendlyByteBuf.Writer<SignElement> PACKET_WRITER = (buf, element) -> {
        var type = element.typeKey();
        buf.writeUtf(type);
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
    FriendlyByteBuf.Reader<SignElement> PACKET_READER = (buf) -> {
        var type = buf.readUtf();
        int localX = buf.readInt();
        int localY = buf.readInt();
        Alignment alignment = Alignment.values()[buf.readInt()];
        return switch (type) {
            case TextElement.TYPE -> {
                var scale = buf.readFloat();
                var color = buf.readUtf();
                var backgroundColor = buf.readUtf();
                if (backgroundColor.isEmpty()) {
                    backgroundColor = null;
                }
                var text = buf.readUtf();
                yield new TextElement(localX, localY, alignment, text, scale, color, backgroundColor);
            }
            case SymbolElement.TYPE -> {
                var id = buf.readResourceLocation();
                var symbol = SymbolRegistry.getSymbol(id);
                yield new SymbolElement(localX, localY, alignment, symbol);
            }
            default -> throw new IllegalArgumentException("Unknown element type: " + type);
        };
    };

    /**
     * Nbt writer
     */
    NbtWriter.Writer<SignElement> NBT_WRITER = (tag, element) -> {
        var type = element.typeKey();
        tag.putString("type", type);
        tag.putInt("localX", element.localX());
        tag.putInt("localY", element.localY());
        tag.putString("alignment", element.alignment().name());
        if (element instanceof TextElement text) {
            tag.putFloat("scale", text.scale());
            tag.putString("color", text.color());
            if (text.backgroundColor() != null) {
                tag.putString("backgroundColor", text.backgroundColor());
            }
            tag.putString("text", text.text());
        } else if (element instanceof SymbolElement symbol) {
            tag.putResourceLocation("symbol", symbol.symbol().identifier());
        }
    };

    /**
     * Nbt reader
     */
    NbtReader.Reader<SignElement> NBT_READER = (tag) -> {
        var type = tag.getString("type");
        int localX = tag.getInt("localX").orElseThrow();
        int localY = tag.getInt("localY").orElseThrow();
        Alignment alignment = Alignment.valueOf(tag.getString("alignment").orElseThrow());
        return switch (type.orElseThrow()) {
            case TextElement.TYPE -> {
                var scale = tag.getFloat("scale").orElseThrow();
                var color = tag.getString("color").orElseThrow();
                var backgroundColor = tag.getString("backgroundColor").orElse(null);
                var text = tag.getString("text").orElseThrow();
                yield new TextElement(localX, localY, alignment, text, scale, color, backgroundColor);
            }
            case SymbolElement.TYPE -> {
                var id = tag.getResourceLocation("symbol").orElseThrow();
                var symbol = SymbolRegistry.getSymbol(id);
                if (symbol == null) {
                    throw new IllegalArgumentException("Symbol with id " + id + " not found");
                }
                yield new SymbolElement(localX, localY, alignment, symbol);
            }
            default -> throw new IllegalArgumentException("Unknown element type: " + type);
        };
    };
}
