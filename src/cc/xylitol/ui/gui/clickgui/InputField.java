package cc.xylitol.ui.gui.clickgui;

import cc.xylitol.ui.font.FontManager;
import cc.xylitol.ui.font.RapeMasterFontManager;
import cc.xylitol.utils.TimerUtil;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.utils.render.animation.AnimationUtils;
import cc.xylitol.utils.render.animation.impl.ColorAnimation;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import org.lwjglx.input.Keyboard;

import java.awt.*;

public class InputField extends Gui {
    private final TimerUtil timerUtil = new TimerUtil();
    public RapeMasterFontManager font;
    private float xPosition, yPosition, radius = 2, alpha = 1;
    private float width, height, textAlpha = 1;
    private Color outline = Color.WHITE, fill = new Color(32, 32, 32), focusedFill = new Color(32, 32, 32);
    private Color focusedTextColor = new Color(77, 77, 77);
    private Color unfocusedTextColor = new Color(77, 77, 77);
    private ColorAnimation realFillColor = new ColorAnimation(new Color(0, 0, 0, 144));
    /**
     * Has the current text being edited on the textbox.
     */
    private String text = "";
    private String backgroundText;
    private int maxStringLength = 32;
    private boolean drawingBackground = true;
    /**
     * if true the textbox can lose focus by clicking elsewhere on the screen
     */
    private boolean canLoseFocus = true;
    /**
     * If this value is true along with isEnabled, keyTyped will process the keys.
     */
    private boolean isFocused;
    /**
     * The current character index that should be used as start of the rendered text.
     */
    private int lineScrollOffset;
    private int cursorPosition;
    /**
     * other selection position, maybe the same as the cursor
     */
    private int selectionEnd;
    //    private final Animation textColor = new DecelerateAnimation(250, 1);
//    private final Animation cursorBlinkAnimation = new DecelerateAnimation(750, 1);
    private boolean textColorStats = false;
    private float textColor = 0;
    private boolean cursorBlinkAnimationStats = false;
    private float cursorBlinkAnimation = 0;
    /**
     * True if this textbox is visible
     */
    private boolean visible = true;
    private boolean password = false;

    public InputField(RapeMasterFontManager font) {
        this.font = font;
    }

    public InputField(RapeMasterFontManager font, float x, float y, float par5Width, float par6Height) {
        this.font = font;
        this.xPosition = x;
        this.yPosition = y;
        this.width = par5Width;
        this.height = par6Height;
    }

    public static int applyOpacity(int color, float opacity) {
        Color old = new Color(color);
        return applyOpacity(old, opacity).getRGB();
    }

