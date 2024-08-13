package cc.xylitol.ui.gui.main;

import cc.xylitol.Client;
import cc.xylitol.ui.font.FontManager;
import cc.xylitol.ui.gui.alt.GuiAltManager;
import cc.xylitol.utils.render.ParticleUtils;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.utils.render.RoundedUtil;
import cc.xylitol.utils.render.animation.Animation;
import cc.xylitol.utils.render.animation.Direction;
import cc.xylitol.utils.render.animation.impl.DecelerateAnimation;
import cc.xylitol.utils.render.animation.impl.LayeringAnimation;
import cc.xylitol.utils.render.shader.GaussianBlur;
import cc.xylitol.utils.render.shader.KawaseBloom;
import cc.xylitol.utils.render.shader.KawaseBlur;
import cc.xylitol.utils.render.shader.ShaderElement;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Skid by Paimon.
 *
 * @Date 2024/1/30
 */
public class CustomMainMenu extends GuiScreen {
//    private float trainX;
    private Animation displayAnimation;

    private final List<CustomMenuButton> buttons;

    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

    public CustomMainMenu() {
//        trainX = -width;
        displayAnimation = new DecelerateAnimation(1000, 1);

        buttons = Arrays.asList(
                new CustomMenuButton("Single"),
                new CustomMenuButton("Multi"),
                new CustomMenuButton("Alt"),
                new CustomMenuButton("Option"),
                new CustomMenuButton("Exit")
        );

    }

    @Override
    public void initGui() {
        displayAnimation.setDirection(Direction.FORWARDS);
        buttons.forEach(CustomMenuButton::initGui);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        float buttonWidth = 50;
        float buttonHeight = 25;

        float totalButtonWidth = buttonWidth * 5 + 5 * 4;

        int count = 0;

        float midX = width / 2F;

        FontManager.font40.drawString("Xylitol Menu.", midX - totalButtonWidth / 2F - 5, ((height / 2f - buttonHeight / 2f - 60)), new Color(255, 255, 255, (int) (255 * displayAnimation.getOutput())).getRGB());

        stencilFramebuffer = ShaderElement.createFrameBuffer(stencilFramebuffer);
        stencilFramebuffer.framebufferClear();
        stencilFramebuffer.bindFramebuffer(false);
        RoundedUtil.drawRound(midX - totalButtonWidth / 2F - 5, ((height / 2f - buttonHeight / 2f) - 30), 280, buttonHeight + 10, 6f, Color.WHITE);
        stencilFramebuffer.unbindFramebuffer();
        KawaseBlur.renderBlur(stencilFramebuffer.framebufferTexture, 3, 1);

        KawaseBloom.shadow(() -> RoundedUtil.drawRound(midX - totalButtonWidth / 2F - 5, ((height / 2f - buttonHeight / 2f) - 30), 280, buttonHeight + 10, 6f, new Color(0, 0, 0, (int) (220 * displayAnimation.getOutput()))), 2, 1);

        RoundedUtil.drawRound(midX - totalButtonWidth / 2F - 5, ((height / 2f - buttonHeight / 2f) - 30), 280, buttonHeight + 10, 6f, new Color(32, 32, 32, (int) (80 * displayAnimation.getOutput())));

        for (CustomMenuButton button : buttons) {
            button.x = midX - totalButtonWidth / 2 + count;
            button.y = ((height / 2f - buttonHeight / 2f) - 25);
            button.width = buttonWidth;
            button.height = buttonHeight;
            button.clickAction = () -> {
                switch (button.text) {
                    case "Single": {
                        LayeringAnimation.play(new GuiSelectWorld(this));
                    }
                    break;
                    case "Multi": {
                        LayeringAnimation.play(new GuiMultiplayer(this));
                    }
                    break;
                    case "Option": {
                        LayeringAnimation.play(new GuiOptions(this, mc.gameSettings));
                    }
                    break;
                    case "Alt": {
                        LayeringAnimation.play(new GuiAltManager(this));
                    }
                    break;
                    case "Exit": {
                        mc.shutdown();
                    }
                    break;
                }
            };
            button.drawScreen(mouseX, mouseY, partialTicks);
            count += buttonWidth + 5;
        }


        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        displayAnimation.setDirection(Direction.BACKWARDS);

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == -1)
        {
            this.mc.displayGuiScreen(null);

        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        buttons.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
