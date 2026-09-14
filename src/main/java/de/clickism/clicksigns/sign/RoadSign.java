package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.sign.element.TextStyle;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.sign.texture.source.TiledTextureSource;
import de.clickism.clicksigns.util.PixelSized;
import de.clickism.clicksigns.util.nbt.NbtReader;
import de.clickism.clicksigns.util.nbt.NbtWriter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a road sign with a front texture, back texture, and elements.
 *
 * <h3>Overview</h3>
 *
 * <h4>Textures</h4>
 * The front texture is the main texture of the road sign, and is used for calculating the size of the sign.
 * The back texture is just used for rendering.
 *
 * <h4>Alignment</h4>
 * The alignment of the road sign determines how the road sign is aligned relative to the block it is placed on.
 * Unlike how element alignments work, it will always try to cover the block if possible, and align the rest of the sign.
 *
 * <h4>Elements</h4>
 * The elements of the road sign are used for rendering symbols, texts, etc. on the road sign.
 * Their render order is determined by their type, that means all elements of the same type will render in the same
 * z-index. With the order being: (Sign Texture) -> Plate -> Symbol -> Text.
 *
 * <h3>Coordinate System</h3>
 * The coordinate system of the road sign starts with <strong>(0, 0) in the bottom left corner</strong> of the road sign,
 * with the x-axis going to the right and the y-axis going up. The coordinates are in pixels.
 * <p>
 * <strong>Note:</strong> This is different from how the UI coordinate system works,
 * which has (0, 0) in the top left corner and the y-axis going down.
 * So the conversion should be handled properly when rendering the road sign in the UI.
 * <p>
 * Element coordinates are restricted to integer values, but after alignment, they can be floating point values.
 * In which case, they should be rendered as precisely as possible, without rounding, which goes for both the UI
 * and the road sign rendering.
 * <p>
 * Text elements are a special case, because they have a scale alongside different style options.
 * For more information, refer to the {@link TextElement} class.
 *
 * @param frontSource texture of the road sign
 * @param backSource  texture of the back of the road sign
 * @param elements    elements of the road sign
 * @param alignment  alignment of the road sign
 */
public record RoadSign(
    TextureSource frontSource,
    TextureSource backSource,
    List<SignElement> elements,
    Alignment alignment
) implements PixelSized {
    /**
     * The default alignment for road signs when no alignment is set.
     */
    public static Alignment DEFAULT_ALIGNMENT = Alignment.TOP_CENTER;
    /**
     * The default symbol texture.
     */
    public static ResourceLocation DEFAULT_SYMBOL_TEXTURE = ClickSigns.signAsset("symbols/arrows/right_curvy.png");
    /**
     * The default road sign to use when no road sign is set.
     */
    public static RoadSign DEFAULT = new RoadSign(
        new TiledTextureSource(ClickSigns.signAsset("tilesets/default/white.png"), 32, 16),
        new TiledTextureSource(ClickSigns.signAsset("tilesets/backs/back.png"), 32, 16),
        List.of(
            new SymbolElement(2, 8, Alignment.CENTER_RIGHT, SignRegistries.SYMBOLS.get(DEFAULT_SYMBOL_TEXTURE)),
            new TextElement(9, 10, Alignment.TEXT_RIGHT, "", 1f, TextStyle.DEFAULT),
            new TextElement(9, 6, Alignment.TEXT_RIGHT, "", 1f, TextStyle.DEFAULT),
            new TextElement(9, 2, Alignment.TEXT_RIGHT, "", 1f, TextStyle.DEFAULT)
        ),
        DEFAULT_ALIGNMENT
    );
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

    /**
     * Gets the color resolver for this road sign.
     *
     * @return the color resolver, or a default color resolver if the texture is not a tiled texture or the tileset could not be resolved
     */
    public ColorResolver colorResolver() {
        // Use background's color resolver
        return frontSource.colorResolver();
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

    @Override
    public int width() {
        return frontTexture().width();
    }

    @Override
    public int height() {
        return frontTexture().height();
    }

    /**
     * Resizes the road sign to the specified width and height.
     *
     * @param width  the new width of the road sign
     * @param height the new height of the road sign
     * @return a new road sign with the updated size
     */
    public RoadSign resized(int width, int height) {
        if (width == width() && height == height()) {
            // No need to resize
            return this;
        }
        var front = frontSource.resize(width, height);
        var back = backSource.resize(width, height);
        return this.withFront(front).withBack(back);
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
    public RoadSign withElements(Collection<SignElement> elements) {
        return new RoadSign(frontSource, backSource, new ArrayList<>(elements), alignment);
    }

    /**
     * Creates a new road sign with the given element replaced.
     *
     * @param oldElement the element to be replaced
     * @param newElement the new element to replace the old one
     * @return a new road sign with the updated elements
     */
    public RoadSign replaceElement(SignElement oldElement, SignElement newElement) {
        var newElements = new ArrayList<>(elements);
        int index = newElements.indexOf(oldElement);
        if (index != -1) {
            newElements.set(index, newElement);
        }
        return withElements(newElements);
    }

    /**
     * Creates a new road sign with the given element removed.
     *
     * @param element the element to be removed
     * @return a new road sign with the updated elements
     */
    public RoadSign removeElement(SignElement element) {
        var newElements = new ArrayList<>(elements);
        newElements.remove(element);
        return withElements(newElements);
    }

    /**
     * Creates a new road sign with the given element added.
     *
     * @param element the element to be added
     * @return a new road sign with the updated elements
     */
    public RoadSign addElement(SignElement element) {
        var newElements = new ArrayList<>(elements);
        newElements.add(element);
        return withElements(newElements);
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
}
