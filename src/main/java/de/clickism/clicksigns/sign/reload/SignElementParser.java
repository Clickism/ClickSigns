package de.clickism.clicksigns.sign.reload;

import com.google.gson.JsonObject;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.util.JsonHandler;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Parser for sign elements from JSON objects.
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
            default -> {
                throw new IllegalArgumentException("Unknown sign element type: " + type);
            }
        }
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
        /**
         * Converts the JSON object to a symbol element object
         */
        SymbolElement toSymbolElement() {
            var pos = position != null ? position : new Position(0, 0);
            return new SymbolElement(
                    pos.x,
                    pos.y,
                    alignment != null ? alignment : Alignment.CENTER,
                    SignRegistries.SYMBOLS.get(symbol)
            );
        }
    }

    /**
     * Json format for a text element.
     *
     * @param alignment the alignment of the text
     * @param position the local position of the text
     * @param text the placeholder text to display
     * @param scale the scale of the text
     * @param color the color of the text, as a hex string or a color name
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
        /**
         * Converts the JSON object to a text element object
         */
        private TextElement toTextElement() {
            var pos = position != null ? position : new Position(0, 0);
            return new TextElement(
                    pos.x,
                    pos.y,
                    alignment != null ? alignment : Alignment.TOP_RIGHT,
                    text != null ? text : "",
                    scale != null ? scale : 1f,
                    color != null ? color : "foreground",
                    backgroundColor
            );
        }
    }
}
