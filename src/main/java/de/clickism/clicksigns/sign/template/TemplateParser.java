package de.clickism.clicksigns.sign.template;

import com.google.gson.JsonObject;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.util.JsonHandler;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Template parsing logic.
 */
public class TemplateParser implements JsonHandler {
    private static final SignElementParser ELEMENT_PARSER = new SignElementParser();

    /**
     * Parses the given JSON into a template object.
     *
     * @param json       the JSON object to parse
     * @param location   the resource location of the template
     * @param categoryId the resource location of the category the template belongs to
     * @return the parsed template object
     */
    public Template parse(
            JsonObject json,
            ResourceLocation location,
            ResourceLocation categoryId
    ) {
        var templateJson = fromJsonOrThrow(json, TemplateJson.class);
        return templateJson.parse(location, categoryId);
    }

    /**
     * Converts the given template metadata and road sign into a JSON object.
     *
     * @param meta         the metadata of the template
     * @param roadSign     the road sign to convert into JSON
     * @param includeTexts whether to include written text in the JSON output
     * @return the JSON object representing the template
     */
    public JsonObject toJson(Template.Meta meta, RoadSign roadSign, boolean includeTexts) {
        var signJson = new TemplateJson.SignJson(
                roadSign.width(),
                roadSign.height(),
                TextureSource.textureLocationOf(roadSign.frontSource()),
                TextureSource.textureLocationOf(roadSign.backSource()),
                roadSign.elements().stream()
                        .map(element -> ELEMENT_PARSER.toJson(element, includeTexts))
                        .toList()
        );
        var templateJson = new TemplateJson(meta, signJson);
        return toJsonObject(templateJson);
    }

    /**
     * Json format for templates.
     *
     * @param meta metadata for the template
     * @param sign sign data for the template
     */
    private record TemplateJson(
            Template.Meta meta,
            SignJson sign
    ) {
        /**
         * Converts the JSON into a template object
         */
        private Template parse(ResourceLocation id, ResourceLocation categoryId) {
            return new Template(
                    meta,
                    sign.parse(),
                    id,
                    categoryId
            );
        }

        /**
         * Json format for sign data in templates.
         *
         * @param width    the width of the sign in pixels
         * @param height   the height of the sign in pixels
         * @param front    the front texture source of the sign
         * @param back     the back texture source of the sign
         * @param elements the list of sign elements for the sign
         */
        private record SignJson(
                int width,
                int height,
                ResourceLocation front,
                ResourceLocation back,
                List<JsonObject> elements
        ) {
            /**
             * Converts the JSON into a sign object
             *
             * @return the parsed sign object
             */
            private Template.Sign parse() {
                var parsedElements = elements.stream()
                        .map(ELEMENT_PARSER::parse)
                        .toList();
                return new Template.Sign(
                        width,
                        height,
                        TextureSource.parse(front, width, height),
                        TextureSource.parse(back, width, height),
                        parsedElements
                );
            }
        }
    }
}
