package me.clickism.clicksigns.gui.widget;

import me.clickism.clicksigns.gui.RoadSignTemplateSelectionScreen;
import me.clickism.clicksigns.sign.Arrows;
import me.clickism.clicksigns.sign.RoadSignTemplate;
import me.clickism.clicksigns.sign.RoadSignTemplateRegistration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
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
        super(client, width, height, y, itemHeight);
        this.parent = parent;
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
    public int getScrollbarX() {
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

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    public static class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {
        public final RoadSignTemplate template;

        public Entry(RoadSignTemplate template) {
            super();
            this.template = template;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            context.drawTextWithShadow(textRenderer, template.getFormattedName(), x + 5, y + 5, 0xFFFFFF);
        }

        @Override
        public Text getNarration() {
            return Text.literal("");
        }
    }
}
