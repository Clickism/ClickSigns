package de.clickism.clicksigns;

import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.sign.reload.StaticTextureListener;
import de.clickism.clicksigns.sign.reload.SymbolListener;
import de.clickism.clicksigns.sign.reload.TemplateListener;
import de.clickism.clicksigns.sign.reload.TileSetListener;
import de.clickism.clicksigns.sign.template.LocalTemplateManager;

/**
 * Client mod class.
 */
public class ClickSignsClient {
    /**
     * Manager for local templates stored in .minecraft/sign_templates
     */
    public static final LocalTemplateManager LOCAL_TEMPLATE_MANAGER = new LocalTemplateManager();

    /**
     * Initializes the client mod.
     */
    public static void initialize() {
        // Local template manager
        LOCAL_TEMPLATE_MANAGER.initialize();
        // Add reload listeners
        Platform.get().addReloadListener(new TileSetListener());
        Platform.get().addReloadListener(new StaticTextureListener());
        Platform.get().addReloadListener(new SymbolListener());
        Platform.get().addReloadListener(new TemplateListener());
    }
}
