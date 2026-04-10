package de.clickism.clicksigns.platform.network;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for packet types.
 */
public class PacketRegistry {
    private static final Map<ResourceLocation, PacketType<?>> PACKET_TYPES = new HashMap<>();

    /**
     * Registers a packet type.
     *
     * @param packetType The packet type to be registered
     * @param <T>        the type of the packet
     */
    public static <T extends Packet> void register(PacketType<T> packetType) {
        if (PACKET_TYPES.containsKey(packetType.id())) {
            throw new IllegalArgumentException("PacketType with id " + packetType.id() + " is already registered");
        }
        PACKET_TYPES.put(packetType.id(), packetType);
    }

    /**
     * Gets the packet type with the given id.
     *
     * @param id the id of the packet type
     * @return the packet type with the given id, or null if no packet type found
     */
    public static @Nullable PacketType<?> get(ResourceLocation id) {
        return PACKET_TYPES.get(id);
    }
}
