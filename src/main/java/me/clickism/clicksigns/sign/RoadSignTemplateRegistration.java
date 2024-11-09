package me.clickism.clicksigns.sign;

import me.clickism.clicksigns.ClickSigns;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Environment(EnvType.CLIENT)
public class RoadSignTemplateRegistration {

    public static final RoadSignTexture ERROR_TEXTURE = new RoadSignTexture(
            ClickSigns.identifier("sign_templates/textures/error.png"),
            ClickSigns.identifier("sign_templates/textures/error.png"),
            new int[]{0}, "Error"
    );

    public static final RoadSignTemplate ERROR = new RoadSignTemplate(
            ClickSigns.ERROR_TEMPLATE_ID, "Error", 2, 1,
            List.of(), List.of(ERROR_TEXTURE), RoadSignTemplateCategory.CUSTOM,
            Set.of(), "Clickism", new RoadSignPack("clicksigns", "ClickSigns")
    );

    private static final SequencedMap<Identifier, RoadSignTemplate> templates = new LinkedHashMap<>();
    private static final Set<RoadSignPack> packs = new HashSet<>();

    public static SequencedCollection<RoadSignTemplate> getTemplates() {
        return templates.sequencedValues();
    }

    public static Set<RoadSignPack> getPacks() {
        return packs;
    }

    public static RoadSignTemplate getDefaultTemplate() {
        if (getTemplates().isEmpty()) return ERROR;
        return getTemplates().getFirst();
    }

    @NotNull
    public static RoadSignTemplate getTemplateOrError(Identifier id) {
        RoadSignTemplate template = templates.get(id);
        if (template != null) {
            return template;
        }
        return ERROR;
    }

    public static void registerTemplate(RoadSignTemplate template) {
        templates.put(template.getId(), template);
        packs.add(template.getPack());
    }

    public static void clearTemplates() {
        templates.clear();
        packs.clear();
    }
}
