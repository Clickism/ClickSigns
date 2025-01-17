/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns.sign;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class RoadSignBuilder {
    private Identifier templateId = RoadSignTemplateRegistration.getDefaultTemplate().getId();
    ;
    private int textureIndex = 0;
    private List<String> texts = new ArrayList<>();
    private RoadSign.Alignment alignment = RoadSign.Alignment.TOP_CENTER;

    public RoadSignBuilder templateId(Identifier templateId) {
        this.templateId = templateId;
        return this;
    }

    public RoadSignBuilder textureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
        return this;
    }

    public RoadSignBuilder texts(List<String> texts) {
        this.texts = texts;
        return this;
    }

    public RoadSignBuilder alignment(RoadSign.Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public Identifier templateId() {
        return templateId;
    }

    public int textureIndex() {
        return textureIndex;
    }

    public List<String> texts() {
        return texts;
    }

    public RoadSign.Alignment alignment() {
        return alignment;
    }

    public RoadSign build() {
        return new RoadSign(templateId, textureIndex, texts, alignment);
    }
}
