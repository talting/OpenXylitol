package cc.xylitol.ui.hud;

import cc.xylitol.Client;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.ui.font.FontManager;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjglx.input.Mouse;

import java.awt.*;

public abstract class HUD extends Gui{
    public final static Minecraft mc = Minecraft.getMinecraft();
    private final String name;
    @Setter
    public int height;
    public int width;
    public boolean drag = false;
    public Module m;
    public boolean noRect = false;
    public int alpha = 100;
    private int posX = 0;
    private int posY = 0;
    private float dragX, dragY;

    public HUD(int width, int height, String name) {
        Client.instance.eventManager.register(this);
        this.height = height;
        this.width = width;
        this.name = name;
        m = new Module(name, Category.HUD);
//        m.getValues().add(aplha);
    }

    public boolean isHovering(int mouseX, int mouseY) {
        float startX = posX;
        float startY = posY;
        float w = width;
        float h = height;

        if (width < 0) {
            startX += width;
            w = Math.abs(w);
        }

        if (height < 0) {
            startY += height;
            h = Math.abs(h);
        }

        return mouseX >= startX && mouseX <= startX + w && mouseY >= startY && mouseY <= startY + h;
    }

    public int getHeight() {
        return this.height;
    }
    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }
    public abstract void drawShader();
    public abstract void drawHUD(int x, int y, float partialTicks);
    public abstract void predrawhud();
    public abstract void onTick();

    public void doDrag(int mouseX, int mouseY) {
        if (this.drag && m.getState()) {
            if (!Mouse.isButtonDown(0)) {
                this.drag = false;
            }
            this.posX = (int) (mouseX - this.dragX);
            this.posY = (int) (mouseY - this.dragY);
        }
    }

    public void mouseClick(int mouseX, int mouseY, int button) {
        if (isHovering(mouseX, mouseY) && m.getState()) {
            if (button == 1) {
                m.toggle();
            }
            if (button == 0) {
                this.drag = true;
                this.dragX = mouseX - this.posX;
                this.dragY = mouseY - this.posY;
            }
        }
    }

    public void draw(float partialTicks) {
        GlStateManager.resetColor();
        drawHUD(posX, posY, partialTicks);
    }

    public void predraw() {
        GlStateManager.resetColor();
        predrawhud();
    }

    public void renderTag() {
        if (!m.getState()) return;

        // Right layout
        float textX = posX;
        float textY = posY;

        if (width < 0) {
            textX += width;
        }

        if (height < 0) {
            textY += height;
        }

        FontManager.font16.drawString(name, textX, textY - 2 - FontManager.font16.getHeight(), new Color(255, 255, 255, alpha).getRGB());
    }

    public boolean isDrag() {
        return drag;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getName() {
        return name;
    }
}
