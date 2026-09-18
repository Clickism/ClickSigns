package de.clickism.clicksigns.ui.components;

import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clicksigns.sign.texture.source.Image;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.ui.UiUtil;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.render.RenderContext;
import de.clickism.clickui.render.TooltipRenderer;
import de.clickism.clickui.style.Border;
import org.jetbrains.annotations.Nullable;

import static de.clickism.clicksigns.util.ComponentUtil.l;

/**
 * A UI component that displays an image with a color picker functionality.
 * When the user hovers over the image, it shows the color of the pixel under the cursor.
 * Clicking on the image copies the color to the clipboard.
 */
public class ImageWithPicker extends UiComponent<ImageWithPicker> implements CommonComponents {
    private final Texture texture;
    private final Image image;
    private final int size;
    private @Nullable Integer hoveredPixel = null;

    /**
     * Constructs an ImageWithPicker component.
     *
     * @param source        the texture source to display
     * @param colorResolver the color resolver to apply to the texture
     * @param size          the size of the image to display
     */
    public ImageWithPicker(TextureSource source, ColorResolver colorResolver, int size) {
        this.texture = source.resolve(colorResolver);
        this.image = source.resolveImage(colorResolver);
        this.size = size;
        this.onClick(event -> {
            if (hoveredPixel != null) {
                var colorHex = String.format("#%06x", (0xFFFFFF & hoveredPixel));
                UiUtil.copyToClipboard(colorHex);
                event.playSound();
            }
        });
    }

    @Override
    protected void build() {
        this
            .padding(2)
            .style(style()
                .borderPosition(Border.Position.INSIDE)
                .borderColor(UiColor.LIGHT_GRAY.alpha(0.2f))
                .backgroundColor(UiColor.BLACK)
                .whenHovered(style()
                    .borderColor(UiColor.LIGHT_GRAY.alpha(1f))))
            .children(
                UiUtil.imageOf(texture)
                    .grow()
                    .maxWidth(size)
                    .maxHeight(size)
                    .keepAspectRatio(true)
            );
    }

    @Override
    public void render(RenderContext context) {
        // Update color
        var imageX = context.mouseX() - bounds().x() - padding().left();
        var imageY = context.mouseY() - bounds().y() - padding().top();
        var renderedWidth = bounds().width() - padding().horizontal();
        var renderedHeight = bounds().height() - padding().vertical();
        if (renderedWidth <= 0 || renderedHeight <= 0) {
            return;
        }
        imageX = imageX * image.width() / renderedWidth;
        imageY = imageY * image.height() / renderedHeight;
        if (imageX >= 0 && imageX < image.width() && imageY >= 0 && imageY < image.height()) {
            hoveredPixel = image.pixelAt(imageX, imageY);
            // Render tooltip
            var colorHex = l(String.format("#%06x", (0xFFFFFF & hoveredPixel)));
            var colorBox = box()
                .horizontal()
                .childGap(2)
                .alignCenter()
                .children(
                    box()
                        .size(10)
                        .style(style().backgroundColor(UiColor.rgba(hoveredPixel))),
                    text(colorHex).padding(1, 0, 0, 0)
                );
            var tooltip = box()
                .childGap(2)
                .children(
                    colorBox,
                    box().height(1).growWidth()
                        .style(style().backgroundColor(UiColor.LIGHT_GRAY.alpha(0.3f))),
                    describeLeftClick(l("Copy Color"))
                );
            tooltip.invalidateLayout();
            new TooltipRenderer(tooltip, context).render();
        }
    }
}
