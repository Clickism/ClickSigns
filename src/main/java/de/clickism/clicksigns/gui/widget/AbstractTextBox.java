package de.clickism.clicksigns.gui.widget;

//? if < 1.21.1
/*import net.minecraft.SharedConstants;*/
//? if >= 1.21.1
import net.minecraft.util.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A simple text box widget without rendering logic
 */
public abstract class AbstractTextBox extends AbstractWidget {
    private static final String DEFAULT_PLACEHOLDER = "Text";

    protected final Font font;

    protected String value = "";
    protected String placeholder = "";
    protected boolean editable = true;

    protected int textColor = Color.WHITE.getRGB();
    protected int backgroundColor = 0;

    protected int cursorPos = 0;
    protected int highlightPos = 0;

    protected List<Consumer<String>> listeners = new ArrayList<>();
    /**
     * Render frame counter for blinking cursor
     */
    protected int frame = 0;

    public AbstractTextBox(int x, int y, int width, int height, Font font) {
        super(x, y, width, height, Component.empty());
        this.font = font;
        this.placeholder = DEFAULT_PLACEHOLDER;
    }

    /**
     * Set the text value of the text box.
     *
     * @param value the text value to set
     */
    public void value(String value) {
        this.value = value;
        setValidCursor(value.length());
        highlightPos = cursorPos;
    }

    /**
     * Get the current text value of the text box.
     *
     * @return the current text value of the text box
     */
    public String value() {
        return this.value;
    }

    /**
     * Gets the current text or the placeholder if the text is empty.
     * Should be used for diplaying the text
     *
     * @return the current text or the placeholder if the text is empty
     */
    public String valueOrPlaceholder() {
        return this.value.isEmpty() ? this.placeholder : this.value;
    }

    /**
     * Set the callback for when the text value changes.
     *
     * @param onValueChanged callback
     */
    public void addListener(@NotNull Consumer<String> onValueChanged) {
        this.listeners.add(onValueChanged);
    }

    /**
     * Set whether the text box is editable.
     *
     * @param editable whether the text box is editable
     */
    public void editable(boolean editable) {
        this.editable = editable;
    }

    /**
     * Set the text color of the text box.
     *
     * @param textColor the text color to set
     */
    public void textColor(int textColor) {
        this.textColor = textColor;
    }

    /**
     * Set the background color of the text box.
     *
     * @param backgroundColor the background color to set
     */
    public void backgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    protected void triggerValueChanged() {
        for (var listener : listeners) {
            listener.accept(value);
        }
    }

    /**
     * Insert text at the current cursor position.
     *
     * @param string the text to insert
     */
    public void insertText(String string) {
        if (!editable) return;
        string = filterInput(string);
        if (highlightPos != cursorPos) {
            // Remove highlighted text before inserting
            int start = selectionStart();
            int end = selectionEnd();
            value = value.substring(0, start) + value.substring(end);
            cursorPos = start;
            highlightPos = cursorPos;
        }
        // Insert text
        value = value.substring(0, cursorPos) + string + value.substring(cursorPos);
        setValidCursor(cursorPos + string.length());
        highlightPos = cursorPos;
        triggerValueChanged();
    }

    /**
     * Gets the highlighted section of the text
     *
     * @return the highlighted text
     */
    public String highlightedText() {
        int start = Math.min(cursorPos, highlightPos);
        int end = Math.max(cursorPos, highlightPos);
        return value.substring(start, end);
    }

    /**
     * Gets the start index of the highlighted section of the text
     *
     * @return the start index of the highlighted text
     */
    private int selectionStart() {
        return Math.min(cursorPos, highlightPos);
    }

    /**
     * Gets the end index of the highlighted section of the text
     *
     * @return the end index of the highlighted text
     */
    private int selectionEnd() {
        return Math.max(cursorPos, highlightPos);
    }

    /**
     * Deletes text in the given direction (positive for forward, negative for backward)
     * If text is highlighted, it will be removed instead.
     *
     * @param direction the direction to delete in, positive or negative
     */
    public void deleteText(int direction) {
        if (!editable) return;
        if (direction == 0) return;
        // If text is highlighted, remove it instead
        if (highlightPos != cursorPos) {
            insertText("");
            return;
        }
        // Delete in direction
        if (direction > 0) {
            // Delete forward
            int end = Math.min(value.length(), cursorPos + direction);
            value = value.substring(0, cursorPos) + value.substring(end);
            // Cursor stays the same
        } else {
            // Delete backward
            int start = Math.max(0, cursorPos + direction);
            value = value.substring(0, start) + value.substring(cursorPos);
            cursorPos = start;
            highlightPos = cursorPos;
        }
        triggerValueChanged();
    }

