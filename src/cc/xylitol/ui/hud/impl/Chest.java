package cc.xylitol.ui.hud.impl;

import cc.xylitol.ui.font.FontManager;
import cc.xylitol.ui.hud.HUD;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.utils.render.shader.ShaderElement;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;

import java.awt.*;

public class Chest extends HUD {

    public Chest() {
        super(200, 100, "Chest");
    }

    @Override
    public void drawShader() {

    }

    @Override
    public void predrawhud() {

    }

    @Override
    public void onTick() {

    }

    @Override
    public void drawHUD(int xPos, int yPos, float partialTicks) {
        if (!(mc.thePlayer.openContainer instanceof ContainerChest)) return;

        ContainerChest container = (ContainerChest) mc.thePlayer.openContainer;

        setWidth(174);
        setHeight(80);
        float y = yPos + 6f;

        ShaderElement.addBlurTask(() -> RenderUtil.drawRectWH(xPos, yPos + 1f, getWidth(), getHeight(), new Color(0, 0, 0, 255).getRGB()));
        ShaderElement.addBloomTask(() -> RenderUtil.drawRectWH(xPos, yPos + 1f, getWidth(), getHeight(), new Color(0, 0, 0, 255).getRGB()));

        RenderUtil.drawRectWH(xPos, yPos, getWidth(), 1f, cc.xylitol.module.impl.render.HUD.color(1).getRGB());
        RenderUtil.drawRectWH(xPos, yPos + 1f, getWidth(), getHeight(), new Color(0, 0, 0, 100).getRGB());

        FontManager.tenacitybold.drawString("Chest", xPos + 6f, y, cc.xylitol.module.impl.render.HUD.color(1).getRGB());

        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();

        for (int i1 = 0; i1 < container.inventorySlots.size() - 36; ++i1) {
            Slot slot = container.inventorySlots.get(i1);
            int i = slot.xDisplayPosition;
            int j = slot.yDisplayPosition;
            mc.getRenderItem().renderItemAndEffectIntoGUI(slot.getStack(), (int) (xPos + 2f + i - 4), (int) (y + j) - 3);
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, slot.getStack(), xPos + 2 + i - 3, (int) (y + j) - 3, null);
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
    }

}
