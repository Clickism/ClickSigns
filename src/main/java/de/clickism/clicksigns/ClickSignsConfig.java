/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clicksigns;

import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;

public class ClickSignsConfig {
    public static Config CONFIG =
            Config.of("config/ClickSigns/config.jsonc")
                    .version(1)
                    .header("""
                            -------------------------------------------------------------
                            ClickSigns Config
                            NOTE: RELOAD/RESTART SERVER/CLIENT FOR CHANGES TO TAKE EFFECT
                            -------------------------------------------------------------
                            """);

    public static final ConfigOption<Boolean> CHECK_UPDATE =
            CONFIG.optionOf("check_update", true)
                    .description("""
                            Whether to check for updates. Strongly Recommended.
                            """)
                    .appendDefaultValue();
}