    /**
     * Gets the position of the next word in the given direction (positive for forward, negative for backward).
     *
     * @param direction the direction to move in, positive or negative
     * @return the position of the next word in the given direction
     */
    private int wordPosition(int direction) {
        int pos = cursorPos;
        if (direction > 0) {
            // Skip current word
            while (pos < value.length() && !Character.isWhitespace(value.charAt(pos))) {
                pos++;
            }
            // skip spaces
            while (pos < value.length() && Character.isWhitespace(value.charAt(pos))) {
                pos++;
            }
        } else {
            // Skip spaces
            while (pos > 0 && Character.isWhitespace(value.charAt(pos - 1))) {
                pos--;
            }
            // Skip word
            while (pos > 0 && !Character.isWhitespace(value.charAt(pos - 1))) {
                pos--;
            }
        }
        return pos;
    }

    /**
     * Moves the cursor to a valid position in the given direction
     * (positive for forward, negative for backward).
     *
     * @param direction the direction to move in, positive or negative
     */
    private void moveCursor(int direction) {
        if (Screen.hasControlDown()) {
            // Move to next word
            cursorPos = wordPosition(direction);
        } else {
            // Move by one character
            cursorPos = Mth.clamp(cursorPos + direction, 0, value.length());
        }
        if (!Screen.hasShiftDown()) {
            highlightPos = cursorPos;
        }
    }

    /**
     * Sets the cursor position to a valid position and clamps if needed.
     *
     * @param pos the position to set the cursor to
     */
    private void setValidCursor(int pos) {
        this.cursorPos = Mth.clamp(pos, 0, value.length());
    }

    /**
     * Whether the text box is currently focused, editable and listening for input.
     *
     * @return true if the text box is focused and editable, false otherwise
     */
    public boolean listening() {
        return this.visible && this.isFocused() && this.editable;
    }

    /**
     * Filters the input string before inserting it into the text box.
     *
     * @param input the input string to filter
     * @return the filtered string
     */

    protected String filterInput(String input) {
        //? if < 1.21.1
        /*return SharedConstants.filterText(input);*/
        //? if >= 1.21.1
        return StringUtil.filterText(input);
    }

    @Override
    public boolean keyPressed(int code, int scanCode, int modifiers) {
        if (!listening()) return false;
        if (Screen.isSelectAll(code)) {
            // Move cursor to the end
            cursorPos = value.length();
            highlightPos = 0; // Highlight from start to end
            return true;
        }
        if (Screen.isCopy(code)) {
            // Copy highlighted text to clipboard
            var keyboard = Minecraft.getInstance().keyboardHandler;
            keyboard.setClipboard(highlightedText());
            return true;
        }
        if (Screen.isPaste(code)) {
            // Paste text from clipboard
            var keyboard = Minecraft.getInstance().keyboardHandler;
            insertText(keyboard.getClipboard());
            return true;
        }
        if (Screen.isCut(code)) {
            // Copy highlighted text to clipboard and remove it from the value
            var keyboard = Minecraft.getInstance().keyboardHandler;
            keyboard.setClipboard(highlightedText());
            insertText("");
            return true;
        }
        // Other keys
        return switch (code) {
            case GLFW.GLFW_KEY_BACKSPACE -> {
                deleteText(-1);
                yield true;
            }
            case GLFW.GLFW_KEY_DELETE -> {
                deleteText(1);
                yield true;
            }
            case GLFW.GLFW_KEY_LEFT -> {
                moveCursor(-1);
                yield true;
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                moveCursor(1);
                yield true;
            }
            case GLFW.GLFW_KEY_HOME -> {
                cursorPos = 0;
                if (!Screen.hasShiftDown()) {
                    highlightPos = cursorPos;
                }
                yield true;
            }
            case GLFW.GLFW_KEY_END -> {
                cursorPos = value.length();
                if (!Screen.hasShiftDown()) {
                    highlightPos = cursorPos;
                }
                yield true;
            }
            default -> false;
        };
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderText(guiGraphics, mouseX, mouseY, delta);
        frame++;
    }

    protected abstract void renderText(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta);

    @Override
    public boolean charTyped(char c, int modifiers) {
        if (!listening()) return false;
        //? if < 1.21.1
        /*if (!SharedConstants.isAllowedChatCharacter(c)) return false;*/
        //? if >= 1.21.1
        if (!StringUtil.isAllowedChatCharacter(c)) return false;
        insertText(Character.toString(c));
        return true;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        int i = (int) mouseX - this.getX(); // Relative mouse x
        // Move cursor to clicked position
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.createNarrationMessage());
    }
}
