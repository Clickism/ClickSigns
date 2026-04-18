package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.sign.texture.source.TiledTextureSource;
import de.clickism.clicksigns.util.nbt.NbtReader;
import de.clickism.clicksigns.util.nbt.NbtWriter;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

/**
 * Road sign class.
 *
 * @param frontSource texture of the road sign
 * @param backSource  texture of the back of the road sign
 * @param elements    elements of the road sign
 */
public record RoadSign(
        TextureSource frontSource,
        TextureSource backSource,
        List<SignElement> elements,
        Alignment alignment
) {
    /**
     * The default alignment for road signs when no alignment is set.
     */
    public static Alignment DEFAULT_ALIGNMENT = Alignment.TOP_CENTER;
    /**
     * The default road sign to use when no road sign is set.
     */
    public static RoadSign DEFAULT = new RoadSign(
            new TiledTextureSource(ClickSigns.signAsset("tilesets/default/white.png"), 32, 16),
            new TiledTextureSource(ClickSigns.signAsset("tilesets/backs/back.png"), 32, 16),
            List.of(
                    new SymbolElement(2, 8, Alignment.CENTER_RIGHT, SignRegistries.SYMBOLS.get(ClickSigns.signAsset("symbols/arrows/right_curvy.png"))),
                    new TextElement(9, 10, Alignment.TOP_RIGHT, "Main Street", 1f, "foreground", null),
                    new TextElement(9, 6, Alignment.TOP_RIGHT, "Main Street", 1f, "foreground", null),
                    new TextElement(9, 2, Alignment.TOP_RIGHT, "Main Street", 1f, "white", "brown")
            ),
            DEFAULT_ALIGNMENT
    );

    /**
     * Gets the color resolver for this road sign.
     *
     * @return the color resolver, or a default color resolver if the texture is not a tiled texture or the tileset could not be resolved
     */
    public ColorResolver colorResolver() {
        // Use background's color resolver
        if (frontSource instanceof TiledTextureSource tiled) {
            var tileSet = tiled.resolveTileSet();
            if (tileSet != null) {
                return tileSet.colorResolver();
            }
        }
        return ColorResolver.withDefault();
    }

    /**
     * Resolves the front texture of the road sign.
     *
     * @return the resolved front texture
     */
    public Texture frontTexture() {
        return frontSource.resolve(colorResolver());
    }

    /**
     * Resolves the back texture of the road sign.
     *
     * @return the resolved back texture
     */
    public Texture backTexture() {
        return backSource.resolve(colorResolver());
    }

    /**
     * Creates a new road sign with the given texture.
     * <p>
     * Keeps the existing back texture and elements.
     *
     * @param frontSource new texture for the road sign
     * @return a new road sign with the updated texture
     */
    public RoadSign withFront(TextureSource frontSource) {
        return new RoadSign(frontSource, backSource, elements, alignment);
    }

    /**
     * Creates a new road sign with the given back texture.
     * <p>
     * Keeps the existing front texture and elements.
     *
     * @param backSource new back texture for the road sign
     * @return a new road sign with the updated back texture
     */
    public RoadSign withBack(TextureSource backSource) {
        return new RoadSign(frontSource, backSource, elements, alignment);
    }

    /**
     * Creates a new road sign with the given elements.
     *
     * @param elements new elements for the road sign
     * @return a new road sign with the updated elements
     */
    public RoadSign withElements(List<SignElement> elements) {
        return new RoadSign(frontSource, backSource, elements, alignment);
    }

    /**
     * Creates a new road sign with the given alignment.
     *
     * @param alignment new alignment for the road sign
     * @return a new road sign with the updated alignment
     */
    public RoadSign withAlignment(Alignment alignment) {
        return new RoadSign(frontSource, backSource, elements, alignment);
    }

    /**
     * Writer for packets
     */
    public static final FriendlyByteBuf.Writer<RoadSign> PACKET_WRITER = (buf, sign) -> {
        TextureSource.PACKET_WRITER.accept(buf, sign.frontSource());
        TextureSource.PACKET_WRITER.accept(buf, sign.backSource());
        buf.writeCollection(sign.elements(), SignElement.PACKET_WRITER);
        buf.writeInt(sign.alignment().ordinal());
    };

    /**
     * Reader for packets
     */
    public static final FriendlyByteBuf.Reader<RoadSign> PACKET_READER = (buf) -> {
        var front = TextureSource.PACKET_READER.apply(buf);
        var back = TextureSource.PACKET_READER.apply(buf);
        var elements = buf.readList(SignElement.PACKET_READER);
        var alignment = Alignment.values()[buf.readInt()];
        return new RoadSign(front, back, elements, alignment);
    };

    /**
     * Writer for NBT
     */
    public static final NbtWriter.Writer<RoadSign> NBT_WRITER = (tag, sign) -> {
        var front = tag.createWriter();
        var back = tag.createWriter();
        TextureSource.NBT_WRITER.write(front, sign.frontSource());
        TextureSource.NBT_WRITER.write(back, sign.backSource());
        tag.putCompound("front", front.asCompoundTag());
        tag.putCompound("back", back.asCompoundTag());
        tag.putCollection("elements", sign.elements, SignElement.NBT_WRITER);
        tag.putString("alignment", sign.alignment().name());
    };

    /**
     * Reader for NBT
     */
    public static final NbtReader.Reader<RoadSign> NBT_READER = (tag) -> {
        var frontCompound = tag.getCompound("front").orElseThrow();
        var backCompound = tag.getCompound("back").orElseThrow();
        var front = TextureSource.NBT_READER.read(frontCompound);
        var back = TextureSource.NBT_READER.read(backCompound);
        var elements = tag.getCollection("elements", SignElement.NBT_READER).orElse(List.of());
        var alignment = Alignment.valueOf(tag.getString("alignment").orElse(DEFAULT_ALIGNMENT.name()));
        return new RoadSign(front, back, new ArrayList<>(elements), alignment);
    };
}
