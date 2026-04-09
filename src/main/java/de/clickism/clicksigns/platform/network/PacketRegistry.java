package de.clickism.clicksigns.platform.network;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PacketRegistry {
    private static final Map<ResourceLocation, PacketType<?>> PACKET_TYPES = new HashMap<>();

    public static <T extends Packet> void register(PacketType<T> packetType) {
        if (PACKET_TYPES.containsKey(packetType.id())) {
            throw new IllegalArgumentException("PacketType with id " + packetType.id() + " is already registered");
        }
        PACKET_TYPES.put(packetType.id(), packetType);
    }

    public static @Nullable PacketType<?> get(ResourceLocation id) {
        return PACKET_TYPES.get(id);
    }
}
