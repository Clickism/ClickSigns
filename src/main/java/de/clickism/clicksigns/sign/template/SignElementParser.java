package de.clickism.clicksigns.sign.template;

import com.google.gson.JsonObject;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.util.JsonHandler;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Parser for sign elements from JSON objects.
 *
 * TODO: Fix, texture sources are not fully encodable in json!
 */
public class SignElementParser implements JsonHandler {
    /**
     * Parses a sign element from a JSON object.
     *
     * @param object the JSON object to parse
     * @return the parsed sign element
     */
    public SignElement parse(JsonObject object) {
        var type = getTypeOrThrow(object);
        switch (type) {
            case "text" -> {
                var textElementJson = fromJsonOrThrow(object, TextElementJson.class);
                return textElementJson.toTextElement();
            }
            case "symbol" -> {
                var symbolElementJson = fromJsonOrThrow(object, SymbolElementJson.class);
                return symbolElementJson.toSymbolElement();
            }
            case "plate" -> {
                var plateElementJson = fromJsonOrThrow(object, PlateElementJson.class);
                return plateElementJson.toPlateElement();
            }
            default -> {
                throw new IllegalArgumentException("Unknown sign element type: " + type);
            }
        }
    }

    /**
     * Converts a sign element to a JSON object, including the type of the element as a property.
     *
     * @param element      the sign element to convert
     * @param includeTexts whether to include written text in the JSON output
     * @return the JSON object representing the sign element
     */
    public JsonObject toJson(SignElement element, boolean includeTexts) {
        var signElementJson = toSignElementJson(element, includeTexts);
        var jsonObject = toJsonObject(signElementJson);
        jsonObject.addProperty("type", element.typeKey());
        return jsonObject;
    }

    /**
     * Converts a sign element to a JSON object representation.
     *
     * @param element      the sign element to convert
     * @param includeTexts whether to include written text in the JSON output
     * @return the JSON object representation of the sign element
     */
    private Object toSignElementJson(SignElement element, boolean includeTexts) {
        if (element instanceof TextElement textElement) {
            return new TextElementJson(
                    nullIfDefault(textElement.alignment(), TextElementJson.DEFAULT_ALIGNMENT),
                    new Position(textElement.localX(), textElement.localY()),
                    // Only include the text if includeTexts is true, otherwise set it to null
                    includeTexts
                            ? nullIfDefault(textElement.text(), TextElementJson.DEFAULT_TEXT)
                            : null,
                    nullIfDefault(textElement.scale(), TextElementJson.DEFAULT_SCALE),
                    nullIfDefault(textElement.color(), TextElementJson.DEFAULT_COLOR),
                    textElement.backgroundColor()
            );
        }
        if (element instanceof SymbolElement symbolElement) {
            return new SymbolElementJson(
                    symbolElement.symbol().identifier(),
                    nullIfDefault(symbolElement.alignment(), SymbolElementJson.DEFAULT_ALIGNMENT),
                    new Position(symbolElement.localX(), symbolElement.localY())
            );
        }
        if (element instanceof PlateElement plateElement) {
            return new PlateElementJson(
                    nullIfDefault(plateElement.alignment(), PlateElementJson.DEFAULT_ALIGNMENT),
                    new Position(plateElement.localX(), plateElement.localY()),
                    plateElement.front().resolve(ColorResolver.empty()).width(),
                    plateElement.front().resolve(ColorResolver.empty()).height(),
                    TextureSource.textureLocationOf(plateElement.front()),
                    TextureSource.textureLocationOf(plateElement.back())
            );
        }
        throw new IllegalArgumentException("Unknown sign element type: " + element.getClass().getName());
    }

    private <T> @Nullable T nullIfDefault(@Nullable T value, T defaultValue) {
        if (value == null) return null;
        if (value.equals(defaultValue)) return null;
        return value;
    }

    /**
     * Position of a sign element.
     *
     * @param x the x coordinate of the element
     * @param y the y coordinate of the element
     */
    private record Position(int x, int y) {}

    /**
     * Json format for a symbol element.
     *
     * @param symbol    the symbol to display
     * @param alignment the alignment of the symbol
     * @param position  the local position of the symbol
     */
    private record SymbolElementJson(
            ResourceLocation symbol,
            @Nullable Alignment alignment,
            @Nullable Position position
    ) {
        private static final Alignment DEFAULT_ALIGNMENT = Alignment.CENTER;

        /**
         * Converts the JSON object to a symbol element object
         */
        SymbolElement toSymbolElement() {
            var pos = position != null ? position : new Position(0, 0);
            return new SymbolElement(
                    pos.x,
                    pos.y,
                    alignment != null ? alignment : DEFAULT_ALIGNMENT,
                    SignRegistries.SYMBOLS.get(symbol)
            );
        }
    }

    /**
     * Json format for a text element.
     *
     * @param alignment       the alignment of the text
     * @param position        the local position of the text
     * @param text            the placeholder text to display
     * @param scale           the scale of the text
     * @param color           the color of the text, as a hex string or a color name
     * @param backgroundColor the color of the text background, as a hex string or a color name, or null for no background
     */
    private record TextElementJson(
            @Nullable Alignment alignment,
            @Nullable Position position,
            String text,
            @Nullable Float scale,
            @Nullable String color,
            @Nullable String backgroundColor
    ) {
        private static final Alignment DEFAULT_ALIGNMENT = Alignment.TOP_RIGHT;
        private static final String DEFAULT_TEXT = "";
        private static final float DEFAULT_SCALE = 1f;
        private static final String DEFAULT_COLOR = "foreground";

        /**
         * Converts the JSON object to a text element object
         */
        private TextElement toTextElement() {
            var pos = position != null ? position : new Position(0, 0);
            return new TextElement(
                    pos.x,
                    pos.y,
                    alignment != null ? alignment : DEFAULT_ALIGNMENT,
                    text != null ? text : DEFAULT_TEXT,
                    scale != null ? scale : DEFAULT_SCALE,
                    color != null ? color : DEFAULT_COLOR,
                    backgroundColor
            );
        }
    }

    /**
     * Json format for a plate element.
     *
     * @param alignment the alignment of the plate
     * @param position  the local position of the plate
     * @param width     the width of the plate in pixels
     * @param height    the height of the plate in pixels
     * @param front     the front texture of the plate
     * @param back      the back texture of the plate
     */
    private record PlateElementJson(
            @Nullable Alignment alignment,
            @Nullable Position position,
            int width,
            int height,
            ResourceLocation front,
            ResourceLocation back
    ) {
        private static final Alignment DEFAULT_ALIGNMENT = Alignment.CENTER;

        /**
         * Converts the JSON object to a plate element object
         */
        private PlateElement toPlateElement() {
            var pos = position != null ? position : new Position(0, 0);
            return new PlateElement(
                    pos.x,
                    pos.y,
                    alignment != null ? alignment : DEFAULT_ALIGNMENT,
                    TextureSource.parse(front, width, height),
                    TextureSource.parse(back, width, height)
            );
        }
    }

}
