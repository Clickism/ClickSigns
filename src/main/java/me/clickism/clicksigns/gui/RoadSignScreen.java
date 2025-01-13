package me.clickism.clicksigns.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public abstract class RoadSignScreen extends Screen {
    protected RoadSignScreen(Text title) {
        super(title);
    }

    //? if <1.20.5 {
    /*@Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(DrawContext context) {
        context.fillGradient(0, 0, this.width, this.height, -0x4FEFEFF0, -0x3FEFEFF0);
    }
    *///?}
}
