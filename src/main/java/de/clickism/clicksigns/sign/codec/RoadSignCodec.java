package de.clickism.clicksigns.sign.codec;

import de.clickism.clicksigns.serialization.codec.CommonCodec;
import de.clickism.clicksigns.serialization.codec.PacketCodec;
import de.clickism.clicksigns.serialization.codec.TagCodec;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.texture.source.TextureSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Codec for serializing and deserializing {@link RoadSign} objects.
 */
public interface RoadSignCodec {
    /**
     * Gets the codec for serializing and deserializing road signs.
     *
     * @return the codec for road signs
     */
    static CommonCodec<RoadSign> codec() {
        return CommonCodec.of(
            TagCodec.of(
                (tag, sign) -> {
                    var front = tag.createTag();
                    var back = tag.createTag();
                    TextureSource.codec().writeTag(front, sign.frontSource());
                    TextureSource.codec().writeTag(back, sign.backSource());
                    tag.putTag("front", front);
                    tag.putTag("back", back);
                    tag.putCollection("elements", sign.elements(), SignElementCodec.codec().tagWriter());
                    tag.putString("alignment", sign.alignment().name());
                },
                (tag) -> {
                    var frontTag = tag.getTag("front").orElseThrow();
                    var backTag = tag.getTag("back").orElseThrow();
                    var front = TextureSource.codec().readTag(frontTag);
                    var back = TextureSource.codec().readTag(backTag);
                    var elements = tag.getCollection("elements", SignElementCodec.codec().tagReader()).orElse(List.of());
                    var alignment = Alignment.valueOf(tag.getString("alignment").orElse(RoadSign.DEFAULT_ALIGNMENT.name()));
                    return new RoadSign(front, back, new ArrayList<>(elements), alignment);
                }
            ),
            PacketCodec.of(
                (buf, sign) -> {
                    TextureSource.codec().writePacket(buf, sign.frontSource());
                    TextureSource.codec().writePacket(buf, sign.backSource());
                    buf.writeCollection(sign.elements(), SignElementCodec.codec().packetWriter());
                    buf.writeInt(sign.alignment().ordinal());
                },
                (buf) -> {
                    var front = TextureSource.codec().readPacket(buf);
                    var back = TextureSource.codec().readPacket(buf);
                    var elements = buf.readList(SignElementCodec.codec().packetReader());
                    var alignment = Alignment.values()[buf.readInt()];
                    return new RoadSign(front, back, elements, alignment);
                }
            )
        );
    }
}
