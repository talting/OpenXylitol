package cc.xylitol.ui.hud.impl;

import cc.xylitol.ui.font.FontManager;
import cc.xylitol.ui.hud.HUD;
import cc.xylitol.utils.render.ColorUtil;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.utils.render.shader.ShaderElement;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;

import java.awt.*;

import static cc.xylitol.module.impl.render.HUD.color;

public class Inventory extends HUD {

    public Inventory() {
        super(200, 100, "Inventory");
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
        setWidth(174);
        setHeight(80);
        boolean hasStacks = false;
        GlStateManager.pushMatrix();

        float y = yPos + 6f;

        ShaderElement.addBlurTask(() -> RenderUtil.drawRectWH(xPos, yPos + 1f, getWidth(), getHeight(), new Color(0, 0, 0, 255).getRGB()));
        ShaderElement.addBloomTask(() -> RenderUtil.drawRectWH(xPos, yPos + 1f, getWidth(), getHeight(), new Color(0, 0, 0, 255).getRGB()));

        RenderUtil.drawRectWH(xPos, yPos, getWidth(), 1f, cc.xylitol.module.impl.render.HUD.color(1).getRGB());
        RenderUtil.drawRectWH(xPos, yPos + 1f, getWidth(), getHeight(), new Color(0, 0, 0, 100).getRGB());

        FontManager.tenacitybold.drawStringDynamic("Inventory", xPos + 6f, y, 1,6);


        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();
        for (int i1 = 9; i1 < mc.thePlayer.inventoryContainer.inventorySlots.size() - 9; ++i1) {
            Slot slot = mc.thePlayer.inventoryContainer.inventorySlots.get(i1);
            if (slot.getHasStack()) hasStacks = true;
            int i = slot.xDisplayPosition;
            int j = slot.yDisplayPosition;
            mc.getRenderItem().renderItemAndEffectIntoGUI(slot.getStack(), (int) (xPos+2f + i - 4), (int) (y + j - 70));
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, slot.getStack(), xPos+2 + i - 4, (int) (y + j - 70), null);
        }

        if (!hasStacks) {
            FontManager.font20.drawString("Empty",
                    xPos + width / 2 - FontManager.font20.getStringWidth("Empty") / 2,
                    y + height / 2 - 5,
                    Color.WHITE.getRGB());
        }


        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
    }

}
