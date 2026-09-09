package de.clickism.clicksigns.ui;

import de.clickism.clickui.UiBuilder;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.elements.Box;
import net.minecraft.network.chat.Component;

public interface FancyHeaders extends UiBuilder {
    default Box fancyHeader(Component text) {
        return box()
            .padding(4)
            .growWidth()
            .alignCenter()
            .style(style()
                .borderColor(UiColor.WHITE_A30)
                .backgroundColor(UiColor.WHITE_A10))
            .children(
                text(text)
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
}
