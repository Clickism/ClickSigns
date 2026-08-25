package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.template.TemplateMenuScreen;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.ui.elements.SignView;
import de.clickism.clickui.Element;
import de.clickism.clickui.Ref;
import de.clickism.clickui.UiScreen;
import net.minecraft.core.BlockPos;

import static de.clickism.clicksigns.util.ComponentUtil.t;

public class SignOverviewScreen extends UiScreen {

    private BlockPos blockPos;
    private RoadSign roadSign;

    public SignOverviewScreen(RoadSignBlockEntity entity) {
        this.blockPos = entity.getBlockPos();
        this.roadSign = entity.roadSign();
        if (this.roadSign == null) {
            this.roadSign = RoadSign.DEFAULT;
        }
    }

    @Override
    public Element<?> build() {
        Ref<SignView> signViewRef = ref();
        return box()
            .alignCenter()
            .childGap(24)
            .grow()
            .children(
                new SignView()
                    .roadSign(roadSign)
                    .ref(signViewRef),
                box()
                    .alignCenter()
                    .childGap(8)
                    .width(128)
                    .children(
                        button(t("✔ ", "clicksigns.text.confirm"))
                            .growWidth(),
                        button(t("📝 ", "clicksigns.text.change_template"))
                            .growWidth()
                            .onClick(event -> {
                                GuiUtils.openScreen(new TemplateMenuScreen(this, (template) -> {
                                    // Change template
                                    this.roadSign = template.build();
                                    signViewRef.get().roadSign(roadSign);
                                }));
                            }),
                        button(t("✎ ", "clicksigns.text.edit"))
                            .growWidth()
                    )
            );
    }
}
