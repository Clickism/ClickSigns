package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.sign.texture.source.processors.AlphaMask;
import de.clickism.clicksigns.util.PixelSized;
import de.clickism.clicksigns.util.Size;

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
 * @param alignment   alignment of the road sign
 */
public record RoadSign(
    TextureSource frontSource,
    TextureSource backSource,
    List<SignElement> elements,
    Alignment alignment
) implements PixelSized {
    public static final Size MIN_SIGN_SIZE = new Size(6, 6);
    public static final Size MAX_SIGN_SIZE = new Size(144, 144); // 9 Blocks

    /**
     * The default alignment for road signs when no alignment is set.
     */
    public static Alignment DEFAULT_ALIGNMENT = Alignment.TOP_CENTER;

    public RoadSign {
        // Ensure that all plate elements match the sign's textures
        elements = elements.stream()
            .map(element -> {
                if (element instanceof PlateElement plate && plate.matchSignTextures()) {
                    return plate.matchTextures(frontSource, backSource);
                } else {
                    return element;
                }
            })
            .toList();
    }

    /**
     * Gets the default front texture source for road signs.
     *
     * @return the default front texture source
     */
    public static TextureSource defaultFrontSource() {
        return SignRegistries.TILE_SETS.get(ClickSigns.signAsset("tilesets/default/white.png"))
            .textureSource(32, 16);
    }

    /**
     * Gets the default back texture source for road signs.
     *
     * @return the default back texture source
     */
    public static TextureSource defaultBackSource() {
        return SignRegistries.TILE_SETS.get(ClickSigns.signAsset("tilesets/backs/back.png"))
            .textureSource(32, 16);
    }

    /**
     * Creates a new road sign with default properties.
     * <p>
     * Warning: {@link SignRegistries} should be initialized before calling this method, otherwise it will throw an exception.
     *
     * @return the default road sign
     */
    public static RoadSign createDefault() {
        return new RoadSign(
            defaultFrontSource(),
            defaultBackSource(),
            List.of(
                SymbolElement.createDefault().withPosition(5, 8),
                TextElement.createDefault().withPosition(9, 12).withAlignment(Alignment.CENTER_RIGHT),
                TextElement.createDefault().withPosition(9, 8).withAlignment(Alignment.CENTER_RIGHT),
                TextElement.createDefault().withPosition(9, 4).withAlignment(Alignment.CENTER_RIGHT)
            ),
            DEFAULT_ALIGNMENT
        );
    }

    /**
     * Masks the back texture with the front texture, so that the back texture always
     * matches up with the front texture.
     *
     * @param frontSource the front texture source
     * @param backSource  the back texture source
     * @return a new texture source with the back texture masked by the front texture
     */
    public static TextureSource maskedBackOf(TextureSource frontSource, TextureSource backSource) {
        // TODO: Make sure it works with asymmetrical textures, might need to flip the front texture as well before masking
        // Resize back to make sure it covers the front texture, and then mask it with the front texture
        backSource = backSource.resize(frontSource.resolve(ColorResolver.empty()));
        return backSource.addProcessor(new AlphaMask(frontSource));
    }

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
     * Creates a new road sign with the given alignment.
     *
     * @param alignment new alignment for the road sign
     * @return a new road sign with the updated alignment
     */
    public RoadSign withAlignment(Alignment alignment) {
        return new RoadSign(frontSource, backSource, elements, alignment);
    }
}
