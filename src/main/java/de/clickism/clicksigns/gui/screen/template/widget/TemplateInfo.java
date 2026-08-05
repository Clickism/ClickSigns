package de.clickism.clicksigns.gui.screen.template.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.edit.widget.PanelWidget;
import de.clickism.clicksigns.gui.util.LinearLayout;
import de.clickism.clicksigns.gui.util.NestedWidget;
import de.clickism.clicksigns.sign.template.Template;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class TemplateInfo extends NestedWidget {
    private final int maxWidth;

    public TemplateInfo(int x, int y, int maxWidth) {
        super(x, y);
        this.maxWidth = maxWidth;
    }

    public void template(@Nullable Template template) {
        this.clearChildren();
        var x = this.getX();
        var y = this.getY();
        // If no template is selected, show a message
        if (template == null) {
            var widget = new StringWidget(x, y, 0, 0, Component.translatable("clicksigns.template.info.none"), GuiUtils.font());
            widget.setColor(new Color(255, 255, 255, 100).getRGB());
            widget.alignLeft();
            addChildAndUpdate(widget);
            return;
        }
        // Template info
        var meta = template.meta();
        var layout = LinearLayout.vertical()
                .padding(4);
        var name = new FieldWidget(x, y, Component.translatable("clicksigns.template.info.name"), Component.literal(meta.name()), maxWidth);
        layout.add(name);
        addChild(name);

        if (meta.description() != null && !meta.description().isEmpty()) {
            var description = new FieldWidget(x, y, Component.translatable("clicksigns.template.info.description"), Component.literal(meta.description()), maxWidth);
            layout.add(description);
            addChild(description);
        }

        if (meta.author() != null && !meta.author().isEmpty()) {
            var author = new FieldWidget(x, y, Component.translatable("clicksigns.template.info.author"), Component.literal(meta.author()), maxWidth);
            layout.add(author);
            addChild(author);
        }

        layout.layout(x, y);

        updateSizeAndPosition();
    }
}
