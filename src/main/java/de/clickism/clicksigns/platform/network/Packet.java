package de.clickism.clicksigns.platform.network;

/**
 * Packet interface representing a network packet.
 */
public interface Packet {
    /**
     * The packet type of this packet
     *
     * @return the packet type of this packet
     */
    PacketType<?> type();
}
