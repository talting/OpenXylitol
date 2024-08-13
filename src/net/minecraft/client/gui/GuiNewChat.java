package net.minecraft.client.gui;

import cc.xylitol.utils.render.animation.Direction;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GuiNewChat extends Gui {
    private static final Logger logger = LogManager.getLogger();
    private final Minecraft mc;
    private final List<String> sentMessages = Lists.<String>newArrayList();
    private final List<ChatLine> chatLines = Lists.<ChatLine>newArrayList();
    private final List<ChatLine> drawnChatLines = Lists.<ChatLine>newArrayList();
    private int scrollPos;
    private boolean isScrolled;
    public final List<Runnable> blur = new ArrayList<>();
    public final List<Runnable> shadow = new ArrayList<>();
    public final List<Runnable> texts = new ArrayList<>();

    public GuiNewChat(Minecraft mcIn) {
        this.mc = mcIn;
    }

    public void drawChat(int updateCounter) {
        this.blur.clear();
        this.shadow.clear();
        this.texts.clear();

        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
//            HUD hud = Client.instance.getModuleManager().getModule(HUD.class);
//            boolean fancyChatEnabled = hud.fancyChat.get();

            int lineCount = this.getLineCount();
            boolean chatOpened = false;
            int chatLineCount = 0;
            int chatLineToDrawCount = this.drawnChatLines.size();
            float opacity = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;

            if (chatLineToDrawCount > 0) {
                if (this.getChatOpen()) {
                    chatOpened = true;
                }

                float chatScale = this.getChatScale();
                int renderWidth = MathHelper.ceiling_float_int(this.getChatWidth() / chatScale);
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0F, 20.0F, 0.0F);
                GlStateManager.scale(chatScale, chatScale, 1.0F);

                for (int i = 0; i + this.scrollPos < this.drawnChatLines.size() && i < lineCount; ++i) {
                    ChatLine chatLine = this.drawnChatLines.get(i + this.scrollPos);

                    if (chatLine != null) {
                        int elapsedTicks = updateCounter - chatLine.getUpdatedCounter();

                        if (elapsedTicks >= 200) {
                            chatLine.xAnimation.setDirection(Direction.BACKWARDS);
                        }

                        if (!chatLine.xAnimation.finished(Direction.BACKWARDS) || chatOpened) {
                            double fadePercent = (1.0D - (elapsedTicks / 200.0D)) * 10.0D;
                            fadePercent = MathHelper.clamp_double(fadePercent, 0.0D, 1.0D);
                            fadePercent = fadePercent * fadePercent;
                            int alpha = (int) (255 * fadePercent);

                            if (chatOpened) {
                                alpha = 255;
                            }

                            alpha = (int) (alpha * opacity);
                            ++chatLineCount;

                            int renderPosY = -i * 9;

                            String message = getReplacedMessage(chatLine.getChatComponent());

                            int finalAlpha = alpha;
                            this.texts.add(() -> {
                                drawRect(0, renderPosY - 9, renderWidth + 4, renderPosY, finalAlpha / 2 << 24);

                                GlStateManager.enableBlend();
                                this.mc.fontRendererObj.drawStringWithShadow(message, 0, renderPosY - 8, 16777215 + (finalAlpha << 24));
                                GlStateManager.disableAlpha();
                                GlStateManager.disableBlend();
                            });
                        }
                    }
                }

                // Scroll bar part
                if (chatOpened) {
                    int fontHeight = this.mc.fontRendererObj.FONT_HEIGHT;
                    GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                    int drawnChatLinesHeight = chatLineToDrawCount * fontHeight + chatLineToDrawCount;
                    int chatLinesHeight = chatLineCount * fontHeight + chatLineCount;
                    int scrolledPosY = this.scrollPos * chatLinesHeight / chatLineToDrawCount;
                    int barHeight = chatLinesHeight * chatLinesHeight / drawnChatLinesHeight;

                    if (drawnChatLinesHeight != chatLinesHeight) {
                        int barAlpha = scrolledPosY > 0 ? 170 : 96;
                        int barColor = this.isScrolled ? 13382451 : 3355562;
                        drawRect(0, -scrolledPosY, 2, -scrolledPosY - barHeight, barColor + (barAlpha << 24));
                        drawRect(2, -scrolledPosY, 1, -scrolledPosY - barHeight, 13421772 + (barAlpha << 24));
                    }
                }

                GlStateManager.popMatrix();
            }
        }
    }

    private String getReplacedMessage(IChatComponent chatComponent) {
        String message = chatComponent.getFormattedText();
        String replacedMessage = StringUtils.replace(StringUtils.replace(message, "花雨庭", "Quick Macro"), "MCHYT", "QUICKMACRO");

        return replacedMessage.toString();
    }


    /**
     * Clears the chat.
     */
    public void clearChatMessages() {
        this.drawnChatLines.clear();
        this.chatLines.clear();
        this.sentMessages.clear();
    }

    public void printChatMessage(IChatComponent chatComponent) {
        this.printChatMessageWithOptionalDeletion(chatComponent, 0);
    }

    /**
     * prints the ChatComponent to Chat. If the ID is not 0, deletes an existing Chat Line of that ID from the GUI
     */
    public void printChatMessageWithOptionalDeletion(IChatComponent chatComponent, int chatLineId) {
        this.setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false);
        logger.info("[CHAT] " + chatComponent.getUnformattedText());
    }

    private void setChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
        if (chatLineId != 0) {
            this.deleteChatLine(chatLineId);
        }

        int i = MathHelper.floor_float((float) this.getChatWidth() / this.getChatScale());
        List<IChatComponent> list = GuiUtilRenderComponents.splitText(chatComponent, i, this.mc.fontRendererObj, false, false);
        boolean flag = this.getChatOpen();

        for (IChatComponent ichatcomponent : list) {
            if (flag && this.scrollPos > 0) {
                this.isScrolled = true;
                this.scroll(1);
            }

            this.drawnChatLines.add(0, new ChatLine(updateCounter, ichatcomponent, chatLineId));
        }

        while (this.drawnChatLines.size() > 100) {
            this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
        }

        if (!displayOnly) {
            this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));

            while (this.chatLines.size() > 100) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    public void refreshChat() {
        this.drawnChatLines.clear();
        this.resetScroll();

        for (int i = this.chatLines.size() - 1; i >= 0; --i) {
            ChatLine chatline = this.chatLines.get(i);
            this.setChatLine(chatline.getChatComponent(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true);
        }
    }

    public List<String> getSentMessages() {
        return this.sentMessages;
    }

    /**
     * Adds this string to the list of sent messages, for recall using the up/down arrow keys
     *
     * @param message The message to add in the sendMessage List
     */
    public void addToSentMessages(String message) {
        if (this.sentMessages.isEmpty() || !((String) this.sentMessages.get(this.sentMessages.size() - 1)).equals(message)) {
            this.sentMessages.add(message);
        }
    }

    /**
     * Resets the chat scroll (executed when the GUI is closed, among others)
     */
    public void resetScroll() {
        this.scrollPos = 0;
        this.isScrolled = false;
    }

    /**
     * Scrolls the chat by the given number of lines.
     *
     * @param amount The amount to scroll
     */
    public void scroll(int amount) {
        this.scrollPos += amount;
        int i = this.drawnChatLines.size();

        if (this.scrollPos > i - this.getLineCount()) {
            this.scrollPos = i - this.getLineCount();
        }

        if (this.scrollPos <= 0) {
            this.scrollPos = 0;
            this.isScrolled = false;
        }
    }

    /**
     * Gets the chat component under the mouse
     *
     * @param mouseX The x position of the mouse
     * @param mouseY The y position of the mouse
     */
    public IChatComponent getChatComponent(int mouseX, int mouseY) {
        if (!this.getChatOpen()) {
            return null;
        } else {
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            int i = scaledresolution.getScaleFactor();
            float f = this.getChatScale();
            int j = mouseX / i - 3;
            int k = mouseY / i - 27;
            j = MathHelper.floor_float((float) j / f);
            k = MathHelper.floor_float((float) k / f);

            if (j >= 0 && k >= 0) {
                int l = Math.min(this.getLineCount(), this.drawnChatLines.size());

                if (j <= MathHelper.floor_float((float) this.getChatWidth() / this.getChatScale()) && k < this.mc.fontRendererObj.FONT_HEIGHT * l + l) {
                    int i1 = k / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;

                    if (i1 >= 0 && i1 < this.drawnChatLines.size()) {
                        ChatLine chatline = (ChatLine) this.drawnChatLines.get(i1);
                        int j1 = 0;

                        for (IChatComponent ichatcomponent : chatline.getChatComponent()) {
                            if (ichatcomponent instanceof ChatComponentText) {
                                j1 += this.mc.fontRendererObj.getStringWidth(GuiUtilRenderComponents.func_178909_a(((ChatComponentText) ichatcomponent).getChatComponentText_TextValue(), false));

                                if (j1 > j) {
                                    return ichatcomponent;
                                }
                            }
                        }
                    }

                    return null;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    /**
     * Returns true if the chat GUI is open
     */
    public boolean getChatOpen() {
        return this.mc.currentScreen instanceof GuiChat;
    }

    /**
     * finds and deletes a Chat line by ID
     *
     * @param id The ChatLine's id to delete
     */
    public void deleteChatLine(int id) {
        Iterator<ChatLine> iterator = this.drawnChatLines.iterator();

        while (iterator.hasNext()) {
            ChatLine chatline = (ChatLine) iterator.next();

            if (chatline.getChatLineID() == id) {
                iterator.remove();
            }
        }

        iterator = this.chatLines.iterator();

        while (iterator.hasNext()) {
            ChatLine chatline1 = (ChatLine) iterator.next();

            if (chatline1.getChatLineID() == id) {
                iterator.remove();
                break;
            }
        }
    }

    public int getChatWidth() {
        return calculateChatboxWidth(this.mc.gameSettings.chatWidth);
    }

    public int getChatHeight() {
        return calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
    }

    /**
     * Returns the chatscale from mc.gameSettings.chatScale
     */
    public float getChatScale() {
        return this.mc.gameSettings.chatScale;
    }

    public static int calculateChatboxWidth(float scale) {
        int i = 320;
        int j = 40;
        return MathHelper.floor_float(scale * (float) (i - j) + (float) j);
    }

    public static int calculateChatboxHeight(float scale) {
        int i = 180;
        int j = 20;
        return MathHelper.floor_float(scale * (float) (i - j) + (float) j);
    }

    public int getLineCount() {
        return this.getChatHeight() / 9;
    }
}
