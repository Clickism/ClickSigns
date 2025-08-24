/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.ClickSigns;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Environment(EnvType.CLIENT)
public class RoadSignTemplateRegistration {

    private static final Identifier DEFAULT_TEMPLATE_ID = ClickSigns.identifier("2x1_street_left");

    public static final RoadSignTexture ERROR_TEXTURE = new RoadSignTexture(
            ClickSigns.identifier("sign_templates/textures/error.png"),
            ClickSigns.identifier("sign_templates/textures/error.png"),
            new int[]{0}, "Error"
    );

    public static final RoadSignTemplate ERROR = new RoadSignTemplate(
            ClickSigns.ERROR_TEMPLATE_ID, "Error", 2, 1,
            List.of(), List.of(ERROR_TEXTURE), RoadSignCategory.CUSTOM,
            Set.of(), "Clickism", new RoadSignPack("clicksigns", "ClickSigns")
    );

    private static final Map<Identifier, RoadSignTemplate> templates = new LinkedHashMap<>();
    private static final Map<RoadSignCategory, Set<RoadSignTemplate>> categoryMap = new HashMap<>();
    private static final Set<RoadSignPack> packs = new HashSet<>();

    public static Collection<RoadSignTemplate> getTemplates() {
        return templates.values();
    }

    public static Collection<RoadSignTemplate> getTemplates(RoadSignCategory category) {
        return categoryMap.getOrDefault(category, new LinkedHashSet<>());
    }

    public static Set<RoadSignPack> getPacks() {
        return packs;
    }

    public static RoadSignTemplate getDefaultTemplate() {
        RoadSignTemplate template = templates.get(DEFAULT_TEMPLATE_ID);
        if (template != null) {
            return template;
        }
        if (templates.isEmpty()) return ERROR;
        return templates.values().iterator().next();
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
        RoadSignCategory category = template.getCategory();
        categoryMap.computeIfAbsent(category, k -> new LinkedHashSet<>())
                .add(template);
        packs.add(template.getPack());
    }

    public static void clearTemplates() {
        templates.clear();
        categoryMap.clear();
        packs.clear();
    }
}
