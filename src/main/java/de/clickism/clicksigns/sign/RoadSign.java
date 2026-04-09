package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.network.RoadSignUpdatePacket;
import de.clickism.clicksigns.sign.element.RoadSignElement;
import de.clickism.clicksigns.util.texture.Texture;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

/**
 * Road sign class.
 *
 * @param texture     Texture of the roadsign
 * @param backTexture Texture of the back of the roadsign
 * @param elements    Elements of the roadsign
 */
public record RoadSign(
        Texture texture,
        Texture backTexture,
        List<RoadSignElement> elements
) {
    public static final FriendlyByteBuf.Writer<RoadSign> WRITER = (buf, element) -> {
        RoadSignUpdatePacket.TEXTURE_WRITER.accept(buf, element.texture());
        RoadSignUpdatePacket.TEXTURE_WRITER.accept(buf, element.backTexture());
        buf.writeCollection(element.elements(), RoadSignUpdatePacket.ELEMENT_WRITER);
    };

    public static final FriendlyByteBuf.Reader<RoadSign> READER = (buf) -> {
        Texture texture = RoadSignUpdatePacket.TEXTURE_READER.apply(buf);
        Texture backTexture = RoadSignUpdatePacket.TEXTURE_READER.apply(buf);
        var elements = buf.readList(RoadSignUpdatePacket.ELEMENT_READER);
        return new RoadSign(texture, backTexture, elements);
    };
}
