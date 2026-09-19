package de.clickism.clicksigns.sign.texture.source;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.serialization.codec.CommonCodec;
import de.clickism.clicksigns.serialization.codec.PacketCodec;
import de.clickism.clicksigns.serialization.codec.TagCodec;
import de.clickism.clicksigns.sign.color.ColorResolver;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clicksigns.ui.UiUtil;
import de.clickism.clicksigns.util.PixelSized;
import de.clickism.clickui.UiColor;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

/**
 * Represents a source for a texture, consisting of a base resource location and a list of texture processors to apply.
 *
 * @param base       the base resource location for the texture source
 * @param processors the list of texture processors to apply to the base image
 */
public record TextureSource(
    ResourceLocation base,
    List<TextureProcessor> processors
) {
    /**
     * The error texture to use when loading or generating a texture fails.
     */
    public static final Texture ERROR_TEXTURE = new Texture(ClickSigns.identifier("error.png"), 16, 16);
    public static final Image ERROR_IMAGE = new Image(16, 16);

    static {
        ERROR_IMAGE.fill(0xFFFF00FF); // Fill with magenta color to indicate error
    }

    private static final Map<String, Texture> TEXTURE_CACHE = new HashMap<>();
    private static final Map<String, Image> IMAGE_CACHE = new HashMap<>();
    private static final Map<String, ResourceLocation> RESOURCE_LOCATIONS = new HashMap<>();
    private static final HashSet<String> ERROR_CACHE = new HashSet<>();

    private static final Map<ResourceLocation, ColorResolver> DYNAMIC_COLOR_RESOLVERS = new HashMap<>();

    public static final int MAX_PROCESSOR_COUNT = 20;

    public TextureSource {
        if (processors.size() > MAX_PROCESSOR_COUNT) {
            throw new IllegalArgumentException(
                "Too many texture processors: " + processors.size() + " (max " + MAX_PROCESSOR_COUNT + ")"
            );
        }
        processors = List.copyOf(processors);
    }

    /**
     * Returns a unique resource location for the given key, generating a new one if it doesn't exist.
     *
     * @param key the key to get or assign a resource location for
     * @return the resource location associated with the key
     */
    private static ResourceLocation getOrAssignResourceLocation(String key) {
        var prefix = "generated/";
        var uuid = UUID.randomUUID();
        return RESOURCE_LOCATIONS.computeIfAbsent(key, k -> ClickSigns.identifier(prefix + uuid));
    }

    /**
     * Creates a new texture source with the given base resource location and no processors.
     *
     * @param base the base resource location for the texture source
     * @return a new texture source with the given base and no processors
     */
    public static TextureSource ofStatic(ResourceLocation base) {
        return new TextureSource(base, List.of());
    }

    /**
     * Creates a new texture source with the given base resource location and a list of processors.
     *
     * @param base       the base resource location for the texture source
     * @param processors the list of processors to apply to the base image
     * @return a new texture source with the given base and processors
     */
    public static TextureSource of(ResourceLocation base, List<TextureProcessor> processors) {
        return new TextureSource(base, processors);
    }

    public static CommonCodec<TextureSource> codec() {
        return CommonCodec.of(
            TagCodec.of(
                (writer, value) -> {
                    writer.putResourceLocation("base", value.base());
                    writer.putCollection("processors", value.processors, TextureProcessor.codec()::writeTag);
                },
                reader -> new TextureSource(
                    reader.getResourceLocation("base").orElseThrow(),
                    reader.getCollection("processors", TextureProcessor.codec()::readTag).orElseThrow().stream()
                        .toList()
                )
            ),
            PacketCodec.of(
                (buf, value) -> {
                    buf.writeResourceLocation(value.base());
                    buf.writeCollection(value.processors(), TextureProcessor.codec()::writePacket);
                },
                buf -> {
                    return new TextureSource(
                        buf.readResourceLocation(),
                        buf.readList(TextureProcessor.codec()::readPacket)
                    );
                }
            )
        );
    }

    /**
     * Returns the color resolver associated with the base resource location of this texture source.
     *
     * @return the color resolver for the base resource location, or a default resolver if none is registered
     */
    public ColorResolver colorResolver() {
        // Check if registered
        if (SignRegistries.TILE_SET_COLOR_RESOLVERS.hasResolver(base)) {
            return SignRegistries.TILE_SET_COLOR_RESOLVERS.getOrDefault(base);
        }
        if (SignRegistries.STATIC_TEXTURE_COLOR_RESOLVERS.hasResolver(base)) {
            return SignRegistries.STATIC_TEXTURE_COLOR_RESOLVERS.getOrDefault(base);
        }
        // Check if a dynamic resolver is available
        if (DYNAMIC_COLOR_RESOLVERS.containsKey(base)) {
            return DYNAMIC_COLOR_RESOLVERS.get(base);
        }
        // Create with default colors based on primary color
        var defaultResolver = ColorResolver.withDefault();
        try {
            var primaryColor = UiUtil.primaryColorOf(baseImage());
            var foregroundColor = primaryColor.pickBetterContrasting(
                UiColor.rgba(defaultResolver.resolve("black")),
                UiColor.rgba(defaultResolver.resolve("white"))
            );
            var colorResolver = ColorResolver.withDefault()
                .define("foreground", foregroundColor.color());
            DYNAMIC_COLOR_RESOLVERS.put(base, colorResolver);
            return colorResolver;
        } catch (Exception e) {
            return defaultResolver;
        }
    }

    /**
     * Resolves the texture source into a texture by loading the base image and applying all processors in order.
     *
     * @param colorResolver the color resolver to use for processing
     * @return the resolved texture, or the error texture if loading or processing fails
     */
    public @NotNull Texture resolve(ColorResolver colorResolver) {
        var context = new TextureContext(colorResolver);
        var identity = identity(context);
        // Check cache
        if (TEXTURE_CACHE.containsKey(identity)) {
            return TEXTURE_CACHE.get(identity);
        }
        // Generate texture
        Image image;
        try {
            image = generateAndCache(context);
        } catch (Exception e) {
            // Only send error message once per unique texture source, to avoid spamming the log
            sendErrorOnce(identity, "Failed to resolve texture source " + identity, e);
            return ERROR_TEXTURE;
        }
        // Upload texture to Minecraft and cache it
        var location = getOrAssignResourceLocation(identity);
        MinecraftImages.upload(location, image);
        var texture = new Texture(location, image.width(), image.height());
        TEXTURE_CACHE.put(identity, texture);
        return texture;
    }

    /**
     * Resolves the texture source into an image by loading the base image and applying all processors in order.
     *
     * @param colorResolver the color resolver to use for processing
     * @return the resolved image, or the error image if loading or processing fails
     */
    public @NotNull Image resolveImage(ColorResolver colorResolver) {
        var context = new TextureContext(colorResolver);
        // Generate image
        try {
            return generateAndCache(context);
        } catch (Exception e) {
            // Only send error message once per unique texture source, to avoid spamming the log
            var identity = identity(context);
            sendErrorOnce(identity, "Failed to resolve image for texture source " + identity, e);
            return ERROR_IMAGE;
        }
    }

    private Image generateAndCache(TextureContext context) throws Exception {
        var identity = identity(context);
        if (IMAGE_CACHE.containsKey(identity)) {
            return IMAGE_CACHE.get(identity);
        }
        // Generate image
        var image = generate(context);
        IMAGE_CACHE.put(identity, image);
        return image;
    }

    /**
     * Resolves the texture source into an image by loading the base image and applying all processors in order.
     *
     * @param context the texture context containing additional information for processing
     * @return the resolved image, or null if loading or processing fails
     */
    private Image generate(TextureContext context) throws Exception {
        var image = baseImage();
        for (var processor : processors) {
            try {
                image = processor.process(image, context);
                if (image == null) {
                    throw new RuntimeException("Processor " + processor.identity(context) + " returned null image");
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to process image with processor " + processor.identity(context), e);
            }
        }
        return image;
    }

    /**
     * Loads the base image from the resource location.
     *
     * @return the loaded base image
     * @throws IOException if the base image cannot be loaded
     */
    private Image baseImage() throws Exception {
        var image = MinecraftImages.open(base);
        if (image == null) {
            throw new IOException("Failed to open base image at location " + base);
        }
        return image;
    }

    private void sendErrorOnce(String identity, String message, Exception e) {
        if (ERROR_CACHE.contains(identity)) {
            return;
        }
        ERROR_CACHE.add(identity);
        ClickSigns.LOGGER.error("{}: {}", message, e.getMessage());
    }

    /**
     * Returns a unique identity string for this texture source and its parameters, used for caching.
     *
     * @param context the texture context containing additional information for processing
     * @return a unique identity string for this texture source
     */
    private String identity(TextureContext context) {
        var sb = new StringBuilder();
        sb.append(base.toString());
        for (var processor : processors) {
            sb.append("+").append(processor.identity(context));
        }
        return sb.toString();
    }

    /**
     * A texture source is resizable if it contains exactly one resizable processor, and
     * possibly other non-resizable processors.
     * <p>
     * This limitation is due to the fact that resizable processors can change the size of the image, and
     * having multiple resizable processors would make it ambiguous which one should determine the final size.
     *
     * @return checks if the texture source is resizable
     */
    public boolean isResizable() {
        var count = processors.stream()
            .filter(p -> p instanceof ResizableTextureProcessor)
            .count();
        return count == 1;
    }

    /**
     * Returns a new texture source with the given width and height, if this texture source is resizable.
     * <p>
     * If this texture source is not resizable, returns the same texture source without any changes.
     *
     * @param width  the desired width of the new texture source
     * @param height the desired height of the new texture source
     * @return a new texture source with the given width and height, or the same texture source if not resizable
     */
    public TextureSource resize(int width, int height) {
        if (!isResizable()) {
            return this;
        }
        var newProcessors = new ArrayList<TextureProcessor>();
        for (var processor : processors) {
            if (processor instanceof ResizableTextureProcessor resizable) {
                newProcessors.add(resizable.resize(width, height));
            } else {
                newProcessors.add(processor);
            }
        }
        return new TextureSource(base, newProcessors);
    }

    /**
     * Returns a new texture source with the given pixel size, if this texture source is resizable.
     * <p>
     * If this texture source is not resizable, returns the same texture source without any changes.
     *
     * @param pixelSized the desired pixel size of the new texture source
     * @return a new texture source with the given pixel size, or the same texture source if not resizable
     */
    public TextureSource resize(PixelSized pixelSized) {
        return resize(pixelSized.width(), pixelSized.height());
    }

    /**
     * Returns a new texture source with the given list of processors, replacing the existing processors.
     *
     * @param newProcessors the new list of processors to apply to the base image
     * @return a new texture source with the given processors
     */
    public TextureSource withProcessors(List<TextureProcessor> newProcessors) {
        return new TextureSource(base, newProcessors);
    }

    /**
     * Returns a new texture source with the given processor added to the end of the existing processors.
     *
     * @param processor the processor to add to the texture source
     * @return a new texture source with the given processor added
     */
    public TextureSource addProcessor(TextureProcessor processor) {
        var newProcessors = new ArrayList<>(processors);
        newProcessors.add(processor);
        return new TextureSource(base, newProcessors);
    }
}
