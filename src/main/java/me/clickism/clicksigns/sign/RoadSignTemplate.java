package me.clickism.clicksigns.sign;

import me.clickism.clicksigns.Utils;
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
    private final RoadSignTemplateCategory category;

    public RoadSignTemplate(
            Identifier id,
            String name,
            int width, int height,
            List<SignTextField> textFields,
            List<RoadSignTexture> textures,
            RoadSignTemplateCategory category,
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
        this.formattedName = formatName(width, height, pack, name, arrows);
    }

    public Vec2f toGlobalPos(Vec2f localPos) {
        return new Vec2f(localPos.x / 16 - (float) width / 2, localPos.y / 16 - (float) height / 2);
    }

    public String getName() {
        return name;
    }

    public RoadSignTemplateCategory getCategory() {
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

    private static String formatName(int width, int height, RoadSignPack pack, String name, Set<Arrows> arrows) {
        return width + "x" + height + " : " + Utils.capitalize(pack.name()) + " : " + Utils.capitalize(name)
                + " (" + formatArrows(arrows) + ")";
    }

    public static String formatArrows(Collection<Arrows> arrows) {
        return arrows.stream()
                .map(Arrows::name)
                .map(Utils::titleCase)
                .collect(Collectors.joining(", "));
    }
}
