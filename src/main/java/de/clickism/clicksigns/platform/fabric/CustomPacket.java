package de.clickism.clicksigns.platform.fabric;

import de.clickism.clicksigns.ClickSigns;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public class CustomPacket implements CustomPacketPayload {
    public static final Type<CustomPacket> TYPE = new Type<>(ClickSigns.identifier("global_packet"));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
