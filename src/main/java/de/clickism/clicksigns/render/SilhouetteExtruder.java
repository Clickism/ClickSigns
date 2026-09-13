package de.clickism.clicksigns.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntPredicate;

/**
 * Extrudes the alpha silhouette of a 2D texture into side-face quads, the same way
 * Minecraft's {@code ItemModelGenerator} builds "3D" side geometry for flat item
 * textures (paper, maps, etc.): a side quad is only emitted where an opaque pixel
 * borders a transparent one, instead of using the texture's full rectangular bounds.
 */
public final class SilhouetteExtruder {
    private static final int ALPHA_THRESHOLD = 1; // pixels with alpha below this count as transparent

    // Cache of computed silhouette edges per texture; walking every pixel every frame would be too slow.
    private static final Map<ResourceLocation, List<Edge>> CACHE = new ConcurrentHashMap<>();

    private SilhouetteExtruder() {}

    private enum Side {LEFT, RIGHT, TOP, BOTTOM}

    private record Edge(Side side, float u0, float v0, float u1, float v1) {}

    public static void renderSilhouetteSides(
        VertexConsumer buffer,
        PoseStack.Pose pose,
        int light,
        ResourceLocation location,
        float blockWidth,
        float blockHeight,
        float thickness,
        int color
    ) {
        List<Edge> edges = CACHE.computeIfAbsent(location, SilhouetteExtruder::computeEdges);
        if (edges.isEmpty()) return;

        float halfWidth = blockWidth / 2f;
        float halfHeight = blockHeight / 2f;

        for (Edge edge : edges) {
            float x0 = edge.u0() * blockWidth - halfWidth;
            float x1 = edge.u1() * blockWidth - halfWidth;
            float y0 = halfHeight - edge.v0() * blockHeight;
            float y1 = halfHeight - edge.v1() * blockHeight;

            float nx = 0, ny = 0;
            switch (edge.side()) {
                case LEFT -> nx = -1;
                case RIGHT -> nx = 1;
                case TOP -> ny = 1;
                case BOTTOM -> ny = -1;
            }

            vertex(buffer, pose, light, x0, y0, 0, 0, 0, nx, ny, 0, color);
            vertex(buffer, pose, light, x1, y1, 0, 1, 0, nx, ny, 0, color);
            vertex(buffer, pose, light, x1, y1, thickness, 1, 1, nx, ny, 0, color);
            vertex(buffer, pose, light, x0, y0, thickness, 0, 1, nx, ny, 0, color);
        }
    }

    private static void vertex(
        VertexConsumer buffer, PoseStack.Pose pose, int light,
        float x, float y, float z, float u, float v,
        float nx, float ny, float nz, int color
    ) {
        buffer.vertex(pose.pose(), x, y, z)
            .color(color)
            .uv(u, v)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(light)
            .normal(pose.normal(), nx, ny, nz)
            .endVertex();
    }

    /**
     * Computes the silhouette edges for a texture, handling both cases:
     * <ul>
     *   <li>Dynamically generated textures (registered in the {@link net.minecraft.client.renderer.texture.TextureManager}
     *       as a {@link DynamicTexture}), whose pixels already live in memory and are read directly.</li>
     *   <li>Ordinary resource-pack textures, which are opened and decoded from disk via the resource manager.</li>
     * </ul>
     */
    private static List<Edge> computeEdges(ResourceLocation location) {
        AbstractTexture registered = Minecraft.getInstance().getTextureManager().getTexture(location, null);
        if (registered instanceof DynamicTexture dynamicTexture) {
            NativeImage image = dynamicTexture.getPixels();
            if (image == null) {
                // Not yet uploaded/generated; nothing to extrude from
                return List.of();
            }
            // Owned by the texture manager - read from it, but do NOT close it
            return walkImage(image);
        }
        // Not a dynamic texture (or not registered yet) - fall back to reading it as a resource-pack file
        try (InputStream stream = Minecraft.getInstance().getResourceManager().open(location);
             NativeImage image = NativeImage.read(stream)) {
            return walkImage(image);
        } catch (IOException e) {
            // Texture couldn't be read (missing, etc.) - fall back to no silhouette sides
            return List.of();
        }
    }

    /**
     * Scans an already-loaded image's alpha channel and builds merged edge segments.
     */
    private static List<Edge> walkImage(NativeImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        List<Edge> edges = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            final int fx = x;
            mergeRuns(edges, height, y -> isOpaque(image, fx, y) && !isOpaque(image, fx - 1, y), Side.LEFT, fx, width, true);
            mergeRuns(edges, height, y -> isOpaque(image, fx, y) && !isOpaque(image, fx + 1, y), Side.RIGHT, fx, width, true);
        }
        for (int y = 0; y < height; y++) {
            final int fy = y;
            mergeRuns(edges, width, x -> isOpaque(image, x, fy) && !isOpaque(image, x, fy - 1), Side.TOP, fy, height, false);
            mergeRuns(edges, width, x -> isOpaque(image, x, fy) && !isOpaque(image, x, fy + 1), Side.BOTTOM, fy, height, false);
        }

        return edges;
    }

    private static void mergeRuns(List<Edge> edges, int runAxisLength, IntPredicate needsEdge, Side side, int fixed, int fixedAxisLength, boolean vertical) {
        int runStart = -1;
        for (int i = 0; i <= runAxisLength; i++) {
            boolean needs = i < runAxisLength && needsEdge.test(i);
            if (needs && runStart == -1) {
                runStart = i;
            } else if (!needs && runStart != -1) {
                edges.add(makeEdge(side, fixed, runStart, i, fixedAxisLength, runAxisLength, vertical));
                runStart = -1;
            }
        }
    }

    private static Edge makeEdge(Side side, int fixed, int start, int end, int fixedAxisLength, int runAxisLength, boolean vertical) {
        float runStart = (float) start / runAxisLength;
        float runEnd = (float) end / runAxisLength;
        if (vertical) {
            float u = side == Side.LEFT ? (float) fixed / fixedAxisLength : (float) (fixed + 1) / fixedAxisLength;
            return new Edge(side, u, runStart, u, runEnd);
        } else {
            float v = side == Side.TOP ? (float) fixed / fixedAxisLength : (float) (fixed + 1) / fixedAxisLength;
            return new Edge(side, runStart, v, runEnd, v);
        }
    }

    private static boolean isOpaque(NativeImage image, int x, int y) {
        if (x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) return false;
        return FastColor.ARGB32.alpha(image.getPixelRGBA(x, y)) >= ALPHA_THRESHOLD;
    }
}