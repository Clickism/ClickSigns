package de.clickism.clicksigns.sign.codec;

import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.serialization.codec.CommonCodec;
import de.clickism.clicksigns.serialization.codec.PacketCodec;
import de.clickism.clicksigns.serialization.codec.TagCodec;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.element.*;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.ui.components.CommonComponents;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashSet;
import java.util.Set;

/**
 * Codec for serializing and deserializing {@link SignElement} objects.
 */
public interface SignElementCodec {
    /**
     * Gets the codec for serializing and deserializing sign elements.
     *
     * @return the codec for sign elements
     */
    static CommonCodec<SignElement> codec() {
        return CommonCodec.of(
            TagCodec.of(
                (tag, element) -> {
                    var type = element.typeKey();
                    tag.putString("type", type);
                    tag.putInt("x", element.x());
                    tag.putInt("y", element.y());
                    tag.putString("alignment", element.alignment().name());
                    if (element instanceof TextElement text) {
                        tag.putFloat("scale", text.scale());
                        tag.putString("text", text.text());
                        // Write text style
                        var styleTag = tag.createTag();
                        var style = text.style();
                        styleTag.putString("color", style.color());
                        styleTag.putString("backgroundColor", style.backgroundColor().orElse(null));
                        styleTag.putString("outlineColor", style.outlineColor().orElse(null));
                        styleTag.putInt("outlineWidth", style.outlineWidth());
                        styleTag.putInt("paddingX", style.paddingX());
                        styleTag.putInt("paddingY", style.paddingY());
                        styleTag.putInt("textAlignment", text.style().textAlignment().ordinal());
                        styleTag.putInt("lineGap", text.style().lineGap());
                        if (style.isBold()) {
                            styleTag.putBoolean("bold", true);
                        }
                        if (style.isItalic()) {
                            styleTag.putBoolean("italic", true);
                        }
                        if (style.isUnderline()) {
                            styleTag.putBoolean("underlined", true);
                        }
                        if (style.isStrikethrough()) {
                            styleTag.putBoolean("strikethrough", true);
                        }
                        tag.putTag("style", styleTag);
                    } else if (element instanceof SymbolElement symbol) {
                        tag.putResourceLocation("symbol", symbol.symbolId());
                        var textureTag = tag.createTag();
                        TextureSource.codec().writeTag(textureTag, symbol.textureSource());
                        tag.putTag("texture", textureTag);
                    } else if (element instanceof PlateElement plate) {
                        var frontTag = tag.createTag();
                        var backTag = tag.createTag();
                        TextureSource.codec().writeTag(frontTag, plate.frontSource());
                        TextureSource.codec().writeTag(backTag, plate.backSource());
                        tag.putTag("front", frontTag);
                        tag.putTag("back", backTag);
                        tag.putBoolean("match", plate.matchSignTextures());
                    }
                },
                (tag) -> {
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
                            var styleTag = tag.getTag("style").orElseThrow();
                            var color = styleTag.getString("color").orElseThrow();
                            var backgroundColor = styleTag.getString("backgroundColor").orElse(null);
                            var outlineColor = styleTag.getString("outlineColor").orElse(null);
                            var outlineWidth = styleTag.getInt("outlineWidth").orElseThrow();
                            var paddingX = styleTag.getInt("paddingX").orElseThrow();
                            var paddingY = styleTag.getInt("paddingY").orElseThrow();
                            var textAlignmentOrdinal = styleTag.getInt("textAlignment").orElse(TextStyle.TextAlignment.CENTER.ordinal());
                            var textAlignment = TextStyle.TextAlignment.values()[textAlignmentOrdinal];
                            var lineGap = styleTag.getInt("lineGap").orElse(0);
                            Set<TextStyle.Formatting> formattings = new HashSet<>();
                            if (styleTag.getBoolean("bold").orElse(false)) {
                                formattings.add(TextStyle.Formatting.BOLD);
                            }
                            if (styleTag.getBoolean("italic").orElse(false)) {
                                formattings.add(TextStyle.Formatting.ITALIC);
                            }
                            if (styleTag.getBoolean("underlined").orElse(false)) {
                                formattings.add(TextStyle.Formatting.UNDERLINE);
                            }
                            if (styleTag.getBoolean("strikethrough").orElse(false)) {
                                formattings.add(TextStyle.Formatting.STRIKETHROUGH);
                            }
                            var style = new TextStyle(
                                color,
                                backgroundColor,
                                outlineColor,
                                outlineWidth,
                                paddingX,
                                paddingY,
                                textAlignment,
                                lineGap,
                                formattings
                            );
                            yield new TextElement(localX, localY, alignment, text, scale, style);
                        }
                        case SymbolElement.TYPE -> {
                            var symbolId = tag.getResourceLocation("symbol").orElseThrow();
                            var textureTag = tag.getTag("texture").orElseThrow();
                            var texture = TextureSource.codec().readTag(textureTag);
                            yield new SymbolElement(localX, localY, alignment, symbolId, texture);
                        }
                        case PlateElement.TYPE -> {
                            var frontTag = tag.getTag("front").orElseThrow();
                            var backTag = tag.getTag("back").orElseThrow();
                            var front = TextureSource.codec().readTag(frontTag);
                            var back = TextureSource.codec().readTag(backTag);
                            var match = tag.getBoolean("match").orElse(true);
                            yield new PlateElement(localX, localY, alignment, front, back, match);
                        }
                        default -> throw new IllegalArgumentException("Unknown element type: " + type);
                    };
                }
            ),
            PacketCodec.of(
                (buf, element) -> {
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
                        buf.writeBoolean(style.isBold());
                        buf.writeBoolean(style.isItalic());
                        buf.writeBoolean(style.isUnderline());
                        buf.writeBoolean(style.isStrikethrough());
                    } else if (element instanceof SymbolElement symbol) {
                        buf.writeResourceLocation(symbol.symbolId());
                        TextureSource.codec().writePacket(buf, symbol.textureSource());
                    } else if (element instanceof PlateElement plate) {
                        TextureSource.codec().writePacket(buf, plate.frontSource());
                        TextureSource.codec().writePacket(buf, plate.backSource());
                        buf.writeBoolean(plate.matchSignTextures());
                    }
                },
                (buf) -> {
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
                            Set<TextStyle.Formatting> formattings = new HashSet<>();
                            if (buf.readBoolean()) {
                                formattings.add(TextStyle.Formatting.BOLD);
                            }
                            if (buf.readBoolean()) {
                                formattings.add(TextStyle.Formatting.ITALIC);
                            }
                            if (buf.readBoolean()) {
                                formattings.add(TextStyle.Formatting.UNDERLINE);
                            }
                            if (buf.readBoolean()) {
                                formattings.add(TextStyle.Formatting.STRIKETHROUGH);
                            }
                            var style = new TextStyle(
                                color,
                                backgroundColor,
                                outlineColor,
                                outlineWidth,
                                paddingX,
                                paddingY,
                                textAlignment,
                                lineGap,
                                formattings
                            );
                            yield new TextElement(localX, localY, alignment, text, scale, style);
                        }
                        case SymbolElement.TYPE -> {
                            var symbolId = buf.readResourceLocation();
                            var source = TextureSource.codec().readPacket(buf);
                            yield new SymbolElement(localX, localY, alignment, symbolId, source);
                        }
                        case PlateElement.TYPE -> {
                            var front = TextureSource.codec().readPacket(buf);
                            var back = TextureSource.codec().readPacket(buf);
                            var match = buf.readBoolean();
                            yield new PlateElement(localX, localY, alignment, front, back, match);
                        }
                        default -> throw new IllegalArgumentException("Unknown element type: " + type);
                    };
                }
            )
        );
    }
}
