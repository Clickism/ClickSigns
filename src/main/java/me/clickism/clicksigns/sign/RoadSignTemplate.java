/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns.sign;

import me.clickism.clicksigns.util.Utils;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RoadSignTemplate {
    private final String author;
    private final RoadSignPack pack;

    private final Identifier id;
    private final String formattedName;
    private final String name;
    private final int width;
    private final int height;

    private final List<SignTextField> textFields;
    private final List<RoadSignTexture> textures;
    private final Set<Arrows> arrows;
    private final RoadSignCategory category;

    public RoadSignTemplate(
            Identifier id,
            String name,
            int width, int height,
            List<SignTextField> textFields,
            List<RoadSignTexture> textures,
            RoadSignCategory category,
            Set<Arrows> arrows,
            String author,
            RoadSignPack pack
    ) {
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
        this.textFields = textFields;
        this.textures = textures;
        this.category = category;
        this.arrows = arrows;
        this.author = author;
        this.pack = pack;
        this.formattedName = formatName();
    }

    public Vec2f toGlobalPos(Vec2f localPos) {
        return new Vec2f(localPos.x / 16 - (float) width / 2, localPos.y / 16 - (float) height / 2);
    }

    public String getName() {
        return name;
    }

    public RoadSignCategory getCategory() {
        return category;
    }

    public String getFormattedName() {
        return formattedName;
    }

    public Identifier getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<SignTextField> getTextFields() {
        return textFields;
    }

    public List<RoadSignTexture> getTextures() {
        return textures;
    }

    public String getAuthor() {
        return author;
    }

    public RoadSignPack getPack() {
        return pack;
    }

    public Set<Arrows> getArrows() {
        return arrows;
    }

    @NotNull
    public RoadSignTexture getFirstTextureOrError() {
        return getTextureOrError(0);
    }

    @NotNull
    public RoadSignTexture getTextureOrError(int textureIndex) {
        if (textureIndex < textures.size()) {
            return textures.get(textureIndex);
        }
        return RoadSignTemplateRegistration.ERROR_TEXTURE;
    }

    private String formatName() {
        String suffix = category == RoadSignCategory.TEMPLATE
                ? "Template"
                : formatArrows(arrows);
        return width + "x" + height + " : " + Utils.capitalize(pack.name()) + " : " + Utils.capitalize(name)
                + " (" + suffix + ")";
    }

    public static String formatArrows(Collection<Arrows> arrows) {
        return arrows.stream()
                .map(Arrows::name)
                .map(Utils::titleCase)
                .collect(Collectors.joining(", "));
    }
}
