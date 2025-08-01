/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns.gui.widget;

import me.clickism.clicksigns.gui.RoadSignTemplateSelectionScreen;
import me.clickism.clicksigns.sign.Arrows;
import me.clickism.clicksigns.sign.RoadSignTemplate;
import me.clickism.clicksigns.sign.RoadSignTemplateRegistration;
import me.clickism.clicksigns.util.VersionHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@Environment(EnvType.CLIENT)
public class RoadSignTemplateListWidget extends EntryListWidget<RoadSignTemplateListWidget.Entry> {
    private final RoadSignTemplateSelectionScreen parent;

    // TODO: FIX ELEMENTS MISSING??

    public RoadSignTemplateListWidget(
            MinecraftClient client,
            int width,
            int height,
            int y,
            int itemHeight,
            RoadSignTemplateSelectionScreen parent
    ) {
        super(client, width, height, y, /*? if <1.20.5 {*/ /*height,*//*?}*/ itemHeight);
        this.parent = parent;
        //? if <1.20.5 {
        /*setRenderBackground(false);
        setRenderHorizontalShadows(false);
        *///?}
    }

    public void filter(
            @Nullable Integer width,
            @Nullable Integer height,
            @Nullable Set<Arrows> arrows
    ) {
        children().clear();
        for (RoadSignTemplate template : RoadSignTemplateRegistration.getTemplates()) {
            if (width != null && template.getWidth() != width) continue;
            if (height != null && template.getHeight() != height) continue;
            if (arrows != null && !template.getArrows().containsAll(arrows)) continue;
            addEntry(new Entry(template));
        }
    }

    public void add(Entry entry) {
        addEntry(entry);
    }

    @Override
    public int /*? if >=1.20.5 {*/ getScrollbarX() /*?} else {*/ /*getScrollbarPositionX() *//*?}*/ {
        return this.width - 6;
    }

    @Override
    public int getRowWidth() {
        return this.width - 12;
    }

    @Override
    public void setSelected(@Nullable RoadSignTemplateListWidget.Entry entry) {
        super.setSelected(entry);
        parent.setSelectedTemplate(entry == null ? null : entry.template, 0);
    }

    //? if >=1.20.5 {
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
    //?} else {
    /*@Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }
    *///?}

    //? if <1.20.5 {
    /*@Override
    protected void renderBackground(DrawContext context) {
        context.fillGradient(0, this.top, this.width, this.height, -0x8FEFEFF0, -0x6FEFEFF0);
        context.fillGradient(RenderLayer.getGuiOverlay(), this.left, this.top, this.right, this.top + 4, -16777216, 0, 0);
        context.fillGradient(RenderLayer.getGuiOverlay(), this.left, this.bottom - 4, this.right, this.bottom, 0, -16777216, 0);
    }
    *///?}

    public static class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {
        public final RoadSignTemplate template;

        public Entry(RoadSignTemplate template) {
            super();
            this.template = template;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int color = VersionHelper.normalizeColor(0xFFFFFF);
            context.drawTextWithShadow(textRenderer, template.getFormattedName(), x + 5, y + 5, color);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return true;
        }

        @Override
        public Text getNarration() {
            return Text.literal("");
        }
    }
}
