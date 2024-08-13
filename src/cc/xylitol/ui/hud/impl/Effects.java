package cc.xylitol.ui.hud.impl;

import cc.xylitol.ui.font.FontManager;
import cc.xylitol.ui.hud.HUD;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.utils.render.RoundedUtil;
import cc.xylitol.utils.render.animation.Direction;
import cc.xylitol.utils.render.animation.impl.ContinualAnimation;
import cc.xylitol.utils.render.animation.impl.EaseBackIn;
import cc.xylitol.utils.render.shader.ShaderElement;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
//import static cc.xylitol.module.impl.render.HUD.styleValue;

public class Effects extends HUD {
    public Effects() {
        super(150, 100, "Effects");
    }

    private final Map<Integer, Integer> potionMaxDurations = new HashMap<>();
    private final ContinualAnimation widthanimation = new ContinualAnimation();
    private final ContinualAnimation heightanimation = new ContinualAnimation();
    private final EaseBackIn animation = new EaseBackIn(200, 1F, 1.3F);
    List<PotionEffect> effects = new ArrayList<>();

    @Override
    public void drawShader() {

    }


    @Override
    public void onTick() {

    }

    private int maxString = 0;

    @Override
    public void drawHUD(int x, int y, float partialTicks) {
        effects = mc.thePlayer.getActivePotionEffects().stream()
                .sorted(Comparator.comparingInt((PotionEffect it) -> FontManager.font16.getStringWidth(
                        get(it)
                )))
                .collect(Collectors.toList());
        int offsetX = 21;
        int offsetY = 14;
        int i2 = 16;
        final ArrayList<Integer> needRemove = new ArrayList<Integer>();
        for (final Map.Entry<Integer, Integer> entry : this.potionMaxDurations.entrySet()) {
            if (mc.thePlayer.getActivePotionEffect(Potion.potionTypes[entry.getKey()]) == null) {
                needRemove.add(entry.getKey());
            }
        }
        for (final int id : needRemove) {
            this.potionMaxDurations.remove(id);
        }
        for (final PotionEffect effect : effects) {
            if (!this.potionMaxDurations.containsKey(effect.getPotionID()) || this.potionMaxDurations.get(effect.getPotionID()) < effect.getDuration()) {
                this.potionMaxDurations.put(effect.getPotionID(), effect.getDuration());
            }
        }
        float width = !effects.isEmpty() ? Math.max(50 + FontManager.font16.getStringWidth(get(effects.get(effects.size() - 1))), 60 + FontManager.font16.getStringWidth(get(effects.get(effects.size() - 1)))) : 0;
        float height = effects.size() * 25;
        widthanimation.animate(width, 20);
        heightanimation.animate(height, 20);
        if (mc.currentScreen instanceof GuiChat && effects.isEmpty()) {
            animation.setDirection(Direction.FORWARDS);
        } else if (!(mc.currentScreen instanceof GuiChat)) {
            animation.setDirection(Direction.BACKWARDS);
        }
        RenderUtil.scaleStart(x + 50, y + 15, (float) animation.getOutput());
        FontManager.font16.drawStringWithShadow("Potion Example", x + 50F - FontManager.font16.getStringWidth("Potion Example") / 2, y + 15 - FontManager.font16.getHeight() / 2, new Color(255, 255, 255, 60).getRGB());
        RenderUtil.scaleEnd();
        if (effects.isEmpty()) {
            maxString = 0;
        }
        if (!effects.isEmpty()) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            int l = 24;
            RenderUtil.drawRectWH(x, y - 18, getWidth(), 1f, cc.xylitol.module.impl.render.HUD.color(1).getRGB());
            RenderUtil.drawRectWH(x, y - 17, widthanimation.getOutput(), 17, new Color(0, 0, 0, 100).getRGB());

            //blur
            ShaderElement.addBlurTask(() -> RenderUtil.drawRectWH(x, y, (int) widthanimation.getOutput(), (int) heightanimation.getOutput(), new Color(0, 0, 0, 255).getRGB()));
            ShaderElement.addBlurTask(() -> RenderUtil.drawRectWH(x, y - 17, widthanimation.getOutput(), 17, new Color(0, 0, 0, 255).getRGB()));

            ShaderElement.addBloomTask(() -> RenderUtil.drawRectWH(x, y, (int) widthanimation.getOutput(), (int) heightanimation.getOutput(), new Color(0, 0, 0, 255).getRGB()));
            ShaderElement.addBloomTask(() -> RenderUtil.drawRectWH(x, y - 17, widthanimation.getOutput(), 17, new Color(0, 0, 0, 255).getRGB()));

            RenderUtil.drawRectWH(x, y, (int) widthanimation.getOutput(), (int) heightanimation.getOutput(), new Color(0, 0, 0, 100).getRGB());
            FontManager.tenacitybold.drawStringDynamic("Potion", x + 6f, y - 17 + 8, 1, 6);

            RenderUtil.startGlScissor(x, y, (int) widthanimation.getOutput(), (int) heightanimation.getOutput());
            for (PotionEffect potioneffect : effects) {
                Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                if (potion.hasStatusIcon()) {
                    mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                    int i1 = potion.getStatusIconIndex();
                    GlStateManager.enableBlend();
                    RenderUtil.drawImage(new ResourceLocation("xylitol/images/" + (potioneffect.getIsPotionDurationMax() ? "infinite" : "potions/" + potioneffect.getPotionID()) + ".png"), (x + offsetX) - 15, (y + i2) - offsetY + 2, 10, 10, new Color(Potion.potionTypes[potioneffect.getPotionID()].getLiquidColor()).getRGB());
                }

                String s = Potion.getDurationString(potioneffect);
                String s1 = get(potioneffect);
                FontManager.font18.drawString(s1, x + offsetX - 2f, (y + i2) - offsetY + 4, -1);
                RoundedUtil.drawGradientHorizontal(x + 6, (y + i2) - offsetY + 16, (float) ((potioneffect.getDuration() / (1.0f * this.potionMaxDurations.get(potioneffect.getPotionID()))) * (widthanimation.getOutput() - 12)), 2f, 1.5f, cc.xylitol.module.impl.render.HUD.color(1), cc.xylitol.module.impl.render.HUD.color(6));
                i2 += l;
                if (maxString < mc.fontRendererObj.getStringWidth(s1)) {
                    maxString = mc.fontRendererObj.getStringWidth(s1);
                }
            }
            RenderUtil.stopGlScissor();
        }
        if (mc.currentScreen instanceof GuiChat && effects.isEmpty()) {
            setWidth(100);
            setHeight(30);
        } else {
            setWidth((int) widthanimation.getOutput());
            setHeight((int) heightanimation.getOutput());

        }
    }

    final ContinualAnimation heightAnimation = new ContinualAnimation();

    @Override
    public void predrawhud() {

    }

    private String get(PotionEffect potioneffect) {
        Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
        String s1 = I18n.format(potion.getName(), new Object[0]);
        s1 = s1 + " " + intToRomanByGreedy(potioneffect.getAmplifier() + 1);
        return s1;
    }

    private String intToRomanByGreedy(int num) {
        final int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        final String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < values.length && num >= 0; i++)
            while (values[i] <= num) {
                num -= values[i];
                stringBuilder.append(symbols[i]);
            }

        return stringBuilder.toString();
    }
}
