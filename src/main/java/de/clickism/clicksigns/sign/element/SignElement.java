package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.util.nbt.NbtReader;
import de.clickism.clicksigns.util.nbt.NbtWriter;
import de.clickism.clicksigns.util.nbt.TypeKeyed;
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
     * Local coordinates are relative to the bottom-left corner of the road sign, with (0, 0) being the bottom-left corner.
     *
     * @return Local Y coordinate
     */
    int localY();

    int signWidth();

    int signHeight();

    default int guiWidth(float guiScale) {
        return (int) (signWidth() * guiScale);
    }

    default int guiHeight(float guiScale) {
        return (int) (signWidth() * guiScale);
    }

    /**
     * Gets the alignment of this element.
     * The alignment determines how the element should be positioned.
     *
     * @return the alignment of this element
     */
    Alignment alignment();

    /**
     * Creates a new element with the given local coordinates, keeping the other properties the same.
     *
     * @param localX local X coordinate
     * @param localY local Y coordinate
     * @return a new element with the given local coordinates, keeping the other properties the same
     */
    SignElement withPosition(int localX, int localY);

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
            TextureSource.PACKET_WRITER.accept(buf, symbol.symbol().texture());
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
                var source = TextureSource.PACKET_READER.apply(buf);
                var symbol = SignRegistries.SYMBOLS.get(id).withTexture(source);
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
            var textureTag = tag.createWriter();
            TextureSource.NBT_WRITER.write(textureTag, symbol.symbol().texture());
            tag.putCompound("texture", textureTag.asCompoundTag());
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
                var textureTag = tag.getCompound("texture").orElseThrow();
                var texture = TextureSource.NBT_READER.read(textureTag);
                var symbol = SignRegistries.SYMBOLS.get(id).withTexture(texture);
                yield new SymbolElement(localX, localY, alignment, symbol);
            }
            default -> throw new IllegalArgumentException("Unknown element type: " + type);
        };
    };
}
