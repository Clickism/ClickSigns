package de.clickism.clicksigns.render;

import de.clickism.clicksigns.ClickSigns;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

//? if >=26.1
import net.minecraft.client.renderer.rendertype.RenderTypes;

/**
 * A utility class for managing render pipelines for sign rendering.
 */
public class SignRenderPipelines {

    private static final Identifier WHITE = ClickSigns.identifier("textures/misc/white.png");

    /**
     * Gets the render type for rendering a base texture with the given texture identifier.
     *
     * @param texture the texture identifier
     * @return the render type for rendering the base texture
     */
    public static RenderType baseRenderType(Identifier texture) {
        //? if >=26.3 {
        return RenderTypes.entityTranslucentCull(texture);
        //?} elif >=26.1 {
        //return RenderTypes.entityTranslucentCullItemTarget(texture);
        //?} else
        //return RenderType.entityTranslucentCull(texture);
    }

    /**
     * Gets the render type for rendering an element texture with the given texture identifier.
     *
     * @param texture the texture identifier
     * @return the render type for rendering the element texture
     */
    public static RenderType elementRenderType(Identifier texture) {
        return baseRenderType(texture);
    }

    /**
     * Gets the render type for rendering a solid color.
     *
     * @return the render type for rendering a solid color
     */
    public static RenderType colorRenderType() {
        return baseRenderType(WHITE);
    }
}
