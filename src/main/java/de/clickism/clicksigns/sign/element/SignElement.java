package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.util.nbt.NbtReader;
import de.clickism.clicksigns.util.nbt.NbtWriter;
import de.clickism.clicksigns.util.nbt.TypeKeyed;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Represents an element of a road sign.
 * <p>
 * Elements are positioned using the sign's coordinate system, where (0, 0) is the bottom left corner of the sign.
 * For more information, see {@link de.clickism.clicksigns.sign.RoadSign}.
 */
public sealed interface SignElement extends TypeKeyed permits PlateElement, SymbolElement, TextElement {
    /**
     * Writer for packets
     */
    FriendlyByteBuf.Writer<SignElement> PACKET_WRITER = (buf, element) -> {
        var type = element.typeKey();
        buf.writeUtf(type);
        buf.writeInt(element.x());
        buf.writeInt(element.y());
        buf.writeInt(element.alignment().ordinal());
        if (element instanceof TextElement text) {
            buf.writeFloat(text.scale());
            buf.writeUtf(text.text());
            // Write text style
            var style = text.style();
            buf.writeUtf(style.color());
            buf.writeNullable(style.backgroundColor().orElse(null), FriendlyByteBuf::writeUtf);
            buf.writeNullable(style.outlineColor().orElse(null), FriendlyByteBuf::writeUtf);
            buf.writeInt(style.outlineWidth());
            buf.writeInt(style.paddingX());
            buf.writeInt(style.paddingY());
            buf.writeInt(style.textAlignment().ordinal());
            buf.writeInt(style.lineGap());
        } else if (element instanceof SymbolElement symbol) {
            buf.writeResourceLocation(symbol.symbol().identifier());
            // TODO: Texture source written but not read
            // TODO: Should symbols have id, or should they be identified based on their texture source's root?, would have to make
            // texture source more like a pipeline
            TextureSource.PACKET_WRITER.accept(buf, symbol.symbol().texture());
        } else if (element instanceof PlateElement plate) {
            TextureSource.PACKET_WRITER.accept(buf, plate.frontSource());
            TextureSource.PACKET_WRITER.accept(buf, plate.backSource());
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
                var text = buf.readUtf();
                // Read text style
                var color = buf.readUtf();
                var backgroundColor = buf.readNullable(FriendlyByteBuf::readUtf);
                var outlineColor = buf.readNullable(FriendlyByteBuf::readUtf);
                var outlineWidth = buf.readInt();
                var paddingX = buf.readInt();
                var paddingY = buf.readInt();
                var textAlignment = TextStyle.TextAlignment.values()[buf.readInt()];
                var lineGap = buf.readInt();
                var style = new TextStyle(
                    color,
                    backgroundColor,
                    outlineColor,
                    outlineWidth,
                    paddingX,
                    paddingY,
                    textAlignment,
                    lineGap
                );
                yield new TextElement(localX, localY, alignment, text, scale, style);
            }
            case SymbolElement.TYPE -> {
                var id = buf.readResourceLocation();
                var source = TextureSource.PACKET_READER.apply(buf);
                var symbol = SignRegistries.SYMBOLS.get(id).withTexture(source);
                yield new SymbolElement(localX, localY, alignment, symbol);
            }
            case PlateElement.TYPE -> {
                var front = TextureSource.PACKET_READER.apply(buf);
                var back = TextureSource.PACKET_READER.apply(buf);
                yield new PlateElement(localX, localY, alignment, front, back);
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
        tag.putInt("x", element.x());
        tag.putInt("y", element.y());
        tag.putString("alignment", element.alignment().name());
        if (element instanceof TextElement text) {
            tag.putFloat("scale", text.scale());
            tag.putString("text", text.text());
            // Write text style
            var styleTag = tag.createWriter();
            var style = text.style();
            styleTag.putString("color", style.color());
            styleTag.putString("backgroundColor", style.backgroundColor().orElse(null));
            styleTag.putString("outlineColor", style.outlineColor().orElse(null));
            styleTag.putInt("outlineWidth", style.outlineWidth());
            styleTag.putInt("paddingX", style.paddingX());
            styleTag.putInt("paddingY", style.paddingY());
            styleTag.putInt("textAlignment", text.style().textAlignment().ordinal());
            styleTag.putInt("lineGap", text.style().lineGap());
            tag.putCompound("style", styleTag.asCompoundTag());
        } else if (element instanceof SymbolElement symbol) {
            tag.putResourceLocation("symbol", symbol.symbol().identifier());
            var textureTag = tag.createWriter();
            TextureSource.NBT_WRITER.write(textureTag, symbol.symbol().texture());
            tag.putCompound("texture", textureTag.asCompoundTag());
        } else if (element instanceof PlateElement plate) {
            var frontTag = tag.createWriter();
            var backTag = tag.createWriter();
            TextureSource.NBT_WRITER.write(frontTag, plate.frontSource());
            TextureSource.NBT_WRITER.write(backTag, plate.backSource());
            tag.putCompound("front", frontTag.asCompoundTag());
            tag.putCompound("back", backTag.asCompoundTag());
        }
    };
    /**
     * Nbt reader
     */
    NbtReader.Reader<SignElement> NBT_READER = (tag) -> {
        var type = tag.getString("type");
        int localX = tag.getInt("x").orElseThrow();
        int localY = tag.getInt("y").orElseThrow();
        var alignmentString = tag.getString("alignment").orElseThrow();
        Alignment alignment = Alignment.valueOf(alignmentString);
        return switch (type.orElseThrow()) {
            case TextElement.TYPE -> {
                var scale = tag.getFloat("scale").orElseThrow();
                var text = tag.getString("text").orElseThrow();
                // Read text style
                var styleTag = tag.getCompound("style").orElseThrow();
                var color = styleTag.getString("color").orElseThrow();
                var backgroundColor = styleTag.getString("backgroundColor").orElse(null);
                var outlineColor = styleTag.getString("outlineColor").orElse(null);
                var outlineWidth = styleTag.getInt("outlineWidth").orElseThrow();
                var paddingX = styleTag.getInt("paddingX").orElseThrow();
                var paddingY = styleTag.getInt("paddingY").orElseThrow();
                var textAlignmentString = styleTag.getString("textAlignment").orElse("CENTER");
                var textAlignment = TextStyle.TextAlignment.valueOf(textAlignmentString);
                var lineGap = styleTag.getInt("lineGap").orElse(0);
                var style = new TextStyle(
                    color,
                    backgroundColor,
                    outlineColor,
                    outlineWidth,
                    paddingX,
                    paddingY,
                    textAlignment,
                    lineGap
                );
                yield new TextElement(localX, localY, alignment, text, scale, style);
            }
            case SymbolElement.TYPE -> {
                var id = tag.getResourceLocation("symbol").orElseThrow();
                var textureTag = tag.getCompound("texture").orElseThrow();
                var texture = TextureSource.NBT_READER.read(textureTag);
                var symbol = SignRegistries.SYMBOLS.get(id).withTexture(texture);
                yield new SymbolElement(localX, localY, alignment, symbol);
            }
            case PlateElement.TYPE -> {
                var frontTag = tag.getCompound("front").orElseThrow();
                var backTag = tag.getCompound("back").orElseThrow();
                var front = TextureSource.NBT_READER.read(frontTag);
                var back = TextureSource.NBT_READER.read(backTag);
                yield new PlateElement(localX, localY, alignment, front, back);
            }
            default -> throw new IllegalArgumentException("Unknown element type: " + type);
        };
    };

    /**
     * Gets the X coordinate of this element.
     *
     * @return X coordinate
     */
    int x();

    /**
     * Gets the Y coordinate of this element.
     *
     * @return Y coordinate
     */
    int y();

    /**
     * Gets the width of this element in sign space.
     *
     * @return Width of this element in sign space
     */
    float width();

    /**
     * Gets the height of this element in sign space.
     *
     * @return Height of this element in sign space
     */
    float height();

    /**
     * Returns the aligned X coordinate of this element in sign space, which is a floating point number.
     *
     * @return The aligned X coordinate of this element in sign space
     */
    default float alignedX() {
        float x = x();
        float width = width();
        // Center origin
        x -= width / 2f;
        // Align
        var offset = alignment().offset();
        x += offset.x * (width / 2f);
        return x;
    }

    /**
     * Returns the aligned Y coordinate of this element in sign space, which is a floating point number.
     *
     * @return The aligned Y coordinate of this element in sign space
     */
    default float alignedY() {
        float y = y();
        float height = height();
        // Center origin
        y -= height / 2f;
        // Align
        var offset = alignment().offset();
        y += offset.y * (height / 2f);
        return y;
    }

    /**
     * Gets the alignment of this element. The alignment determines how the element should be positioned.
     *
     * @return the alignment of this element
     */
    Alignment alignment();

    /**
     * Creates a new element with the given local coordinates, keeping the other properties the same.
     *
     * @param x local X coordinate
     * @param y local Y coordinate
     * @return a new element with the given local coordinates, keeping the other properties the same
     */
    SignElement withPosition(int x, int y);

    /**
     * Creates a new element with the given alignment, keeping the other properties the same.
     *
     * @param alignment the new alignment
     * @return a new element with the given alignment, keeping the other properties the same
     */
    SignElement withAlignment(Alignment alignment);
}
