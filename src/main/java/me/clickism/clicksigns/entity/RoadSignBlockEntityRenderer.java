/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import me.clickism.clicksigns.sign.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;

@Environment(EnvType.CLIENT)
public class RoadSignBlockEntityRenderer implements BlockEntityRenderer<RoadSignBlockEntity> {
    private static final float Z_FIGHTING_OFFSET = 0.001f;

    private final TextRenderer textRenderer;

    public RoadSignBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.textRenderer = context.getTextRenderer();
    }

    @Override
    public void render(RoadSignBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay
                       //? if >=1.21.5
                       ,Vec3d cameraPos
                       ) {
        RoadSign roadSign = entity.getRoadSign();
        if (roadSign == null) {
            RoadSignTemplate first = RoadSignTemplateRegistration.getDefaultTemplate();
            roadSign = new RoadSign(first.getId(), 0, List.of(), RoadSign.Alignment.TOP_CENTER);
        }
        renderSignFace(entity, matrices, vertexConsumers, light, overlay, roadSign, false);
        renderSignFace(entity, matrices, vertexConsumers, light, overlay, roadSign, true);
    }

    private void renderSignFace(RoadSignBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                int light, int overlay, RoadSign roadSign, boolean isBack) {
        matrices.push();
        Identifier texture;
        RoadSignTemplate template = RoadSignTemplateRegistration.getTemplateOrError(roadSign.templateId());
        RoadSignTexture roadSignTexture = template.getTextureOrError(roadSign.getTextureIndex());

        Direction direction = entity.getCachedState().get(Properties.HORIZONTAL_FACING);
        matrices.translate(0.5, 0.5, 0.5);
        //? if >1.21.2 {
        matrices.multiply(new Quaternionf().rotateY((float) Math.toRadians(-direction.getPositiveHorizontalDegrees())));
        //?} else
        /*matrices.multiply(new Quaternionf().rotateY((float) Math.toRadians(-direction.asRotation())));*/
        matrices.translate(0, 0, .5f - Z_FIGHTING_OFFSET);
        float halfWidth = (float) template.getWidth() / 2 - .5f;
        float halfHeight = (float) template.getHeight() / 2 - .5f;
        RoadSign.Alignment alignment = roadSign.getAlignment();
        matrices.translate(-alignment.getXOffset() * halfWidth, alignment.getYOffset() * halfHeight, 0);
        if (isBack) {
            texture = roadSignTexture.getBackTexture();
        } else {
            texture = roadSignTexture.getFrontTexture();
            matrices.scale(-1.0f, 1.0f, -1.0f); // Flip for front face
        }
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f positionMatrix = entry.getPositionMatrix();
        Matrix3f normalMatrix = entry.getNormalMatrix();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(texture));

        float width = template.getWidth();
        float height = template.getHeight();
        float x1 = -width / 2;
        float y1 = -height / 2;
        float x2 = width / 2;
        float y2 = height / 2;

        boolean xAxis = direction.getAxis() == Direction.Axis.X;
        // Bottom-left
        vertex(vertexConsumer, positionMatrix, normalMatrix, x1, y1, 0.0f, 0f, 1f, light, overlay, xAxis);
        // Bottom-right
        vertex(vertexConsumer, positionMatrix, normalMatrix, x2, y1, 0.0f, 1f, 1f, light, overlay, xAxis);
        // Top-right
        vertex(vertexConsumer, positionMatrix, normalMatrix, x2, y2, 0.0f, 1f, 0f, light, overlay, xAxis);
        // Top-left
        vertex(vertexConsumer, positionMatrix, normalMatrix, x1, y2, 0.0f, 0f, 0f, light, overlay, xAxis);

        if (!isBack) {
            List<SignTextField> textFields = template.getTextFields();
            List<String> texts = roadSign.getTexts();
            int commonSize = Math.min(texts.size(), textFields.size());
            for (int i = 0; i < commonSize; i++) {
                renderText(matrices, vertexConsumers, light, template, roadSignTexture, textFields.get(i), texts.get(i));
            }
        }
        matrices.pop();
    }

    private void vertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix,
                        float x, float y, float z, float u, float v, int light, int overlay, boolean xAxis) {
        vertexConsumer.vertex(positionMatrix, x, y, z)
                .color(255, 255, 255, 255)
                .texture(u, v)
                .overlay(overlay)
                .light(light)
                .normal(
                        //? if <1.20.5 {
                        /*normalMatrix, xAxis ? 1f : 0f, 0f, xAxis ? 0f : 1f)
                        *///?} else
                        0f, 0f, 1f)
                //? if <1.20.5
                /*.next()*/
        ;
    }

    public static final float TEXT_RENDER_SCALE = .022f;

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                            RoadSignTemplate template, RoadSignTexture texture, SignTextField textField, String text) {
        matrices.push();
        Vec2f pos = template.toGlobalPos(textField.renderPos);
        matrices.translate(pos.x, pos.y, Z_FIGHTING_OFFSET);
        float scale = TEXT_RENDER_SCALE * textField.scale;
        matrices.scale(scale, -scale, scale);

        Matrix4f textMatrix = matrices.peek().getPositionMatrix();
        float textWidth = textRenderer.getWidth(text);
        float alignedX = textField.getAlignedX(textWidth);
        int color = textField.getColor(texture);
        textRenderer.draw(text, alignedX, -textRenderer.fontHeight, color, false, textMatrix,
                vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
        matrices.pop();
    }
}