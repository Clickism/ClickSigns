package de.clickism.clicksigns.sign.template;

import com.google.gson.JsonObject;
import de.clickism.clicksigns.serialization.JsonTagImpl;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.codec.RoadSignCodec;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.util.JsonHandler;
import net.minecraft.resources.ResourceLocation;

/**
 * Template parsing logic.
 */
public class TemplateParser implements JsonHandler {
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
        var tag = new JsonTagImpl(new JsonObject());
        if (!includeTexts) {
            // Strip texts if not including them
            roadSign = roadSign.withElements(roadSign.elements().stream()
                .map(element -> {
                    if (element instanceof TextElement text) {
                        return text.withText("");
                    }
                    return element;
                })
                .toList());
        }
        RoadSignCodec.codec().writeTag(tag, roadSign);
        var signJson = tag.jsonObject();
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
        JsonObject sign
    ) {
        /**
         * Converts the JSON into a template object
         */
        private Template parse(ResourceLocation id, ResourceLocation categoryId) {
            var tag = new JsonTagImpl(sign);
            var parsedSign = RoadSignCodec.codec().readTag(tag);
            return new Template(
                meta,
                parsedSign,
                id,
                categoryId
            );
        }
    }
}
