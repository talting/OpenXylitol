package cc.xylitol.ui.gui.main;

import cc.xylitol.ui.font.FontManager;
import cc.xylitol.ui.font.RapeMasterFontManager;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.utils.render.RoundedUtil;
import cc.xylitol.utils.render.animation.Animation;
import cc.xylitol.utils.render.animation.Direction;
import cc.xylitol.utils.render.animation.impl.DecelerateAnimation;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
@Getter
@Setter

public class CustomMenuButton extends GuiScreen {

    public final String text;
    private Animation displayAnimation;

    private Animation hoverAnimation = new DecelerateAnimation(500, 1);;
    public float x, y, width, height;
    public Runnable clickAction;
    public RapeMasterFontManager font = FontManager.font20;

    public CustomMenuButton(String text,Runnable clickAction) {
        this.text = text;
        displayAnimation = new DecelerateAnimation(1000, 255);
        font = FontManager.font20;
        this.clickAction = clickAction;
    }

    public CustomMenuButton(String text) {
        this.text = text;
        displayAnimation = new DecelerateAnimation(1000, 255);
        font = FontManager.font20;
    }

    @Override
    public void initGui() {
        hoverAnimation = new DecelerateAnimation(500, 1);
        displayAnimation.setDirection(Direction.FORWARDS);
//        font = FontManager.font20;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float ticks) {
        boolean hovered = RenderUtil.isHovering(x, y, width, height, mouseX, mouseY);
        hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        Color rectColor = new Color(32, 32, 32, (int) (displayAnimation.getOutput() * Math.max(0.7, hoverAnimation.getOutput())));
//        rectColor = ColorUtil.interpolateColorC(rectColor, ColorUtil.darker(rectColor, .1f), (float) hoverAnimation.getOutput());
        RoundedUtil.drawRound(x, y, width, height, 4, rectColor);
        font.drawCenteredString(text, x + width / 2f, y + font.getMiddleOfBox(height) + 2f, new Color(255, 255, 255, (int) displayAnimation.getOutput()).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovered = RenderUtil.isHovering(x, y, width, height, mouseX, mouseY);
        if (hovered) clickAction.run();
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
    }

    @Override
    public void onGuiClosed() {
        displayAnimation.setDirection(Direction.BACKWARDS);
    }
}