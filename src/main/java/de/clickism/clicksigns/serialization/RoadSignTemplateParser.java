/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clicksigns.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.clickism.clicksigns.sign.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoadSignTemplateParser {
    private static final String TEXTURE_ID_PREFIX = "sign_templates/textures/";
    private static final String DIRECTION_KEY = "{direction}";

    public static void parseAndRegister(InputStream stream, String namespace) {
        JsonObject rootNode = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
        // Single template
        if (!rootNode.has("templateGenerator")) {
            RoadSignTemplateRegistration.registerTemplate(parse(rootNode, "", null, new Parameterizer(), namespace));
            return;
        }
        // Use template generator to generate multiple templates
        JsonObject generatorNode = rootNode.getAsJsonObject("templateGenerator");
        Parameterizer parameterizer = parseReplacements(generatorNode);
        if (!generatorNode.has("directions")) {
            // Parse only with replacements
            RoadSignTemplateRegistration.registerTemplate(parse(rootNode, "", null, parameterizer, namespace));
        }
        JsonArray directionsNode = generatorNode.getAsJsonArray("directions");
        for (JsonElement jsonElement : directionsNode) {
            JsonObject directionNode = jsonElement.getAsJsonObject();
            String direction = directionNode.get("direction").getAsString();
            Set<Arrows> arrows = parseArrows(directionNode.getAsJsonArray("arrows"));
            RoadSignTemplateRegistration.registerTemplate(parse(rootNode, direction, arrows, parameterizer, namespace));
        }
    }

    private static Parameterizer parseReplacements(JsonObject generatorNode) {
        Parameterizer parameterizer = new Parameterizer();
        if (!generatorNode.has("replace")) return parameterizer;
        generatorNode.getAsJsonObject("replace").asMap().forEach((key, replacement) -> {
            parameterizer.put(key, replacement.getAsString());
        });
        return parameterizer;
    }

    private static RoadSignTemplate parse(
            JsonObject rootNode,
            @NotNull String direction,
            @Nullable Set<Arrows> arrows,
            Parameterizer parameterizer,
            String namespace
    ) {
        int width = rootNode.get("width").getAsInt();
        int height = rootNode.get("height").getAsInt();
        String id = rootNode.get("id").getAsString().replace(DIRECTION_KEY, direction).toLowerCase();
        String name = rootNode.get("name").getAsString();
        RoadSignPack pack = parsePack(rootNode.get("pack").getAsJsonObject());
        Identifier identifier = Identifier.of(pack.id(), id);
        RoadSignCategory category = RoadSignCategory.fromString(rootNode.get("category").getAsString());
        // Use passed arrows if possible for template generation
        if (arrows == null) {
            arrows = parseArrows(rootNode.getAsJsonArray("arrows"));
        }
        String author = rootNode.get("author").getAsString();
        JsonArray variantsArray = rootNode.getAsJsonArray("variants");

        List<RoadSignTexture> textureVariants = parseVariants(variantsArray, direction, parameterizer, namespace);
        List<SignTextField> textFields = parseTextPositions(rootNode.getAsJsonArray("textPositions"));
        return new RoadSignTemplate(
                identifier, name, width, height, textFields, textureVariants, category, arrows, author, pack
        );
    }

    private static RoadSignPack parsePack(JsonObject packNode) {
        String packId = packNode.get("id").getAsString().toLowerCase();
        String packName = packNode.get("name").getAsString();
        return new RoadSignPack(packId, packName);
    }

    private static Set<Arrows> parseArrows(JsonArray arrowsNode) {
        Set<Arrows> arrows = new HashSet<>(arrowsNode.size());
        for (JsonElement arrowElement : arrowsNode) {
            arrows.add(Arrows.fromString(arrowElement.getAsString()));
        }
        return arrows;
    }

    private static List<RoadSignTexture> parseVariants(
            JsonArray variantsNode,
            String direction,
            Parameterizer parameterizer,
            String namespace
    ) {
        List<RoadSignTexture> textureVariants = new ArrayList<>();
        for (JsonElement variantElement : variantsNode) {
            JsonObject variantNode = variantElement.getAsJsonObject();
            String front = TEXTURE_ID_PREFIX + variantNode.get("front").getAsString().replace(DIRECTION_KEY, direction);
            front = parameterizer.apply(front);
            String back = TEXTURE_ID_PREFIX + variantNode.get("back").getAsString().replace(DIRECTION_KEY, direction);
            back = parameterizer.apply(back);
            JsonArray colorsNode = variantNode.getAsJsonArray("colors");
            String variantName = variantNode.get("name").getAsString();
            int[] colors = new int[colorsNode.size()];
            for (int i = 0; i < colorsNode.size(); i++) {
                colors[i] = colorsNode.get(i).getAsInt();
            }
            Identifier frontTexture = Identifier.of(namespace, front);
            Identifier backTexture = Identifier.of(namespace, back);
            RoadSignTexture roadSignTexture = new RoadSignTexture(frontTexture, backTexture, colors, variantName);
            textureVariants.add(roadSignTexture);
        }
        return textureVariants;
    }

    private static List<SignTextField> parseTextPositions(JsonArray textPositionsNode) {
        List<SignTextField> textFields = new ArrayList<>(textPositionsNode.size());
        for (JsonElement textPosition : textPositionsNode) {
            JsonObject textPositionNode = textPosition.getAsJsonObject();
            float x = textPositionNode.get("x").getAsFloat();
            float y = textPositionNode.get("y").getAsFloat();
            int maxWidth = textPositionNode.get("maxWidth").getAsInt();
            int colorIndex = textPositionNode.get("colorIndex").getAsInt();
            float scale = textPositionNode.has("scale")
                    ? textPositionNode.get("scale").getAsFloat()
                    : 1.0f;
            SignTextField.Alignment alignment = SignTextField.Alignment.valueOf(
                    textPositionNode.has("alignment")
                            ? textPositionNode.get("alignment").getAsString()
                            : "LEFT"
            );
            textFields.add(new SignTextField(new Vec2f(x, y), maxWidth, colorIndex, alignment, scale));
        }
        return textFields;
    }
}