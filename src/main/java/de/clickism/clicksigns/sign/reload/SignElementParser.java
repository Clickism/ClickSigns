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

    private record Position(int x, int y) {}

    private record SymbolElementJson(
            ResourceLocation symbol,
            @Nullable Alignment alignment,
            @Nullable Position position
    ) {
        SymbolElement toSymbolElement() {
            var pos = position != null ? position : new Position(0, 0);
            return new SymbolElement(
                    pos.x,
                    pos.y,
                    alignment != null ? alignment : Alignment.TOP_LEFT,
                    SignRegistries.SYMBOLS.get(symbol)
            );
        }
    }

    private record TextElementJson(
            @Nullable Alignment alignment,
            @Nullable Position position,
            String text,
            @Nullable Float scale,
            String color,
            @Nullable String backgroundColor
    ) {
        TextElement toTextElement() {
            var pos = position != null ? position : new Position(0, 0);
            return new TextElement(
                    pos.x,
                    pos.y,
                    alignment != null ? alignment : Alignment.TOP_LEFT,
                    text,
                    scale != null ? scale : 1f,
                    color,
                    backgroundColor
            );
        }
    }
}
