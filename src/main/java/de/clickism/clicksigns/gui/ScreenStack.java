package de.clickism.clicksigns.gui;

import net.minecraft.client.gui.screens.Screen;

import java.util.Stack;

/**
 * Simple navigation stack for screens.
 */
public class ScreenStack {
    /**
     * Main instance of the screen stack.
     */
    public static final ScreenStack INSTANCE = new ScreenStack();

    private final Stack<Screen> stack = new Stack<>();

    /**
     * Pushes a screen onto the stack and opens it.
     *
     * @param screen The screen to open
     */
    public void open(Screen screen) {
        stack.push(screen);
        GuiUtils.openScreen(screen);
    }

    /**
     * Pops the current screen and opens the previous one if it exists.
     */
    public void back() {
        if (stack.isEmpty()) return;
        stack.pop();
        if (stack.isEmpty()) return;
        var screen = stack.peek();
        GuiUtils.openScreen(screen);
    }
}
