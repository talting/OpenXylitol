package cc.xylitol.module.impl.render;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventRender2D;
import cc.xylitol.event.impl.events.EventUpdate;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.render.ColorUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

public final class Health extends Module {
    public Health() {
        super("Health", Category.Render);
    }

    private final DecimalFormat decimalFormat = new DecimalFormat("0.#", new DecimalFormatSymbols(Locale.ENGLISH));
    private final Random random = new Random();
    private int width;

    @EventTarget
    public void onRenderGuiEvent(EventUpdate event) {
        if (mc.currentScreen instanceof GuiInventory
                || mc.currentScreen instanceof GuiChest
                || mc.currentScreen instanceof GuiContainerCreative) {
            this.renderHealth();
        }
    }

    @EventTarget
    public void onRender2DEvent(EventRender2D event) {
        if (!(mc.currentScreen instanceof GuiInventory) && !(mc.currentScreen instanceof GuiChest)) {
            this.renderHealth();
        }
    }

    private void renderHealth() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        GuiScreen screen = mc.currentScreen;
        float absorptionHealth = mc.thePlayer.getAbsorptionAmount();
        String string = this.decimalFormat.format(mc.thePlayer.getHealth() / 2.0f) + "§c\u2764 " + (absorptionHealth <= 0.0f ? "" : "§e" + this.decimalFormat.format(absorptionHealth / 2.0f) + "§6\u2764");
        int offsetY = 0;
        if (mc.thePlayer.getHealth() >= 0.0f && mc.thePlayer.getHealth() < 10.0f || mc.thePlayer.getHealth() >= 10.0f && mc.thePlayer.getHealth() < 100.0f) {
            this.width = 3;
        }
        if (screen instanceof GuiInventory) {
            offsetY = 70;
        } else if (screen instanceof GuiContainerCreative) {
            offsetY = 80;
        } else if (screen instanceof GuiChest) {
            offsetY = ((GuiChest) screen).ySize / 2 - 15;
        }
        int x = new ScaledResolution(mc).getScaledWidth() / 2 - this.width;
        int y = new ScaledResolution(mc).getScaledHeight() / 2 + 25 + offsetY;
        Color color = ColorUtil.blendColors(new float[]{0.0f, 0.5f, 1.0f}, new Color[]{new Color(255, 37, 0), Color.YELLOW, Color.GREEN}, mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth());
        mc.fontRendererObj.drawString(string, absorptionHealth > 0.0f ? (float) x - 15.5f : (float) x - 3.5f, y, color.getRGB(), true);
        GL11.glPushMatrix();
        mc.getTextureManager().bindTexture(Gui.icons);
        this.random.setSeed((long) mc.ingameGUI.updateCounter * 312871L);
        float width = (float) scaledResolution.getScaledWidth() / 2.0f - mc.thePlayer.getMaxHealth() / 2.5f * 10.0f / 2.0f;
        float maxHealth = mc.thePlayer.getMaxHealth();
        int lastPlayerHealth = mc.ingameGUI.lastPlayerHealth;
        int healthInt = MathHelper.ceiling_float_int(mc.thePlayer.getHealth());
        int l2 = -1;
        boolean flag = mc.ingameGUI.healthUpdateCounter > (long) mc.ingameGUI.updateCounter && (mc.ingameGUI.healthUpdateCounter - (long) mc.ingameGUI.updateCounter) / 3L % 2L == 1L;
        if (mc.thePlayer.isPotionActive(Potion.regeneration)) {
            l2 = mc.ingameGUI.updateCounter % MathHelper.ceiling_float_int(maxHealth + 5.0f);
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        for (int i6 = MathHelper.ceiling_float_int(maxHealth / 2.0f) - 1; i6 >= 0; --i6) {
            int xOffset = 16;
            if (mc.thePlayer.isPotionActive(Potion.poison)) {
                xOffset += 36;
            } else if (mc.thePlayer.isPotionActive(Potion.wither)) {
                xOffset += 72;
            }
            int k3 = 0;
            if (flag) {
                k3 = 1;
            }
            float renX = width + (float) (i6 % 10 * 8);
            float renY = (float) scaledResolution.getScaledHeight() / 2.0f + 15.0f + (float) offsetY;
            if (healthInt <= 4) {
                renY += (float) this.random.nextInt(2);
            }
            if (i6 == l2) {
                renY -= 2.0f;
            }
            int yOffset = 0;
            if (mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
                yOffset = 5;
            }
            Gui.drawTexturedModalRect(renX, renY, 16 + k3 * 9, 9 * yOffset, 9, 9);
            if (flag) {
                if (i6 * 2 + 1 < lastPlayerHealth) {
                    Gui.drawTexturedModalRect(renX, renY, xOffset + 54, 9 * yOffset, 9, 9);
                }
                if (i6 * 2 + 1 == lastPlayerHealth) {
                    Gui.drawTexturedModalRect(renX, renY, xOffset + 63, 9 * yOffset, 9, 9);
                }
            }
            if (i6 * 2 + 1 < healthInt) {
                Gui.drawTexturedModalRect(renX, renY, xOffset + 36, 9 * yOffset, 9, 9);
            }
            if (i6 * 2 + 1 != healthInt) continue;
            Gui.drawTexturedModalRect(renX, renY, xOffset + 45, 9 * yOffset, 9, 9);
        }
        GL11.glPopMatrix();
    }
}
