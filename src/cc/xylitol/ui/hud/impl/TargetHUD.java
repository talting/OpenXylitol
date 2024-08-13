package cc.xylitol.ui.hud.impl;

import cc.xylitol.Client;
import cc.xylitol.module.impl.combat.KillAura;
import cc.xylitol.ui.font.FontManager;
import cc.xylitol.ui.hud.HUD;
import cc.xylitol.utils.math.MathUtils;
import cc.xylitol.utils.render.*;
import cc.xylitol.utils.render.animation.Animation;
import cc.xylitol.utils.render.animation.Direction;
import cc.xylitol.utils.render.animation.impl.ContinualAnimation;
import cc.xylitol.utils.render.animation.impl.DecelerateAnimation;
import cc.xylitol.utils.render.shader.ShaderElement;
import cc.xylitol.value.impl.ModeValue;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static cc.xylitol.module.impl.render.HUD.color;
import static cc.xylitol.utils.render.RenderUtil.drawBigHead;

public class TargetHUD extends HUD {
    public TargetHUD() {
        super(150, 100, "TargetHUD");
    }

    public ModeValue mode = new ModeValue("Mode", new String[]{"Xylitol", "Exire", "Exhibition", "Tenacity", "Akrien", "Raven"}, "Xylitol");

    private NumberValue bgAlpha = new NumberValue("Background Alpha", 100.0, 0.0, 255.0, 1.0, () -> mode.is("Novoline Fky"));
    private final ContinualAnimation animation2 = new ContinualAnimation();
    private final Animation openAnimation = new DecelerateAnimation(175, .5);
    private final DecimalFormat DF_1 = new DecimalFormat("0.0");

    private EntityLivingBase target;

    @Override
    public void drawShader() {

    }


    @Override
    public void onTick() {

    }

    @Override
    public void drawHUD(int xPos, int yPos, float partialTicks) {
        GlStateManager.pushMatrix();
        if (!mode.is("Raven")) RenderUtil.scaleStart(xPos + getWidth() / 2f, yPos + getHeight() / 2f,
                (float) (.5 + openAnimation.getOutput()));
        float alpha = (float) Math.min(1, openAnimation.getOutput() * 2);
        if (target != null) {
            render(xPos, yPos, alpha, target, false);
        }
        if (!mode.is("Raven")) RenderUtil.scaleEnd();
        GlStateManager.popMatrix();
    }

    @Override
    public void predrawhud() {

        KillAura killAura = Client.instance.moduleManager.getModule(KillAura.class);

        if (!(mc.currentScreen instanceof GuiChat)) {
            if (!killAura.getState()) {
                openAnimation.setDirection(Direction.BACKWARDS);
            }

            if (target == null && KillAura.target != null) {
                target = KillAura.target;
                openAnimation.setDirection(Direction.FORWARDS);

            } else if (KillAura.target == null /*|| target != KillAura.target*/) {
                openAnimation.setDirection(Direction.BACKWARDS);
            } else if (target != KillAura.target) {
                target = KillAura.target;
            }

            if (openAnimation.finished(Direction.BACKWARDS)) {
                target = null;
            }
        } else {
            openAnimation.setDirection(Direction.FORWARDS);
            target = mc.thePlayer;
        }

    }


