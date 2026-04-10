package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.network.RoadSignUpdatePacket;
import de.clickism.clicksigns.sign.element.RoadSignElement;
import de.clickism.clicksigns.util.texture.Texture;
import net.minecraft.network.FriendlyByteBuf;
import org.w3c.dom.Text;

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
        Texture.WRITER.accept(buf, element.texture());
        Texture.WRITER.accept(buf, element.backTexture());
        buf.writeCollection(element.elements(), RoadSignElement.WRITER);
    };

    public static final FriendlyByteBuf.Reader<RoadSign> READER = (buf) -> {
        Texture texture = Texture.READER.apply(buf);
        Texture backTexture = Texture.READER.apply(buf);
        var elements = buf.readList(RoadSignElement.READER);
        return new RoadSign(texture, backTexture, elements);
    };
}
