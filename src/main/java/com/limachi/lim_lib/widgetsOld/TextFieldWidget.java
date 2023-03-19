package com.limachi.lim_lib.widgetsOld;

import com.limachi.lim_lib.data.History;
import com.limachi.lim_lib.LimLib;
import com.limachi.lim_lib.maths.Box2d;
import com.limachi.lim_lib.render.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@OnlyIn(Dist.CLIENT)
public class TextFieldWidget extends BaseWidget<TextFieldWidget> {

    public static final Pattern WORD_REGEX = Pattern.compile("[a-zA-Z0-9_]+");
    public static final ResourceLocation BACKGROUND = new ResourceLocation(LimLib.COMMON_ID, "textures/screen/text_field.png");
    public static final Box2d CUTOUT = new Box2d(256, 16);

    protected String text = "";
    protected int cursor = 0;
    protected int selection = 0;
    protected int scroll = 0;

    protected BiPredicate<String, TextFieldWidget> validator = null;
    protected Consumer<TextFieldWidget> onFinish = null;

    protected Font font = Minecraft.getInstance().font;
    protected int textColor = 0xFFFFFFFF;
    protected int cursorColor = 0xFFCCCCCC;
    protected int selectionColor = 0x994488CC;
    protected int scrollPadding = 2;
    protected final History<Pair<Integer, String>> history;

    protected boolean insertMode = true;

    protected double doubleClickTimer = 0; //count down from set value (when click happen), if another click is received before reaching 0, count it as double-click.
    protected double blinkTimer = 0; //count up to 20 then resets, cursor is visible while the count is <= 10. count is also reset when the cursor moves.

    public TextFieldWidget(int x, int y, int w) {
        super(x, y, w, 14, BACKGROUND, CUTOUT, false);
        history = new History<>(new Pair<>(0, ""));
    }

    public TextFieldWidget(int x, int y, int w, String initialText) {
        this(x, y, w);
        setText(initialText);
    }

    /**
     * set a predicate that will be called each time the text in the field tries to change
     * @param textValidator the first parameter will be the new string (not set) and the second the current state of the field widget. if the validator is set to null or returns true, then the string will be stored as the new field text.
     * @return self
     */
    public TextFieldWidget setValidator(@Nullable BiPredicate<String, TextFieldWidget> textValidator) {
        validator = textValidator;
        return this;
    }

