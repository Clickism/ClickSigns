package de.clickism.clicksigns.platform.forge;

import de.clickism.clicksigns.ClickSigns;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeEvents {
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        var player = event.getEntity();
        if (player.level().isClientSide()) return;
        ClickSigns.UPDATE_NOTIFIER.notify((ServerPlayer) player);
    }
}
