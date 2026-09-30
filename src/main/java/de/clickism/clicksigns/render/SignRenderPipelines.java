package de.clickism.clicksigns.render;


import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import de.clickism.clicksigns.ClickSigns;

//? if >=26.1 {
import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
//?}

public class SignRenderPipelines {

    private static final Identifier WHITE = ClickSigns.identifier("textures/misc/white.png");

    //? if >=26.1 {
    public static final RenderPipeline SIGN_BASE = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(ClickSigns.identifier("pipeline/sign_base"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withSampler("Sampler1")
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(true)
            .withDepthStencilState(new DepthStencilState(
                CompareOp.LESS_THAN_OR_EQUAL, true, -1.0f, -2.0f))
            .build()
    );

    public static final RenderPipeline SIGN_ELEMENT = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(ClickSigns.identifier("pipeline/sign_element"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withSampler("Sampler1")
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(true)
            // Test against the base plane, but never write depth:
            // layers can't punch holes in each other, order decides who is on top
            .withDepthStencilState(new DepthStencilState(
                CompareOp.LESS_THAN_OR_EQUAL, false, -2.0f, -6.0f))
            .build()
    );


    private static final Map<Identifier, RenderType> BASE_TYPES = new HashMap<>();
    private static final Map<Identifier, RenderType> ELEMENT_TYPES = new HashMap<>();

    private static RenderType create(RenderPipeline pipeline, Identifier texture) {
        return RenderType.create(
            "clicksigns_sign_layer",
            RenderSetup.builder(pipeline)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .sortOnUpload()
                .setOutline(RenderSetup.OutlineProperty.NONE)
                .createRenderSetup()
        );
    }
    //?}

    public static RenderType baseRenderType(Identifier texture) {
        //? if >=26.1 {
        return BASE_TYPES.computeIfAbsent(texture, id -> create(SIGN_BASE, id));
        //?} else
        //return RenderType.entityTranslucentCull(texture);
    }

    public static RenderType elementRenderType(Identifier texture) {
        //? if >=26.1 {
        return ELEMENT_TYPES.computeIfAbsent(texture, id -> create(SIGN_ELEMENT, id));
        //?} else
        //return RenderType.entityTranslucentCull(texture);
    }

    public static RenderType colorRenderType() {
        //? if >=26.1 {
        return ELEMENT_TYPES.computeIfAbsent(WHITE, id -> create(SIGN_ELEMENT, id));
        //?} else
        //return RenderType.entityTranslucentCull(WHITE);
    }
}