    public void render(float x, float y, float alpha, EntityLivingBase target, boolean blur) {
        GlStateManager.pushMatrix();
        switch (mode.getValue().toLowerCase()) {
            case "xylitol": {
                setWidth(Math.max(120, FontManager.font18.getStringWidth(target.getName()) + 70));
                setHeight((int) 39.5F);
                double healthPercentage = MathHelper.clamp_float((target.animatedHealthBar + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);
                Color bg = new Color(0, 0, 0, (int) (100 * alpha));
                float hurtPercent = target.hurtTime / 10F;
                float scale;
                if (hurtPercent == 0f) {
                    scale = 1f;
                } else if (hurtPercent < 0.5f) {
                    scale = 1 - (0.1f * hurtPercent * 2);
                } else {
                    scale = 0.9f + (0.1f * (hurtPercent - 0.5f) * 2);
                }

                // Draw background
                RoundedUtil.drawRound((int) x, (int) y, (int) (getWidth()), (int) (39.5F), 4f, bg);
                // render blur
                if (openAnimation.isDone()) {
                    ShaderElement.addBlurTask(() -> {
                        RoundedUtil.drawRound((int) x, (int) y, (int) (getWidth()), (int) (39.5F), 4f, Color.BLACK);
                    });
                    ShaderElement.addBloomTask(() -> {
                        RoundedUtil.drawRound((int) x, (int) y, (int) (getWidth()), (int) (39.5F), 4f, Color.BLACK);
                    });

                }
                // damage anim
                float endWidth = (float) Math.max(0, (getWidth() - 44) * healthPercentage);
                animation2.animate(endWidth, 18);
                if (animation2.getOutput() > 0) {
                    RoundedUtil.drawGradientHorizontal((float) (x + 32 + 2.5), y + 29, 1.5f + animation2.getOutput(), 2.5f, 2f, ColorUtil.applyOpacity(color(1), alpha), ColorUtil.applyOpacity(color(6), alpha));
                }

                // Draw head
                GlStateManager.pushMatrix();
                RenderUtil.setAlphaLimit(0);
                int textColor = ColorUtil.applyOpacity(-1, alpha);
                if (target instanceof AbstractClientPlayer) {
                    RenderUtil.color(textColor);
                    float f = 0.8125F;
                    GlStateManager.scale(f, f, f);
                    RenderUtil.scaleStart(x / f + 6 + 16, y / f + 8 + 16, scale);
//                    GL11.glColor4f(1F, 1F - hurtPercent, 1F - hurtPercent, alpha);
                    RenderUtil.drawBigHeadRound(x / f + 6, y / f + 8, 32, 32, alpha, (AbstractClientPlayer) target);
                    RenderUtil.scaleEnd();
                }
                GlStateManager.popMatrix();
                if (!(openAnimation.getDirection() == Direction.BACKWARDS)) {
                    // Draw name
                    FontManager.font18.drawStringDynamic("name: ", x + 32 + 2f, y + 7, 1, 6, alpha);
                    FontManager.font18.drawString(target.getName(), x + 32 + 2f + FontManager.font18.getStringWidth("name: "), y + 7, textColor);

                    FontManager.font18.drawStringDynamic("health: ", x + 32 + 2f, y + 18, 1, 6, alpha);
                    FontManager.font18.drawString(DF_1.format(target.animatedHealthBar) + "hp", x + 32 + 2f + FontManager.font18.getStringWidth("health: "), y + 18, textColor);
                }
                float delta = RenderUtil.deltaTime;
                target.animatedHealthBar += ((target.getHealth() - target.animatedHealthBar) / Math.pow(2.0F, 10.0F - 4f)) * delta;

                break;
            }


            case "exhibition": {
                GlStateManager.pushMatrix();
                this.width = (int) (FontManager.font18.getStringWidth(target.getName()) > 70.0f ? (double) (125.0f + FontManager.font18.getStringWidth(target.getName()) - 70.0f) : 125.0);
                this.height = 45;
                GlStateManager.translate(x, y + 6, 0.0f);
                RenderUtil.skeetRect(0, -2.0, FontManager.font18.getStringWidth(target.getName()) > 70.0f ? (double) (124.0f + FontManager.font18.getStringWidth(target.getName()) - 70.0f) : 124.0, 38.0, 1.0);
                RenderUtil.skeetRectSmall(0.0f, -2.0f, 124.0f, 38.0f, 1.0);
                FontManager.font18.drawStringWithShadow(target.getName(), 41f, 0.3f, -1);
                final float health = target.getHealth();
                final float healthWithAbsorption = target.getHealth() + target.getAbsorptionAmount();
                final float progress = health / target.getMaxHealth();
                final Color healthColor = health >= 0.0f ? ColorUtil.getBlendColor(target.getHealth(), target.getMaxHealth()).brighter() : Color.RED;
                double cockWidth = 0.0;
                cockWidth = MathUtils.round(cockWidth, (int) 5.0);
                if (cockWidth < 50.0) {
                    cockWidth = 50.0;
                }
                final double healthBarPos = cockWidth * (double) progress;
                Gui.drawRect(42.5, 10.3, 53.0 + healthBarPos + 0.5, 13.5, healthColor.getRGB());
                if (target.getAbsorptionAmount() > 0.0f) {
                    Gui.drawRect(97.5 - (double) target.getAbsorptionAmount(), 10.3, 103.5, 13.5, new Color(137, 112, 9).getRGB());
                }
                RenderUtil.drawBorderedRect2(42.0, 9.8f, 54.0 + cockWidth, 14.0, 0.5f, 0, Color.BLACK.getRGB());
                for (int dist = 1; dist < 10; ++dist) {
                    final double cock = cockWidth / 8.5 * (double) dist;
                    Gui.drawRect(43.5 + cock, 9.8, 43.5 + cock + 0.5, 14.0, Color.BLACK.getRGB());
                }
                GlStateManager.scale(0.5, 0.5, 0.5);
                final int distance = (int) mc.thePlayer.getDistanceToEntity(target);
                final String nice = "HP: " + (int) healthWithAbsorption + " | Dist: " + distance;
                mc.fontRendererObj.drawString(nice, 85.3f, 32.3f, -1, true);
                GlStateManager.scale(2.0, 2.0, 2.0);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                if (target != null) drawEquippedShit(28, 20, target);
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.scale(0.31, 0.31, 0.31);
                GlStateManager.translate(73.0f, 102.0f, 40.0f);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                drawModel(target.rotationYaw, target.rotationPitch, target);
                GlStateManager.popMatrix();
                break;
            }

            case "exire": {
                Color firstColor = color(1);
                Color secondColor = color(6);
                float hurtPercent;
                hurtPercent = target.hurtTime / 10F;
                setWidth((int) Math.max(70f, FontManager.font18.getStringWidth(target.getName() + "") + 64));
                setHeight(32);
                RenderUtil.drawRectWH(x, y, (float) getWidth(), (float) getHeight(), new Color(19, 19, 19, (int) (220 * alpha)).getRGB());
                if (!(openAnimation.getDirection() == Direction.BACKWARDS)) {
                    FontManager.font24.drawStringWithShadow(target.getName(), (float) (x + 32), (float) (y + 6), Color.WHITE.getRGB());
                    RenderUtil.drawRectWH((x + 33f), (y + 22f), (getWidth() - 38f), 6f, new Color(0, 0, 0, (int) (230 * alpha)).getRGB());
                    RoundedUtil.drawGradientCornerLR((float) (x + 33f), (float) (y + 23f), (float) ((target.animatedHealthBar / target.getMaxHealth()) * (getWidth() - 38f)), 4f, 0f, firstColor, secondColor);
                }
                int textColor;
                float f;
                textColor = ColorUtil.applyOpacity(-1, alpha);
                if (target instanceof AbstractClientPlayer) {
                    if (!(openAnimation.getDirection() == Direction.BACKWARDS)) {
                        GlStateManager.pushMatrix();
                        RenderUtil.color(textColor);
                        f = 0.8125F;
                        GlStateManager.scale(f, f, f);
                        GL11.glColor4f(1F, 1F - hurtPercent, 1F - hurtPercent, 1F);
                        drawBigHead(x / f + 3, y / f + 3.5f, 32, 32, (AbstractClientPlayer) target);
                        GlStateManager.popMatrix();
                        GlStateManager.resetColor();
                    }
                }
                target.animatedHealthBar += ((target.getHealth() - target.animatedHealthBar) / Math.pow(2.0F, 10.0F - 4f)) * RenderUtil.deltaTime;
                break;
            }

            case "tenacity": {
                double blockrate = 0;
                setWidth(Math.max(145, FontManager.font20.getStringWidth(target.getName() + " " + (int) (blockrate * 100) + "%") + 40));
                setHeight(37);

                Color c1 = ColorUtil.applyOpacity(color(1), alpha);
                Color c2 = ColorUtil.applyOpacity(color(6), alpha);
                Color color = new Color(20, 18, 18, (int) (90 * alpha));

                int textColor = ColorUtil.applyOpacity(-1, alpha);

                RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 4, color);

                float finalX2 = x;

                if (openAnimation.isDone()) {
                    ShaderElement.addBlurTask(() -> RoundedUtil.drawRound(finalX2, y, getWidth(), getHeight(), 4, Color.BLACK));
                    ShaderElement.addBloomTask(() -> RoundedUtil.drawRound(finalX2, y, getWidth(), getHeight(), 4, Color.BLACK));
                }

                if (target instanceof AbstractClientPlayer) {
                    textColor = ((EntityPlayer) target).isBlocking() ? Color.RED.getRGB() : ColorUtil.applyOpacity(-1, alpha);
                    blockrate = ((EntityPlayer) target).getBlockRate(2);
                    StencilUtil.write(false);
                    RenderUtil.renderRoundedRect(x + 3, y + 3, 31, 31, 4, -1);
                    StencilUtil.erase(true);
                    RenderUtil.color(-1, alpha);
                    renderPlayer2D(x + 3, y + 3, 31, 31, (AbstractClientPlayer) target);
                    StencilUtil.dispose();
                } else {
                    if (!(openAnimation.getDirection() == Direction.BACKWARDS)) {
                        FontManager.bold32.drawCenteredStringWithShadow("?", x + 19, y + 20 - FontManager.bold32.getHeight() / 2f, textColor);
                    }
                }

                if (!(openAnimation.getDirection() == Direction.BACKWARDS)) {
                    FontManager.font20.drawStringWithShadow(target.getName() + " " + (int) (blockrate * 100) + "%", x + 39, y + 7, textColor);
                }

                float healthPercent = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);
                float realHealthWidth = getWidth() - 44;
                float realHealthHeight = 3;
                animation2.animate(realHealthWidth * healthPercent, 18);
                Color backgroundHealthColor = new Color(0, 0, 0, ((int) alpha * 110));

                float healthWidth = animation2.getOutput();

                RoundedUtil.drawRound(x + 39, (y + getHeight() - 12), 98, realHealthHeight, 1.5f, backgroundHealthColor);
                RoundedUtil.drawGradientHorizontal(x + 39, (y + getHeight() - 12), healthWidth, realHealthHeight, 1.5f, c1, c2);

                String healthText = (int) MathUtils.round(healthPercent * 100, .01) + "%";
                if (!(openAnimation.getDirection() == Direction.BACKWARDS)) {
                    FontManager.font16.drawStringWithShadow(healthText, x + 34 + Math.min(Math.max(1, healthWidth), realHealthWidth - 11), y + getHeight() - (10 + FontManager.font16.getHeight()), ColorUtil.applyOpacity(-1, alpha));
                }
                break;
            }

            case "akrien": {
                setWidth(Math.max(100, FontManager.font20.getStringWidth(target.getName()) + 45));
                setHeight((int) 39.5F);

                double healthPercentage = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);
                int bg = new Color(0, 0, 0, 0.4F * alpha).getRGB();

                // Draw background
                Gui.drawRect(x, y, x + getWidth(), y + 39.5F, bg);
                if (openAnimation.isDone()) {

                    ShaderElement.addBlurTask(() -> {
                        Gui.drawRect(x, y, x + getWidth(), y + 39.5F, Color.BLACK.getRGB());
                    });
                    ShaderElement.addBloomTask(() -> {
                        Gui.drawRect(x, y, x + getWidth(), y + 39.5F, Color.BLACK.getRGB());
                    });
                }
                // Draw health bar
                Gui.drawRect2(x + 2.5, y + 31, getWidth() - 4.5, 2.5, bg);
                Gui.drawRect2(x + 2.5, y + 34.5, getWidth() - 4.5, 2.5, bg);

                // damage anim
                float endWidth = (float) Math.max(0, (getWidth() - 3.5) * healthPercentage);
                animation2.animate(endWidth, 18);

                if (animation2.getOutput() > 0) {
                    RenderUtil.drawGradientRectBordered(x + 2.5, y + 31, x + 1.5 + animation2.getOutput(), y + 33.5, 0.74,
                            ColorUtil.applyOpacity(0xFF009C41, alpha),
                            ColorUtil.applyOpacity(0xFF8EFFC1, alpha), bg, bg);
                }
                double armorValue = target.getTotalArmorValue() / 20.0;
                if (armorValue > 0) {
                    RenderUtil.drawGradientRectBordered(x + 2.5, y + 34.5, x + 1.5 + ((getWidth() - 3.5) * armorValue), y + 37, 0.74,
                            ColorUtil.applyOpacity(0xFF0067B0, alpha),
                            ColorUtil.applyOpacity(0xFF39D5FF, alpha), bg, bg);
                }

                // Draw head
                GlStateManager.pushMatrix();
                RenderUtil.setAlphaLimit(0);
                int textColor = ColorUtil.applyOpacity(-1, alpha);
                if (target instanceof AbstractClientPlayer) {
                    RenderUtil.color(textColor);
                    float f = 0.8125F;
                    GlStateManager.scale(f, f, f);
                    drawBigHead(x / f + 3, y / f + 3, 32, 32, (AbstractClientPlayer) target);
                } else {
                    if (!(openAnimation.getDirection() == Direction.BACKWARDS)) {
                        Gui.drawRect2(x + 3, y + 3, 25, 25, bg);
                        GlStateManager.scale(2, 2, 2);
                        FontManager.font20.drawStringWithShadow("?", (x + 11) / 2.0F, (y + 11) / 2.0F - 2f, textColor);
                    }
                }
                GlStateManager.popMatrix();
                if (!(openAnimation.getDirection() == Direction.BACKWARDS)) {
                    // Draw name
                    FontManager.font20.drawString(target.getName(), x + 31, y + 2, textColor);
                    FontManager.font16.drawString("Health: " + DF_1.format(target.getHealth()), x + 31, y + 13, textColor);
                    FontManager.font16.drawString("Distance: " + DF_1.format(mc.thePlayer.getDistanceToEntity(target)) + " m", x + 31, y + 22, textColor);
                }

                break;
            }
            case "raven": {

                GlStateManager.pushMatrix();
                GlStateManager.translate(x, y, 0f);

                RoundedUtil.drawRound(0, 0, 70f + mc.fontRendererObj.getStringWidth(target.getName()), 40f, 8f, new Color(0, 0, 0, (int) (alpha * 92)));
                RenderUtil.drawOutline(6, 0, 64f + mc.fontRendererObj.getStringWidth(target.getName()), 28f, 6f, 2f, 2f, ColorUtil.applyOpacity(color(1), alpha), ColorUtil.applyOpacity(color(16), alpha));
                if (!(openAnimation.getDirection() == Direction.BACKWARDS)) {

                    GlStateManager.enableBlend();
                    mc.fontRendererObj.drawStringWithShadow(target.getName(), 7f, 10f, new Color(244, 67, 54, (int) alpha).getRGB());
                    GlStateManager.disableBlend();

                    GlStateManager.enableBlend();
                    mc.fontRendererObj.drawStringWithShadow(target.getHealth() > mc.thePlayer.getHealth() ? "L" : "W", mc.fontRendererObj.getStringWidth(target.getName()) + 55f, 10f, target.getHealth() > mc.thePlayer.getHealth() ? new Color(244, 67, 54).getRGB() : new Color(0, 255, 0, (int) alpha).getRGB());
                    GlStateManager.disableBlend();

                    GlStateManager.enableBlend();
                    mc.fontRendererObj.drawStringWithShadow(DF_1.format(target.getHealth()), 7f + mc.fontRendererObj.getStringWidth(target.getName()) + 4f, 10f, ColorUtil.applyOpacity(RenderUtil.getHealthColor(target.getHealth(), target.getMaxHealth()).getRGB(), alpha));
                    GlStateManager.disableBlend();
                }
                RoundedUtil.drawGradientRoundLR(6, 10 + 17, (int) ((70f + mc.fontRendererObj.getStringWidth(target.getName()) - 5f) * (target.animatedHealthBar / target.getMaxHealth())) - 6, 3.5f, 1.5f, ColorUtil.applyOpacity(color(1), alpha), ColorUtil.applyOpacity(color(16), alpha));

                GlStateManager.resetColor();
                GlStateManager.enableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                target.animatedHealthBar += ((target.getHealth() - target.animatedHealthBar) / Math.pow(2.0F, 10.0F - 4f)) * RenderUtil.deltaTime;

                break;
            }
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }


    public static void drawEquippedShit(final int x, final int y, final EntityLivingBase target) {
        if (!(target instanceof EntityPlayer)) return;
        GL11.glPushMatrix();
        final ArrayList<ItemStack> stuff = new ArrayList<>();
        int cock = -2;
        for (int geraltOfNigeria = 3; geraltOfNigeria >= 0; --geraltOfNigeria) {
            final ItemStack armor = target.getCurrentArmor(geraltOfNigeria);
            if (armor != null) {
                stuff.add(armor);
            }
        }
        if (target.getHeldItem() != null) {
            stuff.add(target.getHeldItem());
        }

        for (final ItemStack yes : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 16;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(yes, cock + x, y);
            Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRendererObj, yes, cock + x, y);
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            yes.getEnchantmentTagList();
        }
        GL11.glPopMatrix();
    }

    protected void renderPlayer2D(float x, float y, float width, float height, AbstractClientPlayer player) {
        GLUtil.startBlend();
        mc.getTextureManager().bindTexture(player.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(x, y, (float) 8.0, (float) 8.0, 8, 8, width, height, 64.0F, 64.0F);
        GLUtil.endBlend();
    }

    public void drawModel(final float yaw, final float pitch, final EntityLivingBase entityLivingBase) {
        GlStateManager.resetColor();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, 0.0f, 50.0f);
        GlStateManager.scale(-50.0f, 50.0f, 50.0f);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        final float renderYawOffset = entityLivingBase.renderYawOffset;
        final float rotationYaw = entityLivingBase.rotationYaw;
        final float rotationPitch = entityLivingBase.rotationPitch;
        final float prevRotationYawHead = entityLivingBase.prevRotationYawHead;
        final float rotationYawHead = entityLivingBase.rotationYawHead;
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate((float) (-Math.atan(pitch / 40.0f) * 20.0), 1.0f, 0.0f, 0.0f);
        entityLivingBase.renderYawOffset = yaw - 0.4f;
        entityLivingBase.rotationYaw = yaw - 0.2f;
        entityLivingBase.rotationPitch = pitch;
        entityLivingBase.rotationYawHead = entityLivingBase.rotationYaw;
        entityLivingBase.prevRotationYawHead = entityLivingBase.rotationYaw;
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        final RenderManager renderManager = mc.getRenderManager();
        renderManager.setPlayerViewY(180.0f);
        renderManager.setRenderShadow(false);
        renderManager.renderEntityWithPosYaw(entityLivingBase, 0.0, 0.0, 0.0, 0.0f, 1.0f);
        renderManager.setRenderShadow(true);
        entityLivingBase.renderYawOffset = renderYawOffset;
        entityLivingBase.rotationYaw = rotationYaw;
        entityLivingBase.rotationPitch = rotationPitch;
        entityLivingBase.prevRotationYawHead = prevRotationYawHead;
        entityLivingBase.rotationYawHead = rotationYawHead;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.resetColor();
    }

}
