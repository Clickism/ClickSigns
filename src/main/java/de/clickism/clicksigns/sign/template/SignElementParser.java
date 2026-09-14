package de.clickism.clicksigns.sign.template;

import com.google.gson.JsonObject;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.*;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.util.JsonHandler;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Parser for sign elements from JSON objects.
 * <p>
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
                new Position(textElement.x(), textElement.y()),
                // Only include the text if includeTexts is true, otherwise set it to null
                includeTexts
                    ? nullIfDefault(textElement.text(), TextElementJson.DEFAULT_TEXT)
                    : null,
                nullIfDefault(textElement.scale(), TextElementJson.DEFAULT_SCALE),
                new TextElementJson.TextStyleJson(
                    nullIfDefault(textElement.style().color(), TextElementJson.DEFAULT_COLOR),
                    textElement.style().backgroundColor().orElse(null),
                    textElement.style().outlineColor().orElse(null),
                    nullIfDefault(textElement.style().outlineWidth(), TextStyle.DEFAULT.outlineWidth()),
                    nullIfDefault(textElement.style().paddingX(), TextStyle.DEFAULT.paddingX()),
                    nullIfDefault(textElement.style().paddingY(), TextStyle.DEFAULT.paddingY()),
                    nullIfDefault(textElement.style().textAlignment(), TextStyle.DEFAULT.textAlignment()),
                    nullIfDefault(textElement.style().lineGap(), TextStyle.DEFAULT.lineGap())
                )
            );
        }
        if (element instanceof SymbolElement symbolElement) {
            return new SymbolElementJson(
                symbolElement.symbol().identifier(),
                nullIfDefault(symbolElement.alignment(), SymbolElementJson.DEFAULT_ALIGNMENT),
                new Position(symbolElement.x(), symbolElement.y())
            );
        }
        if (element instanceof PlateElement plateElement) {
            return new PlateElementJson(
                nullIfDefault(plateElement.alignment(), PlateElementJson.DEFAULT_ALIGNMENT),
                new Position(plateElement.x(), plateElement.y()),
                plateElement.front().resolve(ColorResolver.empty()).width(),
                plateElement.front().resolve(ColorResolver.empty()).height(),
                TextureSource.textureLocationOf(plateElement.front()),
                TextureSource.textureLocationOf(plateElement.back())
            );
        }
        throw new IllegalArgumentException("Unknown sign element type: " + element.getClass().getName());
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
            var pos = position != null
                ? position
                : new Position(0, 0);
            return new SymbolElement(
                pos.x,
                pos.y,
                orDefault(alignment, DEFAULT_ALIGNMENT),
                SignRegistries.SYMBOLS.get(symbol)
            );
        }
    }

    /**
     * Json format for a text element.
     *
     * @param alignment the alignment of the text
     * @param position  the local position of the text
     * @param text      the placeholder text to display
     * @param scale     the scale of the text
     * @param style     the style of the text
     */
    private record TextElementJson(
        @Nullable Alignment alignment,
        @Nullable Position position,
        String text,
        @Nullable Float scale,
        @Nullable TextStyleJson style
    ) {
        private static final Alignment DEFAULT_ALIGNMENT = Alignment.TOP_RIGHT;
        private static final String DEFAULT_TEXT = "";
        private static final float DEFAULT_SCALE = 1f;
        private static final String DEFAULT_COLOR = "foreground";

        /**
         * Converts the JSON object to a text element object
         */
        private TextElement toTextElement() {
            var pos = position != null
                ? position
                : new Position(0, 0);
            return new TextElement(
                pos.x,
                pos.y,
                orDefault(alignment, DEFAULT_ALIGNMENT),
                orDefault(text, DEFAULT_TEXT),
                orDefault(scale, DEFAULT_SCALE),
                Optional.ofNullable(style)
                    .map(TextStyleJson::toTextStyle)
                    .orElse(TextStyle.DEFAULT)
            );
        }

        /**
         * Json format for a text style.
         *
         * @param color           the color of the text
         * @param backgroundColor the background color of the text
         * @param outlineColor    the outline color of the text
         * @param outlineWidth    the width of the outline
         * @param paddingX        the horizontal padding of the text
         * @param paddingY        the vertical padding of the text
         */
        private record TextStyleJson(
            @Nullable String color,
            @Nullable String backgroundColor,
            @Nullable String outlineColor,
            @Nullable Integer outlineWidth,
            @Nullable Integer paddingX,
            @Nullable Integer paddingY,
            @Nullable TextStyle.TextAlignment textAlignment,
            @Nullable Integer lineGap
        ) {
            /**
             * Converts the JSON object to a text style object
             */
            private TextStyle toTextStyle() {
                return new TextStyle(
                    orDefault(color, TextStyle.DEFAULT.color()),
                    orDefault(backgroundColor, TextStyle.DEFAULT.backgroundColor().orElse(null)),
                    orDefault(outlineColor, TextStyle.DEFAULT.outlineColor().orElse(null)),
                    orDefault(outlineWidth, TextStyle.DEFAULT.outlineWidth()),
                    orDefault(paddingX, TextStyle.DEFAULT.paddingX()),
                    orDefault(paddingY, TextStyle.DEFAULT.paddingY()),
                    orDefault(textAlignment, TextStyle.DEFAULT.textAlignment()),
                    orDefault(lineGap, TextStyle.DEFAULT.lineGap())
                );
            }
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
            var pos = position != null
                ? position
                : new Position(0, 0);
            return new PlateElement(
                pos.x,
                pos.y,
                orDefault(alignment, DEFAULT_ALIGNMENT),
                TextureSource.parse(front, width, height),
                TextureSource.parse(back, width, height)
            );
        }
    }

    /**
     * Returns the value if it is not null, otherwise returns the default value.
     *
     * @param value        the value to check for null
     * @param defaultValue the default value to return if the value is null
     * @param <T>          the type of the value
     * @return the value if it is not null, otherwise the default value
     */
    public static <T> T orDefault(@Nullable T value, T defaultValue) {
        return value != null
            ? value
            : defaultValue;
    }

    /**
     * Returns null if the value is equal to the default value, otherwise returns the value.
     *
     * @param value        the value to check for equality with the default value
     * @param defaultValue the default value to compare against
     * @param <T>          the type of the value
     * @return null if the value is equal to the default value, otherwise the value
     */
    private static <T> @Nullable T nullIfDefault(@Nullable T value, T defaultValue) {
        if (value == null) return null;
        if (value.equals(defaultValue)) return null;
        return value;
    }
}
