package de.clickism.clicksigns;

import de.clickism.modrinthupdatechecker.ModrinthVersion;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static de.clickism.clicksigns.util.ComponentUtil.l;
import static de.clickism.clicksigns.util.ComponentUtil.t;

/**
 * UpdateNotifier is responsible for notifying players about newer versions of the mod.
 * It keeps track of which players have already been notified to avoid duplicate messages.
 */
public class UpdateNotifier {
    private @Nullable ModrinthVersion newerVersion = null;
    private final Set<UUID> notifiedPlayers = new HashSet<>();

    /**
     * Sets the newer version of the mod.
     *
     * @param newerVersion The newer version to set, or null if there is no newer version.
     */
    public void newerVersion(@Nullable ModrinthVersion newerVersion) {
        this.newerVersion = newerVersion;
    }

    /**
     * Notifies the given player about the newer version if applicable.
     *
     * @param player The player to notify.
     */
    public void notify(ServerPlayer player) {
        if (newerVersion == null) return;
        if (notifiedPlayers.contains(player.getUUID())) return;
        notifiedPlayers.add(player.getUUID());
        if (!isOpOrInSinglePlayer(player.createCommandSourceStack())) return;

        var message = updateMessage(newerVersion);
        player.sendSystemMessage(message);
    }

    private Component updateMessage(ModrinthVersion version) {
        var versionNumber = version.strippedVersionNumber();
        var changelog = version.changelog();
        var link = "https://modrinth.com/mod/" + ClickSigns.MOD_ID + "/version/" + version.versionNumber();
        return t(
            "clicksigns.update.newer_version",
            l(versionNumber, ChatFormatting.BOLD, ChatFormatting.WHITE)
        ).copy()
            .append("\n")
            .append(t("clicksigns.update.changelog", l("\n" + changelog, ChatFormatting.RESET)))
            .append("\n")
            .append(t("clicksigns.update.download").copy()
                .withStyle(style -> style
                    .withClickEvent(new ClickEvent(
                        ClickEvent.Action.OPEN_URL,
                        link
                    ))));
    }

    private static boolean isOpOrInSinglePlayer(CommandSourceStack source) {
        var player = source.getPlayer();
        if (player != null) {
            var server = player.level().getServer();
            if (server != null && server.isSingleplayer()) {
                return true;
            }
        }
        return isOp(source);
    }

    private static boolean isOp(CommandSourceStack source) {
        //? if >=1.21.11 {
        /*var perms = source.permissions();
        return perms.hasPermission(Permissions.COMMANDS_ADMIN)
               || perms.hasPermission(Permissions.COMMANDS_OWNER);
        *///?} else
        return source.hasPermission(3);
    }
}
