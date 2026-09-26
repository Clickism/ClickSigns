package de.clickism.clicksigns;

import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;
import de.clickism.configured.json.JsonFormat;

/**
 * The configuration file for ClickSigns.
 */
public interface ClickSignsConfig {
    // Use format directly, since service providers cause problems when running in dev for legacy forge.
    Config CONFIG = Config.of("config/ClickSigns/config.jsonc", JsonFormat.jsonc())
        .version(1)
        .appendDefaults()
        .header("""
            ---------------------------------------------------------
            ClickSigns Config
            NOTE: RESTART MINECRAFT FOR CHANGES TO TAKE EFFECT
            ---------------------------------------------------------
            """);

    ConfigOption<Boolean> CHECK_UPDATES =
        CONFIG.option("check_updates", true)
            .description("""
                Whether to check for updates on game start.
                Recommended!
                """);
}