    /**
     * set a callback that will run when input finishes (enter key).
     * @param onFinish the widget will pass itself as the sole parameter of the callback. if the callback is null, does nothing.
     * @return self
     */
    public TextFieldWidget setOnFinish(@Nullable Consumer<TextFieldWidget> onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    /**
     * set the font to be used by the field, by default use the currently selected minecraft font (at the time of widget creation).
     * @param font the new font to be used, make sure it is compatible with the widget size.
     * @return self
     */
    public TextFieldWidget setFont(@Nonnull Font font) {
        this.font = font;
        return this;
    }

    /**
     * set the palette of colors used for the widget. all parameters are self explain.
     * @return self
     */
    public TextFieldWidget setColors(int textColor, int cursorColor, int selectionColor) {
        this.textColor = textColor;
        this.cursorColor = cursorColor;
        this.selectionColor = selectionColor;
        return this;
    }

    protected void finishInput(boolean callback) {
        if (callback && onFinish != null)
            onFinish.accept(this);
        selection = cursor;
        focus(false);
    }

    /**
     * change the cursor position and if select is true, update the current selection. setting the cursor position outside rendering will scroll the text if possible.
     * @param pos the new position of the cursor.
     * @param select should the selection be updated (note: if false the current selection will be lost/reset).
     */
    public TextFieldWidget setCursorPos(int pos, boolean select) {
        cursor = Mth.clamp(pos, 0, text.length());
        if (text.length() > 0) {
            if (cursor - scrollPadding < scroll)
                scroll = Mth.clamp(cursor - scrollPadding, 0, text.length() - 1);
            else {
                String visible = getVisibleText();
                if (cursor + scrollPadding > scroll + visible.length() && font.width(visible) + font.width("|_|_") >= area.getWidth())
                    scroll = Mth.clamp(cursor - visible.length() + scrollPadding, 0, text.length() - 1);
            }
        }
        if (!select)
            selection = cursor;
        blinkTimer = 0;
        return this;
    }

    protected boolean shouldSelect() { return Screen.hasShiftDown() || isDragged(); }

    /**
     * select an area of text, setting the current cursor position at the end of selection (start can be greater than end for a backward selection).
     * @param start of selection
     * @param end of selection, will also be the position of the cursor on return
     * @return self
     */
    public TextFieldWidget select(int start, int end) {
        setCursorPos(start, false);
        setCursorPos(end, true);
        return this;
    }

    /**
     * using the current cursor position, try to select a word (any sequence not including punctuation or blanks)
     * @param left should the cursor be positioned to the left of the word (instead of right).
     * @return self
     */
    public TextFieldWidget selectWord(boolean left) {
        Matcher m = WORD_REGEX.matcher(text);
        while (m.find()) {
            if (m.start() <= cursor && m.end() >= cursor) {
                if (left)
                    select(m.end(), m.start());
                else
                    select(m.start(), m.end());
                return this;
            }
        }
        return this;
    }

    /**
     * set the current text (and cursor pos at the end of the text).
     * @param text new text to replace the current field with.
     * @param select if true, also update the selection.
     * @return self
     */
    public TextFieldWidget setText(String text, boolean select) {
        if (validator == null || validator.test(text, this)) {
            this.text = text;
            if (cursor > text.length())
                setCursorPos(text.length(), select);
        }
        return this;
    }

    /**
     * set the current text, without selection but will update the history.
     * @return self
     */
    public TextFieldWidget setText(String text) {
        setText(text, false);
        pushToHistory();
        return this;
    }

    /**
     * @return get the full text
     */
    public String getText() { return text; }

    /**
     * @return get only the current selection or an empty string if nothing is selected
     */
    public String getSelectedText() { return text.length() == 0 || selection == cursor ? "" : text.substring(Math.min(selection, cursor), Math.max(selection, cursor)); }

    /**
     * @return get the current selection or the full text if no selection is active
     */
    public String getSelectedOrAll() { return selection == cursor ? text : text.substring(Math.min(selection, cursor), Math.max(selection, cursor)); }

    /**
     * @return get only the visible (rendered) part of the string
     */
    public String getVisibleText() { return font.plainSubstrByWidth(text.substring(scroll), (int)area.getWidth() - 6); }

    /**
     * push the current text and cursor position to history, clearing pages if more than one undo was made
     * @return self
     */
    public TextFieldWidget pushToHistory() {
        history.write(new Pair<>(cursor, text));
        return this;
    }

    /**
     * load the state from a previous history point.
     * @param depth (can be negative to jump forward if no push was done since the last load).
     * @return self
     */
    public TextFieldWidget loadFromHistory(int depth) {
        Pair<Integer, String> p = history.read(depth);
        setText(p.getSecond(), false);
        setCursorPos(p.getFirst(), false);
        return this;
    }

    /**
     * write text at the cursor position (or in the selection if active). will also update the history.
     * @return self
     */
    public TextFieldWidget writeText(String add) {
        add = SharedConstants.filterText(add);
        String start = text.substring(0, Math.min(selection, cursor));
        String end = text.substring(Math.max(selection, cursor));
        if (!insertMode)
            end = end.length() > add.length() ? end.substring(add.length()) : "";
        setText(start + add + end, false);
        setCursorPos((start + add).length(), false);
        pushToHistory();
        return this;
    }

    /**
     * delete characters (or the current selection).
     * @param amount negative to delete characters before the cursor (backspace), positive for after the cursor (del)
     * @return self
     */
    public TextFieldWidget delete(int amount) {
        if (selection != cursor)
            return writeText("");
        int p = Mth.clamp(cursor + amount, 0, text.length());
        if (amount < 0) {
            text = text.substring(0, p) + text.substring(cursor);
            setCursorPos(p, false);
        } else
            text = text.substring(0, cursor) + text.substring(p);
        return this;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isFocused()) return false;
        if (Screen.isSelectAll(keyCode)) {
            cursor = text.length();
            selection = 0;
            return true;
        }
        if (Screen.isCopy(keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(getSelectedOrAll());
            return true;
        }
        if (Screen.isPaste(keyCode)) {
            writeText(Minecraft.getInstance().keyboardHandler.getClipboard());
            return true;
        }
        if (Screen.isCut(keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(getSelectedText());
            writeText("");
            return true;
        }
        switch (keyCode) {
            case GLFW.GLFW_KEY_ESCAPE:
                finishInput(false);
                return true;
            case GLFW.GLFW_KEY_ENTER:
                finishInput(true);
                return true;
            case GLFW.GLFW_KEY_BACKSPACE:
                delete(-1);
                return true;
            case GLFW.GLFW_KEY_INSERT:
                insertMode = !insertMode;
                return true;
            case GLFW.GLFW_KEY_DELETE:
                delete(1);
                return true;
            case GLFW.GLFW_KEY_RIGHT:
                setCursorPos(cursor + 1, shouldSelect());
                return true;
            case GLFW.GLFW_KEY_LEFT:
                setCursorPos(cursor - 1, shouldSelect());
                return true;
            case GLFW.GLFW_KEY_HOME:
                setCursorPos(0, shouldSelect());
                return true;
            case GLFW.GLFW_KEY_END:
                setCursorPos(text.length(), shouldSelect());
                return true;
            case GLFW.GLFW_KEY_Z:
                if (Screen.hasControlDown()) {
                    loadFromHistory(1);
                    return true;
                }
            case GLFW.GLFW_KEY_Y:
                if (Screen.hasControlDown()) {
                    loadFromHistory(-1);
                    return true;
                }
            case GLFW.GLFW_KEY_TAB:
            case GLFW.GLFW_KEY_DOWN:
            case GLFW.GLFW_KEY_UP:
            case GLFW.GLFW_KEY_PAGE_UP:
            case GLFW.GLFW_KEY_PAGE_DOWN:
            default:
        }
        return true;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!isFocused()) return false;
        if (SharedConstants.isAllowedChatCharacter(codePoint)) {
            writeText(Character.toString(codePoint));
            return true;
        }
        return false;
    }

    protected void moveCursorToMouse(double mouseX, double mouseY) {
        String s = getVisibleText();
        int i = 1;
        int x = relativeMouseOverX(mouseX);
        while (i <= s.length() && font.width(s.substring(0, i)) < x)
            ++i;
        setCursorPos(i - 1 + scroll, shouldSelect());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        if (isMouseOver(mouseX, mouseY)) {
            moveCursorToMouse(mouseX, mouseY);
            if (doubleClickTimer > 0)
                selectWord(true);
            doubleClickTimer = 5;
            return true;
        }
        finishInput(false);
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double originX, double originY) {
        if (!isFocused() || !isMouseOver(mouseX, mouseY) || button != 0) return super.mouseDragged(mouseX, mouseY, button, originX, originY);
        moveCursorToMouse(mouseX, mouseY);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (!isMouseOver(mouseX, mouseY) || scrollAmount == 0 || text.length() < scrollPadding) return super.mouseScrolled(mouseX, mouseY, scrollAmount);
        scroll = Mth.clamp(scroll + (scrollAmount < 0 ? scrollPadding : -scrollPadding), 0, text.length() - 1);
        return true;
    }

    @Override
    public void renderRelative(PoseStack stack, int mouseX, int mouseY, float partialTick, boolean isMouseOver) {
        int y = (int) (area.getHeight() - font.lineHeight) / 2;
        int cursorPos = scroll < cursor ? font.width((text + " ").substring(scroll, cursor)) : 0;
        if (selection != cursor) {
            int selectionPos = 0;
            if (selection >= scroll)
                selectionPos = font.width(text.substring(scroll, selection));
            int start = Math.min(selectionPos, cursorPos);
            int end = Math.max(selectionPos, cursorPos);
            Box2d b = new Box2d(1 + start, y - 1, end - start + 3, area.getHeight() - y * 2 + 2);
            if (b.getX2() > area.getWidth() - 1)
                b.setX2(area.getWidth() - 1);
            RenderUtils.drawBox(stack, b, selectionColor, 0);
        }
        RenderUtils.drawString(stack, font, getVisibleText(), new Box2d(3, y + 1, area.getWidth() - 6, area.getHeight() - y * 2 - 1), textColor, true, false);
        if (isFocused() && blinkTimer <= 10 && scroll <= cursor && cursorPos < area.getWidth() - 6)
            RenderUtils.drawString(stack, font, insertMode && cursor != text.length() ? "|" : "_", new Box2d(cursorPos + 2, y + 1, area.getWidth() - 6 - cursorPos, area.getHeight() - y * 2 - 1), cursorColor, true, false);
        if (doubleClickTimer > 0)
            doubleClickTimer -= partialTick;
        blinkTimer += partialTick;
        if (blinkTimer > 20)
            blinkTimer = 0;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !isFocused();
    }
}
