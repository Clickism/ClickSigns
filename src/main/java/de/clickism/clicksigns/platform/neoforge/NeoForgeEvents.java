package de.clickism.clicksigns.platform.neoforge;

import de.clickism.clicksigns.ClickSigns;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class NeoForgeEvents {
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        var player = event.getEntity();
        if (player.level().isClientSide()) return;
        ClickSigns.UPDATE_NOTIFIER.notify((ServerPlayer) player);
    }
}
