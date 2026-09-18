package de.clickism.clicksigns.ui.components;

import de.clickism.clickui.BaseComponents;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiElement;
import de.clickism.clickui.elements.Box;
import de.clickism.clickui.elements.Text;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import static de.clickism.clicksigns.util.ComponentUtil.t;

/**
 * A collection of custom UI components.
 */
public interface CommonComponents extends BaseComponents {
    default Box fancyHeader(Component text) {
        return box()
            .padding(4)
            .growWidth()
            .alignCenter()
            .style(style()
                .borderColorBottom(UiColor.WHITE_A60)
                .backgroundColor(UiColor.WHITE.alpha(0.10f)))
            .children(
                text(text)
                    .style(style()
                        .alpha(0.9f))
            );
    }

    default Box smallHeader(Component text) {
        return box()
            .growWidth()
            .padding(8, 0, 0, 0)
            .children(
                box()
                    .padding(3, 0, 2, 0)
                    .growWidth()
                    .alignCenter()
                    .style(style()
                        .borderColorBottom(UiColor.WHITE_A30)
                        .backgroundColor(UiColor.WHITE.alpha(0.05f)))
                    .children(
                        text(text)
                            .style(style()
                                .fontScale(0.75f)
                                .alpha(0.8f))
                    ));
    }

    default Text islandHeader(Component text) {
        return h4(text)
            .padding(6, 12)
            .style(style()
                .borderColor(UiColor.LIGHT_GRAY.alpha(0.5f))
                .backgroundColor(UiColor.BLACK.alpha(0.5f)));
    }

    default Text paragraph(Component text) {
        return text(text)
            .growWidth()
            .style(style()
                .fontScale(0.95f)
                .alpha(0.9f));
    }

    default Box darkBox() {
        return box()
            .growWidth()
            .padding(4)
            .style(style()
                .backgroundColor(UiColor.BLACK_A50));
    }

    default Box darkBoxOutlined() {
        return box()
            .growWidth()
            .padding(4)
            .style(style()
                .backgroundColor(UiColor.BLACK_A50)
                .borderColor(UiColor.LIGHT_GRAY.alpha(0.5f)));
    }

    default Box withHeader(Component header, UiElement<?>... content) {
        return box()
            .growWidth()
            .childGap(4)
            .children(smallHeader(header).padding(0))
            .children(content);
    }

    default Text smallParagraph(Component text) {
        return text(text)
            .growWidth()
            .style(style()
                .fontScale(0.8f)
                .alpha(0.8f));
    }

    default UiElement<?> action(Component text) {
        return text(text.copy().withStyle(ChatFormatting.BOLD))
            .padding(3, 2, 2, 3)
            .style(style()
                .textColor(UiColor.WHITE_A90)
                .backgroundColor(UiColor.WHITE_A20)
                .fontScale(0.75f));
    }

    default UiElement<?> action(Component... texts) {
        var box = box()
            .horizontal()
            .childGap(2)
            .alignCenter();
        for (var text : texts) {
            box.add(action(text));
        }
        return box;
    }

    default UiElement<?> descriptions(UiElement<?>... descriptions) {
        return box()
            .vertical()
            .childGap(2)
            .children(descriptions);
    }

    default UiElement<?> describeAction(UiElement<?> action, Component description) {
        return box()
            .horizontal()
            .childGap(3)
            .alignCenter()
            .children(
                action,
//                box()
//                    .growHeight()
//                    .width(2)
//                    .style(style()
//                        .backgroundColor(UiColor.WHITE_A50)),
                text(description)
                    .padding(1, 0, 0, 1)
                    .style(style()
                        .textColor(UiColor.WHITE_A90)
                        .fontScale(0.95f))
            );
    }

    default UiElement<?> describeLeftClick(Component description) {
        return describeAction(leftClick(), description);
    }

    default UiElement<?> describeRightClick(Component description) {
        return describeAction(rightClick(), description);
    }

    default UiElement<?> describeShiftClick(Component description) {
        return describeAction(shiftClick(), description);
    }

    default UiElement<?> leftClick() {
        return action(t("clicksigns.ui.leftClick"));
    }

    default UiElement<?> rightClick() {
        return action(t("clicksigns.ui.rightClick"));
    }

    default UiElement<?> shiftClick() {
        return action(t("clicksigns.ui.shiftClick"));
    }
}
