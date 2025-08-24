/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clicksigns.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class UpdateNotifier implements ServerPlayConnectionEvents.Join {

    private static final boolean isClient = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;

    private final Supplier<String> newerVersionSupplier;
    // Warn players only once per session
    private final Set<UUID> notifiedPlayers = new HashSet<>();

    public UpdateNotifier(Supplier<String> newerVersionSupplier) {
        this.newerVersionSupplier = newerVersionSupplier;
    }

    @Override
    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        String newerVersion = newerVersionSupplier.get();
        if (newerVersion == null) return;
        ServerPlayerEntity player = handler.player;
        if (notifiedPlayers.contains(player.getUuid())) return;
        notifiedPlayers.add(player.getUuid());
        if (!player.hasPermissionLevel(3) && !isClient) return;
        Text message = Text.literal("§e[⚠] ClickSigns: §a§lNewer version available: §f" + newerVersion);
        player.sendMessage(message, false);
        VersionHelper.playSound(player, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.MASTER, 1, .5f);
    }
}