    //Opacity value ranges from 0-1
    public static Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1, Math.max(0, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * opacity));
    }

    /**
     * Returns the contents of the textbox
     */
    public String getText() {
        return this.text;
    }

    /**
     * Sets the text of the textbox
     */
    public void setText(String text) {
        if (text.length() > this.maxStringLength) {
            this.text = text.substring(0, this.maxStringLength);
        } else {
            this.text = text;
        }

        setCursorPositionZero();
    }

    /**
     * returns the text between the cursor and selectionEnd
     */
    public String getSelectedText() {
        int i = Math.min(this.cursorPosition, this.selectionEnd);
        int j = Math.max(this.cursorPosition, this.selectionEnd);
        return this.text.substring(i, j);
    }

    /**
     * replaces selected text, or inserts text at the position on the cursor
     */
    public void writeText(String text) {
        String s = "";
        String s1 = ChatAllowedCharacters.filterAllowedCharacters(text);
        int min = Math.min(this.cursorPosition, this.selectionEnd);
        int max = Math.max(this.cursorPosition, this.selectionEnd);
        int len = this.maxStringLength - this.text.length() - (min - max);
        int l;

        if (this.text.length() > 0) {
            s = s + this.text.substring(0, min);
        }

        if (len < s1.length()) {
            s = s + s1.substring(0, len);
            l = len;
        } else {
            s = s + s1;
            l = s1.length();
        }

        if (this.text.length() > 0 && max < this.text.length()) {
            s = s + this.text.substring(max);
        }

        this.text = s;
        this.moveCursorBy(min - this.selectionEnd + l);


    }

    /**
     * Deletes the specified number of words starting at the cursor position. Negative numbers will delete words left of
     * the cursor.
     */
    public void deleteWords(int num) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
            }
        }
    }

    /**
     * delete the selected text, otherwsie deletes characters from either side of the cursor. params: delete num
     */
    public void deleteFromCursor(int num) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean negative = num < 0;
                int i = negative ? this.cursorPosition + num : this.cursorPosition;
                int j = negative ? this.cursorPosition : this.cursorPosition + num;
                String s = "";

                if (i >= 0) {
                    s = this.text.substring(0, i);
                }

                if (j < this.text.length()) {
                    s = s + this.text.substring(j);
                }

                this.text = s;

                if (negative) {
                    this.moveCursorBy(num);
                }

            }
        }
    }

    /**
     * see @getNthNextWordFromPos() params: N, position
     */
    public int getNthWordFromCursor(int n) {
        return this.getNthWordFromPos(n, this.getCursorPosition());
    }

    /**
     * gets the position of the nth word. N may be negative, then it looks backwards. params: N, position
     */
    public int getNthWordFromPos(int n, int pos) {
        return this.func_146197_a(n, pos);
    }

    public int func_146197_a(int n, int pos) {
        int i = pos;
        boolean negative = n < 0;
        int j = Math.abs(n);

        for (int k = 0; k < j; ++k) {
            if (!negative) {
                int l = this.text.length();
                i = this.text.indexOf(32, i);

                if (i == -1) {
                    i = l;
                } else {
                    while (i < l && this.text.charAt(i) == 32) {
                        ++i;
                    }
                }
            } else {
                while (i > 0 && this.text.charAt(i - 1) == 32) {
                    --i;
                }

                while (i > 0 && this.text.charAt(i - 1) != 32) {
                    --i;
                }
            }
        }

        return i;
    }

    /**
     * Moves the text cursor by a specified number of characters and clears the selection
     */
    public void moveCursorBy(int p_146182_1_) {
        this.setCursorPosition(this.selectionEnd + p_146182_1_);
    }

    /**
     * sets the cursors position to the beginning
     */
    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    /**
     * sets the cursors position to after the text
     */
    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    /**
     * Call this method from your GuiScreen to process the keys into the textbox
     */
    public boolean keyTyped(char cha, int keyCode) {
        if (!this.isFocused) {
            return false;
        }

        timerUtil.reset();

        if (GuiScreen.isKeyComboCtrlA(keyCode)) {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
        } else if (GuiScreen.isKeyComboCtrlC(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            return true;
        } else if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            this.writeText(GuiScreen.getClipboardString());


            return true;
        } else if (GuiScreen.isKeyComboCtrlX(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            this.writeText("");


            return true;
        } else {
            switch (keyCode) {
                case 0x0E:
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.deleteWords(-1);
                    } else {
                        this.deleteFromCursor(-1);
                    }

                    return true;

                case 0xC7:
                    if (GuiScreen.isShiftKeyDown()) {
                        this.setSelectionPos(0);
                    } else {
                        this.setCursorPositionZero();
                    }

                    return true;

                case 0xCB:
                    if (GuiScreen.isShiftKeyDown()) {
                        if (GuiScreen.isCtrlKeyDown()) {
                            this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                        } else {
                            this.setSelectionPos(this.getSelectionEnd() - 1);
                        }
                    } else if (GuiScreen.isCtrlKeyDown()) {
                        this.setCursorPosition(this.getNthWordFromCursor(-1));
                    } else {
                        this.moveCursorBy(-1);
                    }

                    return true;

                case 0xCD:
                    if (GuiScreen.isShiftKeyDown()) {
                        if (GuiScreen.isCtrlKeyDown()) {
                            this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                        } else {
                            this.setSelectionPos(this.getSelectionEnd() + 1);
                        }
                    } else if (GuiScreen.isCtrlKeyDown()) {
                        this.setCursorPosition(this.getNthWordFromCursor(1));
                    } else {
                        this.moveCursorBy(1);
                    }

                    return true;

                case 0xCF:
                    if (GuiScreen.isShiftKeyDown()) {
                        this.setSelectionPos(this.text.length());
                    } else {
                        this.setCursorPositionEnd();
                    }

                    return true;

                case 0xD3:
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.deleteWords(1);
                    } else {
                        this.deleteFromCursor(1);
                    }

                    return true;

                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(cha)) {
                        this.writeText(Character.toString(cha));

                        return true;
                    } else {
                        return false;
                    }
            }
        }
    }

    /**
     * Args: x, y, buttonClicked
     */
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean flag = RenderUtil.isHovering(xPosition, yPosition, width, height, mouseX, mouseY);

        if (this.canLoseFocus) {
            this.setFocused(flag);
        }

        if (this.isFocused && flag && mouseButton == 0) {
            float xPos = xPosition;
            if (backgroundText != null && backgroundText.equals("Search")) {
                xPos += 13;
            }

            float i = mouseX - xPos;

            String s = this.font.trimStringToWidth(this.text.substring(this.lineScrollOffset), (int) this.getWidth());
            this.setCursorPosition(this.font.trimStringToWidth(s, (int) i).length() + this.lineScrollOffset);
        }
        return flag;
    }

    /**
     * Draws the textbox
     */
    public void drawTextBox(int mouseX, int mouseY) {
        if (this.getVisible()) {

            if (isFocused()) {
                realFillColor.animateTo(focusedFill, 0.5f);
                Keyboard.enableRepeatEvents(true);
            } else {
                realFillColor.animateTo(fill, 0.5f);
            }

            Color textColorWithAlpha = focusedTextColor;
            if (textAlpha != 1) {
                textColorWithAlpha = applyOpacity(focusedTextColor, textAlpha);
            }


            float xPos = this.xPosition + 3;
            float yPos = this.yPosition + font.getMiddleOfBox(height);

            if (this.isDrawingBackground()) {
                RenderUtil.renderRoundedRect(this.xPosition, this.yPosition, this.width, this.height, radius, realFillColor.getColor().getRGB());
            }


            if (backgroundText != null) {
                Color backgroundTextColor = applyOpacity(applyOpacity(unfocusedTextColor, textAlpha), textColor);
                if (backgroundText.equals("Search")) {
                    xPos += 15;
                }

                if (text.equals("") /* && !(textColor == 0)*/) {
                    font.drawString(backgroundText, xPos, yPos, new Color(77,77,77).getRGB());
                }
            }


            int cursorPos = this.cursorPosition - this.lineScrollOffset;
            int selEnd = this.selectionEnd - this.lineScrollOffset;
            String text = this.font.trimStringToWidth(this.text.substring(this.lineScrollOffset), (int) this.getWidth());
            boolean cursorInBounds = cursorPos >= 0 && cursorPos <= text.length();
            boolean canShowCursor = this.isFocused && cursorInBounds;
            float j1 = xPos;


            if (selEnd > text.length()) {
                selEnd = text.length();
            }


            if (text.length() > 0) {
                String s1 = cursorInBounds ? text.substring(0, cursorPos) : text;
                this.font.drawString(password ? s1.replaceAll(".", "*") : s1, xPos, yPos, textColorWithAlpha.getRGB());
                j1 = this.font.getStringWidth(password ? s1.replaceAll(".", "*") : s1) + .5f;
            }

            boolean cursorEndPos = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            float k1 = j1;

            if (!cursorInBounds) {
                k1 = cursorPos > 0 ? xPos + this.width : xPos;
            } else if (cursorEndPos) {
                k1 = j1;
                --j1;
            }

            if (text.length() > 0 && cursorInBounds && cursorPos < text.length()) {
                this.font.drawString(password ? text.substring(cursorPos).replaceAll(".", "*") : text.substring(cursorPos), j1 + 2f, yPos, textColorWithAlpha.getRGB());
                j1 = font.getStringWidth(password ? text.substring(cursorPos).replaceAll(".", "*") : text.substring(cursorPos));
            }

            boolean cursorBlink = timerUtil.hasTimeElapsed(1000) || cursorEndPos;

            if (cursorBlink) {
                if (cursorBlinkAnimation == 1 || cursorBlinkAnimation == 0) {
                    cursorBlinkAnimationStats = !cursorBlinkAnimationStats;
                }
            } else {
                cursorBlinkAnimationStats = false;
            }

            cursorBlinkAnimation = AnimationUtils.animate(cursorBlinkAnimation, cursorBlinkAnimationStats ? 0 : 1, 0.1f);


      /*      RenderUtil.drawRect(k1 + 1, yPos - 2, .5f, font.getHeight() + 3,
                    new Color(1,1,1,cursorBlinkAnimation).getRGB());*/

 /*           if (selEnd != cursorPos) {
                int l1 = (int) (xPos + this.font.getStringWidth(text.substring(0, selEnd)));
                int offset = selEnd > cursorPos ? 2 : 0;
                float widthOffset = selEnd > cursorPos ? .5f : 0;

                drawSelectionBox(k1 + offset, yPos - 1, l1 + widthOffset, yPos + 1 + font.getHeight());
            }*/
        }

    }

    /**
     * draws the vertical line cursor in the textbox
     */
    private void drawSelectionBox(float x, float y, float width, float height) {
        if (x < width) {
            float i = x;
            x = width;
            width = i;
        }

        if (y < height) {
            float j = y;
            y = height;
            height = j;
        }

        if (width > this.xPosition + this.width) {
            width = this.xPosition + this.width;
        }

        if (x > this.xPosition + this.width) {
            x = this.xPosition + this.width;
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5387);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x, height, 0.0D).endVertex();
        worldrenderer.pos(width, height, 0.0D).endVertex();
        worldrenderer.pos(width, y, 0.0D).endVertex();
        worldrenderer.pos(x, y, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    /**
     * returns the maximum number of character that can be contained in this textbox
     */
    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public void setMaxStringLength(int len) {
        this.maxStringLength = len;

        if (this.text.length() > len) {
            this.text = this.text.substring(0, len);
        }
    }

    /**
     * returns the current position of the cursor
     */
    public int getCursorPosition() {
        return this.cursorPosition;
    }

    /**
     * sets the position of the cursor to the provided index
     */
    public void setCursorPosition(int p_146190_1_) {
        this.cursorPosition = p_146190_1_;
        int i = this.text.length();
        this.cursorPosition = MathHelper.clamp_int(this.cursorPosition, 0, i);
        this.setSelectionPos(this.cursorPosition);
    }

    /**
     * Sets the text colour for this textbox (disabled text will not use this colour)
     */
    public void setTextColor(Color color) {
        this.focusedTextColor = color;
    }

    public void setDisabledTextColour(Color color) {
        this.unfocusedTextColor = color;
    }

    /**
     * the side of the selection that is not the cursor, may be the same as the cursor
     */
    public int getSelectionEnd() {
        return this.selectionEnd;
    }

    /**
     * returns the width of the textbox depending on if background drawing is enabled
     */
    public float getWidth() {
        boolean flag = backgroundText != null && backgroundText.equals("Search");
        return this.isDrawingBackground() ? this.width - (flag ? 24 : 11) : this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getRealWidth() {
        return this.isDrawingBackground() ? this.width - 11 : this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * Sets the position of the selection anchor (i.e. position the selection was started at)
     */
    public void setSelectionPos(int selectionPos) {
        int i = this.text.length();

        if (selectionPos > i) {
            selectionPos = i;
        }

        if (selectionPos < 0) {
            selectionPos = 0;
        }

        this.selectionEnd = selectionPos;

        if (this.font != null) {
            if (this.lineScrollOffset > i) {
                this.lineScrollOffset = i;
            }

            float j = this.getWidth();
            String s = this.font.trimStringToWidth(this.text.substring(this.lineScrollOffset), (int) j);
            int k = s.length() + this.lineScrollOffset;

            if (selectionPos == this.lineScrollOffset) {
                this.lineScrollOffset -= this.font.trimStringToWidth(this.text, (int) j, true).length();
            }

            if (selectionPos > k) {
                this.lineScrollOffset += selectionPos - k;
            } else if (selectionPos <= this.lineScrollOffset) {
                this.lineScrollOffset -= this.lineScrollOffset - selectionPos;
            }

            this.lineScrollOffset = MathHelper.clamp_int(this.lineScrollOffset, 0, i);
        }
    }

    /**
     * if true the textbox can lose focus by clicking elsewhere on the screen
     */
    public void setCanLoseFocus(boolean canLoseFocus) {
        this.canLoseFocus = canLoseFocus;
    }

    /**
     * returns true if this textbox is visible
     */
    public boolean getVisible() {
        return this.visible;
    }

    /**
     * Sets whether this textbox is visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public float getxPosition() {
        return xPosition;
    }

    public void setxPosition(float xPosition) {
        this.xPosition = xPosition;
    }

    public float getyPosition() {
        return yPosition;
    }

    public void setyPosition(float yPosition) {
        this.yPosition = yPosition;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getTextAlpha() {
        return textAlpha;
    }

    public void setTextAlpha(float textAlpha) {
        this.textAlpha = textAlpha;
    }

    public Color getOutline() {
        return outline;
    }

    public void setOutline(Color outline) {
        this.outline = outline;
    }

    public Color getFill() {
        return fill;
    }

    public void setFill(Color fill) {
        realFillColor.setColor(fill);
        this.fill = fill;
    }

    public Color getFocusedFill() {
        return focusedFill;
    }

    public void setFocusedFill(Color focusedFill) {
        this.focusedFill = focusedFill;
    }

    public String getBackgroundText() {
        return backgroundText;
    }

    public void setBackgroundText(String backgroundText) {
        this.backgroundText = backgroundText;
    }

    private boolean isDrawingLine = true;

    public void setDrawingLine(boolean drawingLine) {
        isDrawingLine = drawingLine;
    }

    public boolean isDrawingBackground() {
        return drawingBackground;
    }

    public void setDrawingBackground(boolean drawingBackground) {
        this.drawingBackground = drawingBackground;
    }

    public boolean isFocused() {
        return isFocused;
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
    }

    public boolean isPassword() {
        return password;
    }

    public void setPassword(boolean password) {
        this.password = password;
    }
}