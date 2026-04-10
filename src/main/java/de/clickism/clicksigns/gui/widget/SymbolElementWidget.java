package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.element.RoadSignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.util.texture.Texture;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.util.List;

import static de.clickism.clicksigns.gui.GuiUtils.OUTLINE_COLOR;

/**
 * Widget for a symbol element of a road sign
 */
public class SymbolElementWidget extends TextureWidget implements ElementProvider {
    private SymbolElement symbol;

    /**
     * Creates a new symbol widget.
     */
    public SymbolElementWidget(int anchorX, int anchorY, SymbolElement symbol) {
        super(anchorX, anchorY, symbol.texture());
        this.symbol = symbol;
        this.active = true; // Make clickable
        // Calculate position
        var pos = GuiUtils.calculateElementPosition(anchorX, anchorY, symbol, this.width, this.height);
        // Need to move up by half a pixel to align for some reason???
        this.setPosition(pos.x, pos.y - TEXTURE_RENDER_SCALE / 2);
        this.setTooltip(Tooltip.create(Component.literal("§lClick §rto cycle symbol")));
    }

    @Override
    public RoadSignElement element() {
        return symbol;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if (this.isHovered) {
            guiGraphics.renderOutline(this.getX(), this.getY(), this.width, this.height, OUTLINE_COLOR);
        }
    }

    // TODO: Switch to full category registration
    private final List<Texture> textures = List.of(
            Texture.load(ClickSigns.identifier("roadsigns/symbols/arrows/left.png")),
            Texture.load(ClickSigns.identifier("roadsigns/symbols/arrows/right.png"))
    );

    @Override
    public void onClick(double mouseX, double mouseY) {
        int currentIndex = textures.indexOf(symbol.texture());
        int nextIndex = (currentIndex + 1) % textures.size();
        this.symbol = this.symbol.withTexture(textures.get(nextIndex));
        this.texture(this.symbol.texture());
    }
}
