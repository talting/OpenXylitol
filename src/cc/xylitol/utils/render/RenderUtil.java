package cc.xylitol.utils.render;

import cc.xylitol.module.impl.render.HUD;
import cc.xylitol.utils.math.MathUtils;
import cc.xylitol.utils.render.animation.AnimationUtils;
import cc.xylitol.utils.render.shader.ShaderUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;
import org.lwjglx.opengl.Display;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static cc.xylitol.Client.mc;
import static cc.xylitol.utils.render.Colors.blendColors;
import static cc.xylitol.utils.render.GLUtil.end2DRendering;
import static cc.xylitol.utils.render.GLUtil.setup2DRendering;
import static cc.xylitol.utils.render.RoundedUtil.setupRoundedRectUniforms;
import static java.lang.Math.*;
import static java.lang.Math.PI;
import static net.minecraft.client.renderer.RenderGlobal.drawSelectionBoundingBox;
import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {
    private static final Tessellator tessellator = Tessellator.getInstance();

    private static final WorldRenderer worldrenderer = tessellator.getWorldRenderer();

    public static int deltaTime;

    public static double ticks = 0;
    public static long lastFrame = 0;
    private static final Frustum FRUSTUM = new Frustum();
    public static final ShaderUtil roundedShader = new ShaderUtil("roundedRect");
    private static final Map<Integer, Boolean> glCapMap = new HashMap<>();
    @Getter
    @Setter
    private static double frameDeltaTime = 0;
    private static int lastScaledWidth;
    private static int lastScaledHeight;
    private static ScaledResolution scaledResolution;
    private static float lastGuiScale;

    public static void drawEntityESP(double x, double y, double z, double width, double height, float red, float green, float blue, float alpha, float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWdith) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, alpha);
        drawBoundBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glLineWidth(lineWdith);
        GL11.glColor4f(lineRed, lineGreen, lineBlue, lineAlpha);
        RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    public static void drawEntityServerESP(final Entity entity, final float red, final float green, final float blue, final float alpha, final float lineAlpha, final float lineWidth) {
        double d0 = entity.serverPosX / 32.0;
        double d2 = entity.serverPosY / 32.0;
        double d3 = entity.serverPosZ / 32.0;
        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase livingBase = (EntityLivingBase)entity;
            d0 = livingBase.realPosX / 32.0;
            d2 = livingBase.realPosY / 32.0;
            d3 = livingBase.realPosZ / 32.0;
        }
        final float x = (float)(d0 - mc.getRenderManager().getRenderPosX());
        final float y = (float)(d2 - mc.getRenderManager().getRenderPosY());
        final float z = (float)(d3 - mc.getRenderManager().getRenderPosZ());
        GL11.glColor4f(red, green, blue, alpha);
        otherDrawBoundingBox(entity, x, y, z, entity.width - 0.2f, entity.height + 0.1f);
        if (lineWidth > 0.0f) {
            GL11.glLineWidth(lineWidth);
            GL11.glColor4f(red, green, blue, lineAlpha);
            otherDrawOutlinedBoundingBox(entity, x, y, z, entity.width - 0.2f, entity.height + 0.1f);
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void otherDrawOutlinedBoundingBox(final Entity entity, final float x, final float y, final float z, double width, final double height) {
        width *= 1.5;
        final float yaw1 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 45.0f;
        float newYaw1;
        if (yaw1 < 0.0f) {
            newYaw1 = 0.0f;
            newYaw1 += 360.0f - Math.abs(yaw1);
        }
        else {
            newYaw1 = yaw1;
        }
        newYaw1 *= -1.0f;
        newYaw1 *= (float)0.017453292519943295;
        final float yaw2 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 135.0f;
        float newYaw2;
        if (yaw2 < 0.0f) {
            newYaw2 = 0.0f;
            newYaw2 += 360.0f - Math.abs(yaw2);
        }
        else {
            newYaw2 = yaw2;
        }
        newYaw2 *= -1.0f;
        newYaw2 *= (float)0.017453292519943295;
        final float yaw3 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 225.0f;
        float newYaw3;
        if (yaw3 < 0.0f) {
            newYaw3 = 0.0f;
            newYaw3 += 360.0f - Math.abs(yaw3);
        }
        else {
            newYaw3 = yaw3;
        }
        newYaw3 *= -1.0f;
        newYaw3 *= (float)0.017453292519943295;
        final float yaw4 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 315.0f;
        float newYaw4;
        if (yaw4 < 0.0f) {
            newYaw4 = 0.0f;
            newYaw4 += 360.0f - Math.abs(yaw4);
        }
        else {
            newYaw4 = yaw4;
        }
        newYaw4 *= -1.0f;
        newYaw4 *= (float)0.017453292519943295;
        final float x2 = (float)(Math.sin(newYaw1) * width + x);
        final float z2 = (float)(Math.cos(newYaw1) * width + z);
        final float x3 = (float)(Math.sin(newYaw2) * width + x);
        final float z3 = (float)(Math.cos(newYaw2) * width + z);
        final float x4 = (float)(Math.sin(newYaw3) * width + x);
        final float z4 = (float)(Math.cos(newYaw3) * width + z);
        final float x5 = (float)(Math.sin(newYaw4) * width + x);
        final float z5 = (float)(Math.cos(newYaw4) * width + z);
        final float y2 = (float)(y + height);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x2, y, z2).endVertex();
        worldrenderer.pos(x2, y2, z2).endVertex();
        worldrenderer.pos(x3, y2, z3).endVertex();
        worldrenderer.pos(x3, y, z3).endVertex();
        worldrenderer.pos(x2, y, z2).endVertex();
        worldrenderer.pos(x5, y, z5).endVertex();
        worldrenderer.pos(x4, y, z4).endVertex();
        worldrenderer.pos(x4, y2, z4).endVertex();
        worldrenderer.pos(x5, y2, z5).endVertex();
        worldrenderer.pos(x5, y, z5).endVertex();
        worldrenderer.pos(x5, y2, z5).endVertex();
        worldrenderer.pos(x4, y2, z4).endVertex();
        worldrenderer.pos(x3, y2, z3).endVertex();
        worldrenderer.pos(x3, y, z3).endVertex();
        worldrenderer.pos(x4, y, z4).endVertex();
        worldrenderer.pos(x5, y, z5).endVertex();
        worldrenderer.pos(x5, y2, z5).endVertex();
        worldrenderer.pos(x2, y2, z2).endVertex();
        worldrenderer.pos(x2, y, z2).endVertex();
        worldrenderer.endVertex();
        tessellator.draw();
    }

    public static void otherDrawBoundingBox(final Entity entity, final float x, final float y, final float z, double width, final double height) {
        width *= 1.5;
        final float yaw1 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 45.0f;
        float newYaw1;
        if (yaw1 < 0.0f) {
            newYaw1 = 0.0f;
            newYaw1 += 360.0f - Math.abs(yaw1);
        }
        else {
            newYaw1 = yaw1;
        }
        newYaw1 *= -1.0f;
        newYaw1 *= (float)0.017453292519943295;
        final float yaw2 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 135.0f;
        float newYaw2;
        if (yaw2 < 0.0f) {
            newYaw2 = 0.0f;
            newYaw2 += 360.0f - Math.abs(yaw2);
        }
        else {
            newYaw2 = yaw2;
        }
        newYaw2 *= -1.0f;
        newYaw2 *= (float)0.017453292519943295;
        final float yaw3 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 225.0f;
        float newYaw3;
        if (yaw3 < 0.0f) {
            newYaw3 = 0.0f;
            newYaw3 += 360.0f - Math.abs(yaw3);
        }
        else {
            newYaw3 = yaw3;
        }
        newYaw3 *= -1.0f;
        newYaw3 *= (float)0.017453292519943295;
        final float yaw4 = MathHelper.wrapAngleTo180_float(entity.getRotationYawHead()) + 315.0f;
        float newYaw4;
        if (yaw4 < 0.0f) {
            newYaw4 = 0.0f;
            newYaw4 += 360.0f - Math.abs(yaw4);
        }
        else {
            newYaw4 = yaw4;
        }
        newYaw4 *= -1.0f;
        newYaw4 *= (float)0.017453292519943295;
        final float x2 = (float)(Math.sin(newYaw1) * width + x);
        final float z2 = (float)(Math.cos(newYaw1) * width + z);
        final float x3 = (float)(Math.sin(newYaw2) * width + x);
        final float z3 = (float)(Math.cos(newYaw2) * width + z);
        final float x4 = (float)(Math.sin(newYaw3) * width + x);
        final float z4 = (float)(Math.cos(newYaw3) * width + z);
        final float x5 = (float)(Math.sin(newYaw4) * width + x);
        final float z5 = (float)(Math.cos(newYaw4) * width + z);
        final float y2 = (float)(y + height);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x2, y, z2).endVertex();
        worldrenderer.pos(x2, y2, z2).endVertex();
        worldrenderer.pos(x3, y2, z3).endVertex();
        worldrenderer.pos(x3, y, z3).endVertex();
        worldrenderer.pos(x3, y, z3).endVertex();
        worldrenderer.pos(x3, y2, z3).endVertex();
        worldrenderer.pos(x4, y2, z4).endVertex();
        worldrenderer.pos(x4, y, z4).endVertex();
        worldrenderer.pos(x4, y, z4).endVertex();
        worldrenderer.pos(x4, y2, z4).endVertex();
        worldrenderer.pos(x5, y2, z5).endVertex();
        worldrenderer.pos(x5, y, z5).endVertex();
        worldrenderer.pos(x5, y, z5).endVertex();
        worldrenderer.pos(x5, y2, z5).endVertex();
        worldrenderer.pos(x2, y2, z2).endVertex();
        worldrenderer.pos(x2, y, z2).endVertex();
        worldrenderer.pos(x2, y, z2).endVertex();
        worldrenderer.pos(x3, y, z3).endVertex();
        worldrenderer.pos(x4, y, z4).endVertex();
        worldrenderer.pos(x5, y, z5).endVertex();
        worldrenderer.pos(x2, y2, z2).endVertex();
        worldrenderer.pos(x3, y2, z3).endVertex();
        worldrenderer.pos(x4, y2, z4).endVertex();
        worldrenderer.pos(x5, y2, z5).endVertex();
        worldrenderer.endVertex();
        tessellator.draw();
    }
    public static void drawAxisAlignedBB(AxisAlignedBB axisAlignedBB, boolean outline, int color) {
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)3042);
        GL11.glLineWidth(2.0f);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        color(color);
        RenderGlobal.func_181561_a(axisAlignedBB, outline, true);
        GlStateManager.resetColor();
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
    }

    public static Color getHealthColor(float health, float maxHealth) {
        float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
        Color[] colors = new Color[]{new Color(108, 0, 0), new Color(255, 51, 0), Color.GREEN};
        float progress = health / maxHealth;
        return blendColors(fractions, colors, progress).brighter();
    }

    public static void drawOutline(float x, float y, float x2, float y2, float radius, float line, float offset, Color c1, Color c2) {
        glEnable(3042);
        glDisable(2884);
        glDisable(3553);
        glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glBlendFunc(770, 771);
        GL11.glPushMatrix();
        GL11.glLineWidth(line);
        GL11.glBegin(3);
        float edgeRadius = radius;
        float centerX = x + edgeRadius;
        float centerY = y + edgeRadius;
        int vertices = (int) Math.min(Math.max(edgeRadius, 10.0F), 90.0F);
        int i;
        int colorI = 0;
        double angleRadians;
        centerX = x2;
        centerY = y2 + edgeRadius;
        vertices = (int) Math.min(Math.max(edgeRadius, 10.0F), 90.0F);
        for (i = 0; i <= vertices; ++i) {
            color(fadeBetween(c1.getRGB(), c2.getRGB(), (long) 20L * colorI));
            angleRadians = 6.283185307179586D * (double) (i) / (double) (vertices * 4);
            GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
            colorI++;
        }

        GL11.glEnd();
        GL11.glLineWidth(line);
        GL11.glBegin(3);
        centerX = x2 + edgeRadius;
        centerY = y2 + edgeRadius;
        for (i = 0; i <= (y2 - y); ++i) {
            color(fadeBetween(c1.getRGB(), c2.getRGB(), (long) 20L * colorI));
            GL11.glVertex2d(centerX, centerY - i);
            colorI++;
        }
        GL11.glEnd();
        GL11.glLineWidth(line);
        GL11.glBegin(3);
        centerX = x2;
        centerY = (y) + edgeRadius;
        for (i = 0; i <= vertices; ++i) {
            color(fadeBetween(c1.getRGB(), c2.getRGB(), (long) 20L * colorI));
            angleRadians = 6.283185307179586D * (double) (i + 90) / (double) (vertices * 4);
            GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
            colorI++;
        }
        GL11.glEnd();
        GL11.glLineWidth(line);
        GL11.glBegin(3);
        centerX = x2;
        centerY = (y);
        for (i = 0; i <= (x2 - x); ++i) {
            color(fadeBetween(c1.getRGB(), c2.getRGB(), (long) 20L * colorI));
            GL11.glVertex2d(centerX - i, centerY);
            colorI++;
        }
        GL11.glEnd();
        GL11.glLineWidth(line);
        GL11.glBegin(3);
        centerX = x;
        centerY = (y + edgeRadius);
        for (i = 0; i <= vertices; ++i) {
            color(fadeBetween(c1.getRGB(), c2.getRGB(), (long) 20L * colorI));
            angleRadians = 6.283185307179586D * (double) (i + 180) / (double) (vertices * 4);
            GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
            colorI++;
        }
        colorI = 0;
        GL11.glEnd();
        GL11.glLineWidth(line);
        GL11.glBegin(3);
        centerX = x2;
        centerY = (y2 + vertices + offset);
        for (i = 0; i <= (x2 - x); ++i) {
            color(fadeBetween(c1.getRGB(), c2.getRGB(), (long) 20L * colorI));
            GL11.glVertex2d(centerX - i, centerY);
            colorI++;
        }
        GL11.glEnd();
        GL11.glLineWidth(line);
        GL11.glBegin(3);
        centerX = x;
        centerY = (y2 + edgeRadius);
        for (i = 0; i <= vertices; ++i) {
            color(fadeBetween(c1.getRGB(), c2.getRGB(), (long) 20L * colorI));
            angleRadians = 6.283185307179586D * (double) (i + 180) / (double) (vertices * 4);
            GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY - Math.cos(angleRadians) * (double) edgeRadius);
            colorI++;
        }
        GL11.glEnd();
        GL11.glLineWidth(line);
        GL11.glBegin(3);
        centerX = x - edgeRadius;
        centerY = (y2 + edgeRadius);

        for (i = 0; i <= (y2 - y); ++i) {
            color(fadeBetween(c1.getRGB(), c2.getRGB(), (long) 20L * colorI));
            GL11.glVertex2d(centerX, centerY - i);
            colorI++;
        }
        GL11.glEnd();
        GL11.glPopMatrix();
        glDisable(3042);
        glEnable(2884);
        glEnable(3553);
        glDisable(2848);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }
    public static int fadeBetween(int startColour, int endColour, double progress) {
        if (progress > 1) progress = 1 - progress % 1;
        return fadeTo(startColour, endColour, progress);
    }

    public static int fadeTo(int startColour, int endColour, double progress) {
        double invert = 1.0 - progress;
        int r = (int) ((startColour >> 16 & 0xFF) * invert +
                (endColour >> 16 & 0xFF) * progress);
        int g = (int) ((startColour >> 8 & 0xFF) * invert +
                (endColour >> 8 & 0xFF) * progress);
        int b = (int) ((startColour & 0xFF) * invert +
                (endColour & 0xFF) * progress);
        int a = (int) ((startColour >> 24 & 0xFF) * invert +
                (endColour >> 24 & 0xFF) * progress);
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                (b & 0xFF);
    }

    public static int fadeBetween(int startColour, int endColour, long offset) {
        return fadeBetween(startColour, endColour, ((System.currentTimeMillis() + offset) % 2000L) / 1000.0);
    }
    public static void connectPoints(float xOne, float yOne, float xTwo, float yTwo) {
        glPushMatrix();
        glEnable(GL_LINE_SMOOTH);
        glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        glLineWidth(0.5F);
        glBegin(GL_LINES);
        glVertex2f(xOne, yOne);
        glVertex2f(xTwo, yTwo);
        glEnd();
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }


    public static int colorSwitch(Color firstColor, Color secondColor, float time, int index, long timePerIndex, double speed) {
        return colorSwitch(firstColor, secondColor, time, index, timePerIndex, speed, 255);
    }

    public static int colorSwitch(Color firstColor, Color secondColor, float time, int index, long timePerIndex, double speed, double alpha) {
        long now = (long) (speed * System.currentTimeMillis() + index * timePerIndex);

        float redDiff = (firstColor.getRed() - secondColor.getRed()) / time;
        float greenDiff = (firstColor.getGreen() - secondColor.getGreen()) / time;
        float blueDiff = (firstColor.getBlue() - secondColor.getBlue()) / time;
        int red = Math.round(secondColor.getRed() + redDiff * (now % (long) time));
        int green = Math.round(secondColor.getGreen() + greenDiff * (now % (long) time));
        int blue = Math.round(secondColor.getBlue() + blueDiff * (now % (long) time));

        float redInverseDiff = (secondColor.getRed() - firstColor.getRed()) / time;
        float greenInverseDiff = (secondColor.getGreen() - firstColor.getGreen()) / time;
        float blueInverseDiff = (secondColor.getBlue() - firstColor.getBlue()) / time;
        int inverseRed = Math.round(firstColor.getRed() + redInverseDiff * (now % (long) time));
        int inverseGreen = Math.round(firstColor.getGreen() + greenInverseDiff * (now % (long) time));
        int inverseBlue = Math.round(firstColor.getBlue() + blueInverseDiff * (now % (long) time));

        if (now % ((long) time * 2) < (long) time)
            return ColorUtil.getColor(inverseRed, inverseGreen, inverseBlue, (int) alpha);
        else return ColorUtil.getColor(red, green, blue, (int) alpha);
    }

    public static void drawCircle(float x, float y, float radius, int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        glColor4f(red, green, blue, alpha);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glPushMatrix();
        glLineWidth(1F);
        glBegin(GL_POLYGON);
        for(int i = 0; i <= 360; i++)
            glVertex2d(x + Math.sin(i * Math.PI / 180.0D) * radius, y + Math.cos(i * Math.PI / 180.0D) * radius);
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_LINE_SMOOTH);
        glColor4f(1F, 1F, 1F, 1F);
    }
    public static void drawPlatESP(EntityLivingBase player,Color color){

        if (mc.getRenderManager() == null || player == null) return;

        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glLineWidth(1.8F);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GlStateManager.depthMask(true);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        float partialTicks = mc.timer.renderPartialTicks;
        double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks + player.getEyeHeight() * 1.2;
        double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        float width = player.width;
        float height = player.height + (player.isSneaking() ? -0.2F : 0.1F);

        color(color.getRGB());
        drawBoundBox(new AxisAlignedBB(x - width / 1.75, y, z - width / 1.75,x + width / 1.75, y - 0.05, z + width / 1.75));
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
        color(Color.WHITE.getRGB());
    }

    public static void setTextureFliterTypeToLinear(){
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    }

    public static Vec3 getRenderPos(double x, double y, double z) {

        x -= mc.getRenderManager().renderPosX;
        y -= mc.getRenderManager().renderPosY;
        z -= mc.getRenderManager().renderPosZ;

        return new Vec3(x, y, z);
    }

    public static int hexColor(int red, int green, int blue) {
        return hexColor(red, green, blue, 255);
    }

    public static int hexColor(int red, int green, int blue, int alpha) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static void glVertex3D(Vec3 vector3d) {
        GL11.glVertex3d(vector3d.xCoord, vector3d.yCoord, vector3d.zCoord);
    }

    public static void end() {
        GL11.glEnd();
    }

    public static void drawBoundBox(final AxisAlignedBB aa) {

        glBegin(GL_QUADS);
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        end();

        glBegin(GL_QUADS);
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        end();

        glBegin(GL_QUADS);
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        end();

        glBegin(GL_QUADS);
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        end();

        glBegin(GL_QUADS);
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        end();

        glBegin(GL_QUADS);
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        end();
    }

    public static float[] getNextWheelPosition(int wheel, float[] memorize, float topY, float bottomY, float objectY, float offset, boolean updateAction) {
        float target = memorize[0];
        float current = memorize[1];
        if (updateAction) {
            if (wheel > 0) {
                for (int i = 0; i < 15; i++) if (target++ > 0) break;
                if (target > 0) target = 0;
            } else if (wheel < 0) {
                for (int i = 0; i < 15; i++) {
                    if (topY + ((objectY - current) + (target - 1)) + offset < bottomY) break;
                    else target -= 1;
                }
            }
        }

        final float reversingOffset = bottomY - topY - offset;
        if (objectY - current + target < reversingOffset) {
            final float diff = reversingOffset - (objectY - current + target);
            if (target + diff <= 0) target += diff;
            else target += diff - (target + diff);
        }

        current = AnimationUtils.animate(current, target, .2F);

        return new float[]{
                target, current
        };
    }
    public static void drawVGradientRect(double x, double y, double width, double height, int startColor, int endColor) {
        final float f = (float) (startColor >> 24 & 255) / 255.0F;
        final float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        final float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        final float f3 = (float) (startColor & 255) / 255.0F;
        final float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        final float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        final float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        final float f7 = (float) (endColor & 255) / 255.0F;
        setup2DRendering(() -> {
            glShadeModel(GL_SMOOTH);
            worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos(x + width, y, 0.0D).color(f1, f2, f3, f).endVertex();
            worldrenderer.pos(x, y, 0.0D).color(f1, f2, f3, f).endVertex();
            worldrenderer.pos(x, y + height, 0.0D).color(f5, f6, f7, f4).endVertex();
            worldrenderer.pos(x + width, y + height, 0.0D).color(f5, f6, f7, f4).endVertex();
            tessellator.draw();
            GlStateManager.resetColor();
            glShadeModel(GL_FLAT);
        });
    }

    public static void drawHGradientRect(double x, double y, double width, double height, int startColor, int endColor) {
        final float f = (float) (startColor >> 24 & 255) / 255.0F;
        final float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        final float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        final float f3 = (float) (startColor & 255) / 255.0F;
        final float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        final float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        final float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        final float f7 = (float) (endColor & 255) / 255.0F;
        setup2DRendering(() -> {
            glShadeModel(GL_SMOOTH);
            worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos(x, y, 0.0D).color(f1, f2, f3, f).endVertex();
            worldrenderer.pos(x, y + height, 0.0D).color(f1, f2, f3, f).endVertex();
            worldrenderer.pos(x + width, y + height, 0.0D).color(f5, f6, f7, f4).endVertex();
            worldrenderer.pos(x + width, y, 0.0D).color(f5, f6, f7, f4).endVertex();
            tessellator.draw();
            GlStateManager.resetColor();
            glShadeModel(GL_FLAT);
        });
    }

    public static void drawItemStack(ItemStack stack, float x, float y) {
        GL11.glPushMatrix();

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld != null) {
            RenderHelper.enableGUIStandardItemLighting();
        }

        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.clear(256);
        GlStateManager.enableBlend();

        mc.getRenderItem().zLevel = -150.0F;
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, (int) x, (int) y);
        mc.getRenderItem().zLevel = 0.0F;

        GlStateManager.enableBlend();
        final float z = 0.5F;

        GlStateManager.scale(z, z, z);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();

        GL11.glPopMatrix();
    }


    public static void skeetRect(final double x, final double y, final double x1, final double y1, final double size) {
        RenderUtil.rectangleBordered(x, y - 4.0, x1 + size, y1 + size, 0.5, new Color(60, 60, 60).getRGB(), ColorUtil.getColor(10,10,10));
        RenderUtil.rectangleBordered(x + 1.0, y - 3.0, x1 + size - 1.0, y1 + size - 1.0, 1.0, new Color(40, 40, 40).getRGB(), ColorUtil.getColor(40, 40, 40));
        RenderUtil.rectangleBordered(x + 2.5, y - 1.5, x1 + size - 2.5, y1 + size - 2.5, 0.5, new Color(40, 40, 40).getRGB(), ColorUtil.getColor(60, 60, 60));
        RenderUtil.rectangleBordered(x + 2.5, y - 1.5, x1 + size - 2.5, y1 + size - 2.5, 0.5, new Color(22, 22, 22).getRGB(), ColorUtil.getColor(255, 255, 255, 0));
    }

    public static void skeetRectSmall(final double x, final double y, final double x1, final double y1, final double size) {
        RenderUtil.rectangleBordered(x + 4.35, y + 0.5, x1 + size - 84.5, y1 + size - 4.35, 0.5, new Color(48, 48, 48).getRGB(), ColorUtil.getColor(10, 10, 10));
        RenderUtil.rectangleBordered(x + 5.0, y + 1.0, x1 + size - 85.0, y1 + size - 5.0, 0.5, new Color(17, 17, 17).getRGB(), ColorUtil.getColor(255, 255, 255, 0));
    }
    public static void rectangleBordered(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
        Gui.drawRect(x + width, y + width, x1 - width, y1 - width, internalColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawRect(x + width, y, x1 - width, y + width, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawRect(x, y, x + width, y1, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawRect(x1 - width, y, x1, y1, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawRect(x + width, y1 - width, x1 - width, y1, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
    public static void drawBorderedRect2(final double x, final double y, final double x1, final double y1, final double width, final int internalColor, final int borderColor) {
        Gui.drawRect(x + width, y + width, x1 - width, y1 - width, internalColor);
        Gui.drawRect(x + width, y, x1 - width, y + width, borderColor);
        Gui.drawRect(x, y, x + width, y1, borderColor);
        Gui.drawRect(x1 - width, y, x1, y1, borderColor);
        Gui.drawRect(x + width, y1 - width, x1 - width, y1, borderColor);
    }



    public static void drawLoadingCircle(float x, float y) {
        for (int i = 0; i < 2; i++) {
            int rot = (int) ((System.nanoTime() / 5000000 * i) % 360);
            drawCircle(x, y, i * 8, rot - 180, rot);
        }
    }

    public static void drawCircle(float x, float y, float radius, int start, int end) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glColor(Color.WHITE.getRGB());

        glEnable(GL_LINE_SMOOTH);
        glLineWidth(3F);
        glBegin(GL_LINE_STRIP);
        for (float i = end; i >= start; i -= (360 / 90.0f)) {
            glVertex2f((float) (x + (cos(i * PI / 180) * (radius * 1.001F))), (float) (y + (sin(i * PI / 180) * (radius * 1.001F))));
        }
        glEnd();
        glDisable(GL_LINE_SMOOTH);

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRound(float x, float y, float width, float height, float radius, boolean blur, Color color, boolean topLeftCorner, boolean bottomLeftCorner, boolean topRightCorner, boolean bottomRightCorner) {
        GlStateManager.resetColor();
        GlStateManager.enableBlend();

        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (0 * .01));

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        boolean flag = GL11.glIsEnabled(GL11.GL_ALPHA_TEST) && disableAlphaTest;
//        if (flag) {
//            GL11.glDisable(GL11.GL_ALPHA_TEST);
//        }

        roundedShader.init();

        float alpha = color.getAlpha() / 255f;
//        if (alpha < 0.101 && !disableAlphaTest) {
//            alpha = 0.101f;
//        }

        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        roundedShader.setUniformi("blur", blur ? 1 : 0);
        roundedShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha);
        roundedShader.setUniformf("corner", topLeftCorner ? 1 : 0, bottomLeftCorner ? 1 : 0, topRightCorner ? 1 : 0, bottomRightCorner ? 1 : 0);

        ShaderUtil.drawQuads(x - 1, y - 1, width + 2, height + 2);
        roundedShader.unload();
//        if (flag)
//            GL11.glEnable(GL11.GL_ALPHA_TEST);
        GlStateManager.disableBlend();
    }

    public static Color tripleColor(int rgbValue, float alpha) {
        alpha = Math.min(1, Math.max(0, alpha));
        return new Color(rgbValue, rgbValue, rgbValue, (int) (255 * alpha));
    }

    public static void scissorStart(double x, double y, double width, double height) {
        glEnable(GL_SCISSOR_TEST);
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        final double scale = sr.getScaleFactor();
        double finalHeight = height * scale;
        double finalY = (sr.getScaledHeight() - y) * scale;
        double finalX = x * scale;
        double finalWidth = width * scale;
        glScissor((int) finalX, (int) (finalY - finalHeight), (int) finalWidth, (int) finalHeight);
    }

    public static void scissorEnd() {
        glDisable(GL_SCISSOR_TEST);
    }

    public static void draw2D(final BlockPos blockPos, final int color, final int backgroundColor) {
        final RenderManager renderManager = mc.getRenderManager();

        final double posX = (blockPos.getX() + 0.5) - renderManager.renderPosX;
        final double posY = blockPos.getY() - renderManager.renderPosY;
        final double posZ = (blockPos.getZ() + 0.5) - renderManager.renderPosZ;

        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, posZ);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0F, 1F, 0F);
        GlStateManager.scale(-0.1D, -0.1D, 0.1D);

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.depthMask(true);

        glColor(color);

        glCallList(DISPLAY_LISTS_2D[0]);

        glColor(backgroundColor);

        glCallList(DISPLAY_LISTS_2D[1]);

        GlStateManager.translate(0, 9, 0);

        glColor(color);

        glCallList(DISPLAY_LISTS_2D[2]);

        glColor(backgroundColor);

        glCallList(DISPLAY_LISTS_2D[3]);

        // Stop render
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);

        GlStateManager.popMatrix();
    }
    private static final int[] DISPLAY_LISTS_2D = new int[4];

    static {
        for (int i = 0; i < DISPLAY_LISTS_2D.length; i++) {
            DISPLAY_LISTS_2D[i] = glGenLists(1);
        }

        glNewList(DISPLAY_LISTS_2D[0], GL_COMPILE);

        quickDrawRect(-7F, 2F, -4F, 3F);
        quickDrawRect(4F, 2F, 7F, 3F);
        quickDrawRect(-7F, 0.5F, -6F, 3F);
        quickDrawRect(6F, 0.5F, 7F, 3F);

        glEndList();

        glNewList(DISPLAY_LISTS_2D[1], GL_COMPILE);

        quickDrawRect(-7F, 3F, -4F, 3.3F);
        quickDrawRect(4F, 3F, 7F, 3.3F);
        quickDrawRect(-7.3F, 0.5F, -7F, 3.3F);
        quickDrawRect(7F, 0.5F, 7.3F, 3.3F);

        glEndList();

        glNewList(DISPLAY_LISTS_2D[2], GL_COMPILE);

        quickDrawRect(4F, -20F, 7F, -19F);
        quickDrawRect(-7F, -20F, -4F, -19F);
        quickDrawRect(6F, -20F, 7F, -17.5F);
        quickDrawRect(-7F, -20F, -6F, -17.5F);

        glEndList();

        glNewList(DISPLAY_LISTS_2D[3], GL_COMPILE);

        quickDrawRect(7F, -20F, 7.3F, -17.5F);
        quickDrawRect(-7.3F, -20F, -7F, -17.5F);
        quickDrawRect(4F, -20.3F, 7.3F, -20F);
        quickDrawRect(-7.3F, -20.3F, -4F, -20F);

        glEndList();
    }
    public static void quickDrawRect(final float x, final float y, final float x2, final float y2) {
        glBegin(GL_QUADS);

        glVertex2d(x2, y);
        glVertex2d(x, y);
        glVertex2d(x, y2);
        glVertex2d(x2, y2);

        glEnd();
    }
    public static Color getColor(int color) {
        int f = (color >> 24 & 0xFF);
        int f1 = (color >> 16 & 0xFF);
        int f2 = (color >> 8 & 0xFF);
        int f3 = (color & 0xFF);
        return new Color(f1, f2, f3, f);
    }
    public static void renderOne() {
        StencilUtil.checkSetupFBO();
        GL11.glPushAttrib(1048575);
        GL11.glDisable(3008);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(4.0F);
        GL11.glEnable(2848);
        GL11.glEnable(2960);
        GL11.glClear(1024);
        GL11.glClearStencil(15);
        GL11.glStencilFunc(512, 1, 15);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glPolygonMode(1032, 6913);
    }

    public static void renderTwo() {
        GL11.glStencilFunc(512, 0, 15);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glPolygonMode(1032, 6914);
    }

    public static void renderThree() {
        GL11.glStencilFunc(514, 1, 15);
        GL11.glStencilOp(7680, 7680, 7680);
        GL11.glPolygonMode(1032, 6913);
    }

    public static void renderFour(int color) {
        setColor(color);
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
        GL11.glEnable(10754);
        GL11.glPolygonOffset(1.0F, -2000000.0F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
    }

    public static void renderFive() {
        GL11.glPolygonOffset(1.0F, 2000000.0F);
        GL11.glDisable(10754);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(2960);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glEnable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
        GL11.glEnable(3008);
        GL11.glPopAttrib();
    }

    public static void setColor(int colorHex) {
        float alpha = (float) (colorHex >> 24 & 255) / 255.0F;
        float red = (float) (colorHex >> 16 & 255) / 255.0F;
        float green = (float) (colorHex >> 8 & 255) / 255.0F;
        float blue = (float) (colorHex & 255) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }
    //draw box
    public static void drawBlockBox(final BlockPos blockPos, final Color color, final boolean outline) {
        final RenderManager renderManager = mc.getRenderManager();
        final Timer timer = mc.timer;

        final double x = blockPos.getX() - renderManager.renderPosX;
        final double y = blockPos.getY() - renderManager.renderPosY;
        final double z = blockPos.getZ() - renderManager.renderPosZ;

        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        final Block block = mc.theWorld.getBlockState(blockPos).getBlock();


        if (block != null) {
            final EntityPlayer player = mc.thePlayer;

            final double posX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) timer.renderPartialTicks;
            final double posY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) timer.renderPartialTicks;
            final double posZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) timer.renderPartialTicks;
            axisAlignedBB = block.getSelectedBoundingBox(mc.theWorld, blockPos)
                    .expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D)
                    .offset(-posX, -posY, -posZ);
        }

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        enableGlCap(GL_BLEND);
        disableGlCap(GL_TEXTURE_2D, GL_DEPTH_TEST);
        glDepthMask(false);

        glColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() != 255 ? color.getAlpha() : outline ? 26 : 35);
        drawFilledBox(axisAlignedBB);

        if (outline) {
            glLineWidth(1F);
            enableGlCap(GL_LINE_SMOOTH);
            glColor(color.getRGB());

            drawSelectionBoundingBox(axisAlignedBB);
        }

        GlStateManager.resetColor();
        glDepthMask(true);
        resetCaps();

    }
    public static void glColor(int color) {
        float f = (color >> 24 & 0xFF) / 255.0F;
        float f1 = (color >> 16 & 0xFF) / 255.0F;
        float f2 = (color >> 8 & 0xFF) / 255.0F;
        float f3 = (color & 0xFF) / 255.0F;
        GL11.glColor4f(f1, f2, f3, f);
    }
    public static void glColor(final int red, final int green, final int blue, final int alpha) {
        GlStateManager.color(red / 255F, green / 255F, blue / 255F, alpha / 255F);
    }
    public static void resetCaps() {
        glCapMap.forEach(RenderUtil::setGlState);
    }
    public static void enableGlCap(final int cap) {
        setGlCap(cap, true);
    }

    public static void enableGlCap(final int... caps) {
        for (final int cap : caps)
            setGlCap(cap, true);
    }

    public static void disableGlCap(final int cap) {
        setGlCap(cap, true);
    }

    public static void disableGlCap(final int... caps) {
        for (final int cap : caps)
            setGlCap(cap, false);
    }

    public static void setGlCap(final int cap, final boolean state) {
        glCapMap.put(cap, glGetBoolean(cap));
        setGlState(cap, state);
    }

    public static void setGlState(final int cap, final boolean state) {
        if (state)
            glEnable(cap);
        else
            glDisable(cap);
    }
    public static void drawFilledBox(final AxisAlignedBB axisAlignedBB) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
    }





    public static void drawImage(final ResourceLocation image, final float x, final float y, final float width, final float height) {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(image);
        final float f = 1.0f / width;
        final float f2 = 1.0f / height;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0).tex(0.0f * f, height * f2).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0).tex(width * f, height * f2).endVertex();
        worldrenderer.pos(x + width, y, 0.0).tex(width * f, 0.0f * f2).endVertex();
        worldrenderer.pos(x, y, 0.0).tex(0.0f * f, 0.0f * f2).endVertex();
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }


    public static void drawImage(final ResourceLocation image, final float x, final float y, final int width, final int height) {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(image);
        final float f = 1.0f / width;
        final float f2 = 1.0f / height;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0).tex(0.0f * f, height * f2).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0).tex(width * f, height * f2).endVertex();
        worldrenderer.pos(x + width, y, 0.0).tex(width * f, 0.0f * f2).endVertex();
        worldrenderer.pos(x, y, 0.0).tex(0.0f * f, 0.0f * f2).endVertex();
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static void drawImage(ResourceLocation imageLocation, double x, double y, double width, double height, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableAlpha();
        mc.getTextureManager().bindTexture(imageLocation);
        color(color);
        Gui.drawModalRectWithCustomSizedTexture((float) x, (float) y, (float) 0, (float) 0, (float) width, (float) height, (float) width, (float) height);
        GlStateManager.resetColor();
        GlStateManager.bindTexture(0);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }


    public static void drawImage2(final ResourceLocation image, final float x, final float y, final int width, final int height,final float alpha) {
        setTextureFliterTypeToLinear();
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
        mc.getTextureManager().bindTexture(image);
        final float f = 1.0f / width;
        final float f2 = 1.0f / height;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0).tex(0.0f * f, height * f2).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0).tex(width * f, height * f2).endVertex();
        worldrenderer.pos(x + width, y, 0.0).tex(width * f, 0.0f * f2).endVertex();
        worldrenderer.pos(x, y, 0.0).tex(0.0f * f, 0.0f * f2).endVertex();
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }
    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    //draw Image
    public static void drawImageRound(ResourceLocation imageLocation, double x, double y, double width, double height, int color, Runnable cutMethod) {
        GlStateManager.pushMatrix();
        StencilUtil.initStencilToWrite();
        cutMethod.run();
        StencilUtil.readStencilBuffer(1);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableAlpha();
        mc.getTextureManager().bindTexture(imageLocation);
        color(color);
        Gui.drawModalRectWithCustomSizedTexture((float) x, (float) y, (float) 0, (float) 0, (float) width, (float) height, (float) width, (float) height);
        GlStateManager.resetColor();
        GlStateManager.bindTexture(0);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        StencilUtil.uninitStencilBuffer();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    /**
     * 画大头
     *
     * @param x      x
     * @param y      y
     * @param width  宽度
     * @param height 高度
     * @param player 球员
     */
    public static void drawBigHead(float x, float y, float width, float height, AbstractClientPlayer player) {
        drawBigHead(x, y, width, height, 1F, player);
    }

    public static void drawBigHead(float x, float y, float width, float height, float opacity, AbstractClientPlayer player) {
        final double offset = -(player.hurtTime * 23);
        color(ColorUtil.getColor(255, (int) (255 + offset), (int) (255 + offset)), opacity);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        mc.getTextureManager().bindTexture(player.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(x, y, (float) 8.0, (float) 8.0, 8, 8, width, height, 64.0F, 64.0F);
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }

    public static void setSplashScreen(ScaledResolution sr, Framebuffer framebuffer) {
        framebuffer.bindFramebuffer(false);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, sr.getScaledWidth(), sr.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
    }

    public static void renderSplashScreen(ScaledResolution sr, Framebuffer framebuffer) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(sr.getScaledWidth() * sr.getScaleFactor(), sr.getScaledHeight() * sr.getScaleFactor());
    }

    /**
     * 画圆角大头
     *
     * @param x      x
     * @param y      y
     * @param width  宽度
     * @param height 高度
     * @param player 球员
     */
    public static void drawBigHeadRound(float x, float y, float width, float height, AbstractClientPlayer player) {
        drawBigHeadRound(x, y, width, height, 1F, player);
    }

    public static void drawBigHeadRound(float x, float y, float width, float height, float opacity, AbstractClientPlayer player) {
        StencilUtil.write(false);

        renderRoundedRect(x, y, width, height, 8, -1);
        StencilUtil.erase(true);

        color(-1);
        drawBigHead(x, y, width, height, opacity, player);
        StencilUtil.dispose();
        GlStateManager.disableBlend();
    }
    public static void drawESPRect(float left, float top, float right, float bottom, int color) {
        if (left < right) {
            float i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            float j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (color >> 24 & 0xFF) / 255.0f;
        float f = (color >> 16 & 0xFF) / 255.0f;
        float f1 = (color >> 8 & 0xFF) / 255.0f;
        float f2 = (color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0).endVertex();
        worldrenderer.pos(right, bottom, 0.0).endVertex();
        worldrenderer.pos(right, top, 0.0).endVertex();
        worldrenderer.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    public static void drawRectBordered(final double x, final double y, final double x1, final double y1, final double width, final int internalColor, final int borderColor) {
        rectangle(x + width, y + width, x1 - width, y1 - width, internalColor);
        rectangle(x + width, y, x1 - width, y + width, borderColor);
        rectangle(x, y, x + width, y1, borderColor);
        rectangle(x1 - width, y, x1, y1, borderColor);
        rectangle(x + width, y1 - width, x1 - width, y1, borderColor);
    }

    public static void rectangle(double left, double top, double right, double bottom, final int color) {
        if (left < right) {
            final double var5 = left;
            left = right;
            right = var5;
        }
        if (top < bottom) {
            final double var5 = top;
            top = bottom;
            bottom = var5;
        }
        final float var6 = (color >> 24 & 0xFF) / 255.0f;
        final float var7 = (color >> 16 & 0xFF) / 255.0f;
        final float var8 = (color >> 8 & 0xFF) / 255.0f;
        final float var9 = (color & 0xFF) / 255.0f;
        final WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var7, var8, var9, var6);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(left, bottom, 0.0).endVertex();
        worldRenderer.pos(right, bottom, 0.0).endVertex();
        worldRenderer.pos(right, top, 0.0).endVertex();
        worldRenderer.pos(left, top, 0.0).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static ScaledResolution getScaledResolution() {
        int displayWidth = Display.getWidth();
        int displayHeight = Display.getHeight();
        float guiScale = mc.gameSettings.guiScale;

        if (displayWidth != lastScaledWidth ||
                displayHeight != lastScaledHeight ||
                guiScale != lastGuiScale) {
            lastScaledWidth = displayWidth;
            lastScaledHeight = displayHeight;
            lastGuiScale = guiScale;
            return scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        }

        return scaledResolution;
    }




    public static void renderRoundedRect(float x, float y, float width, float height, float radius, int color) {
        drawGoodCircle(x + radius, y + radius, radius, color);
        drawGoodCircle(x + width - radius, y + radius, radius, color);
        drawGoodCircle(x + radius, y + height - radius, radius, color);
        drawGoodCircle(x + width - radius, y + height - radius, radius, color);

        Gui.drawRect3(x + radius, y, width - radius * 2, height, color);
        Gui.drawRect3(x, y + radius, width, height - radius * 2, color);
    }

    public static void drawCircleCGUI(double x, double y, float radius, int color) {
        if (radius == 0)
            return;
        final float correctRadius = radius * 2;
        GLUtil.setup2DRendering(() -> {
            glColor(color);
            glEnable(GL_POINT_SMOOTH);
            glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
            glPointSize(correctRadius);
            GLUtil.setupRendering(GL_POINTS, () -> glVertex2d(x, y));
            glDisable(GL_POINT_SMOOTH);
            GlStateManager.resetColor();
        });
    }

    public static void drawGoodCircle(double x, double y, float radius, int color) {

        color(color);
        setup2DRendering(() -> {
            glEnable(GL_POINT_SMOOTH);
            glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
            glPointSize(radius * (2 * Minecraft.getMinecraft().gameSettings.guiScale));
            GLUtil.render(GL_POINTS, () -> glVertex2d(x, y));
        });
    }

    public static void drawSolidBlockESP(double x, double y, double z, int color) {
        double xPos = x - mc.getRenderManager().renderPosX, yPos = y - mc.getRenderManager().renderPosY, zPos = z - mc.getRenderManager().renderPosZ;
        float f = (float) (color >> 16 & 0xFF) / 255.0f;
        float f2 = (float) (color >> 8 & 0xFF) / 255.0f;
        float f3 = (float) (color & 0xFF) / 255.0f;
        float f4 = (float) (color >> 24 & 0xFF) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(1.0f);
        GL11.glColor4f(f, f2, f3, f4);
        drawOutlinedBoundingBox(new AxisAlignedBB(xPos, yPos, zPos, xPos + 1.0, yPos + 1.0, zPos + 1.0));
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawOutlinedBoundingBox(AxisAlignedBB axisAlignedBB) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawSolidBlockESP(BlockPos pos, int color) {
        double xPos = pos.getX() - mc.getRenderManager().renderPosX, yPos = pos.getY() - mc.getRenderManager().renderPosY, zPos = pos.getZ() - mc.getRenderManager().renderPosZ;
        double height = mc.theWorld.getBlockState(pos).getBlock().getBlockBoundsMaxY() - mc.theWorld.getBlockState(pos).getBlock().getBlockBoundsMinY();
        float f = (float) (color >> 16 & 0xFF) / 255.0f;
        float f2 = (float) (color >> 8 & 0xFF) / 255.0f;
        float f3 = (float) (color & 0xFF) / 255.0f;
        float f4 = (float) (color >> 24 & 0xFF) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(1.0f);
        GL11.glColor4f(f, f2, f3, f4);
        drawOutlinedBoundingBox(new AxisAlignedBB(xPos, yPos, zPos, xPos + 1.0, yPos + height, zPos + 1.0));
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glDisable(3042);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(2929);
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    public static void drawLine(BlockPos blockPos, int color) {
        Minecraft mc = Minecraft.getMinecraft();
        double renderPosXDelta = blockPos.getX() - mc.getRenderManager().renderPosX + 0.5D;
        double renderPosYDelta = blockPos.getY() - mc.getRenderManager().renderPosY + 0.5D;
        double renderPosZDelta = blockPos.getZ() - mc.getRenderManager().renderPosZ + 0.5D;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.0F);
        float blockPos9 = (float) (mc.thePlayer.posX - (double) blockPos.getX());
        float blockPos7 = (float) (mc.thePlayer.posY - (double) blockPos.getY());
        float f = (float) (color >> 16 & 0xFF) / 255.0f;
        float f2 = (float) (color >> 8 & 0xFF) / 255.0f;
        float f3 = (float) (color & 0xFF) / 255.0f;
        float f4 = (float) (color >> 24 & 0xFF) / 255.0f;
        GL11.glColor4f(f, f2, f3, f4);
        GL11.glLoadIdentity();
        boolean previousState = mc.gameSettings.viewBobbing;
        mc.gameSettings.viewBobbing = false;
        mc.entityRenderer.orientCamera(mc.timer.renderPartialTicks);
        GL11.glBegin(3);
        GL11.glVertex3d(0.0D, mc.thePlayer.getEyeHeight(), 0.0D);
        GL11.glVertex3d(renderPosXDelta, renderPosYDelta, renderPosZDelta);
        GL11.glVertex3d(renderPosXDelta, renderPosYDelta, renderPosZDelta);
        GL11.glEnd();
        mc.gameSettings.viewBobbing = previousState;
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void enableRender3D(boolean disableDepth) {
        if (disableDepth) {
            GL11.glDepthMask(false);
            GL11.glDisable(2929);
        }

        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(1.0F);
    }

    public static void disableRender3D(boolean enableDepth) {
        if (enableDepth) {
            GL11.glDepthMask(true);
            GL11.glEnable(2929);
        }

        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDisable(2848);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }


    public static boolean isHovering(float x, float y, float width, float height, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public static boolean isInViewFrustrum(final Entity entity) {
        return (isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck);
    }

    private static boolean isInViewFrustrum(final AxisAlignedBB bb) {
        final Entity current = mc.getRenderViewEntity();
        FRUSTUM.setPosition(current.posX, current.posY, current.posZ);
        return FRUSTUM.isBoundingBoxInFrustum(bb);
    }


    public static void drawRoundedRect(float left, float top, float right, float bottom, float radius, int points, int color) {
        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        if (left < right) left = left + right - (right = left);
        if (top < bottom) top = top + bottom - (bottom = top);

        float[][] corners = {
                {right + radius, top - radius, 270},
                {left - radius, top - radius, 360},
                {left - radius, bottom + radius, 90},
                {right + radius, bottom + radius, 180}};

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.alphaFunc(516, 0.003921569F);
        GlStateManager.color(f, f1, f2, f3);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(GL_POLYGON, DefaultVertexFormats.POSITION);
        for (float[] c : corners) {
            for (int i = 0; i <= points; i++) {
                double anglerad = (Math.PI * (c[2] + i * 90.0F / points) / 180.0f);
                renderer.pos(c[0] + (Math.sin(anglerad) * radius), c[1] + (Math.cos(anglerad) * radius), 0).endVertex();
            }
        }

        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public static void drawCircle(Entity entity, double rad, boolean shade) {
        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        glDepthMask(false);
        GlStateManager.alphaFunc(GL_GREATER, 0);
        if (shade) glShadeModel(GL_SMOOTH);
        GlStateManager.disableCull();
        glBegin(GL_TRIANGLE_STRIP);

        final double x = MathUtils.interpolate2(entity.posX, entity.lastTickPosX, mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosX;
        final double y = (MathUtils.interpolate2(entity.posY, entity.lastTickPosY, mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosY) + Math.sin(System.currentTimeMillis() / 2E+2) + 1;
        final double z = MathUtils.interpolate2(entity.posZ, entity.lastTickPosZ, mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosZ;

        Color c;
        for (float i = 0; i < Math.PI * 2; i += Math.PI * 2 / 64) {
            final double vecX = x + rad * Math.cos(i);
            final double vecZ = z + rad * Math.sin(i);
            c = HUD.color(Math.round(i * 100) / 200);

            if (shade) {
                glColor4f(c.getRed() / 255F,
                        c.getGreen() / 255F,
                        c.getBlue() / 255F,
                        0
                );
                glVertex3d(vecX, y - Math.cos(System.currentTimeMillis() / 2E+2) / 2, vecZ);
                glColor4f(c.getRed() / 255F,
                        c.getGreen() / 255F,
                        c.getBlue() / 255F,
                        0.85F
                );
            }
            glVertex3d(vecX, y, vecZ);
        }
        glEnd();
        if (shade) glShadeModel(GL_FLAT);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        GlStateManager.alphaFunc(GL_GREATER, 0.1F);
        GlStateManager.enableCull();
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_POINT_SMOOTH);
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
        glColor3f(255, 255, 255);
    }

    public static void drawHorizontalGradientSideways(double x, double y, double width, double height, int leftColor, int rightColor) {
        setup2DRendering(() -> {
            glShadeModel(GL_SMOOTH);
            GLUtil.setupRendering(GL_QUADS, () -> {
                color(leftColor);
                glVertex2d(x, y);
                glVertex2d(x, y + height);
                color(rightColor);
                glVertex2d(x + width, y + height);
                glVertex2d(x + width, y);
                GlStateManager.resetColor();
            });
            glShadeModel(GL_FLAT);
        });
    }

    public static double[] getInterpolatedPosServer(Entity entity) {
        float ticks = mc.timer.renderPartialTicks;
        double d0 = entity.serverPosX / 32.0;
        double d2 = entity.serverPosY / 32.0;
        double d3 = entity.serverPosZ / 32.0;
        return new double[]{
                MathUtils.interpolate(entity.lastTickPosX, entity.serverPosX, ticks) - mc.getRenderManager().viewerPosX,
                MathUtils.interpolate(entity.lastTickPosY, entity.serverPosY, ticks) - mc.getRenderManager().viewerPosY,
                MathUtils.interpolate(entity.lastTickPosZ, entity.serverPosZ, ticks) - mc.getRenderManager().viewerPosZ
        };
    }

    public static AxisAlignedBB getInterpolatedBoundingBoxServer(Entity entity) {
        final double[] renderingEntityPos = getInterpolatedPosServer(entity);
        final double entityRenderWidth = entity.width / 1.5;
        return new AxisAlignedBB(renderingEntityPos[0] - entityRenderWidth,
                renderingEntityPos[1], renderingEntityPos[2] - entityRenderWidth, renderingEntityPos[0] + entityRenderWidth,
                renderingEntityPos[1] + entity.height + (entity.isSneaking() ? -0.3 : 0.18), renderingEntityPos[2] + entityRenderWidth).expand(0.15, 0.15, 0.15);
    }

    public static double[] getInterpolatedPos(Entity entity) {
        float ticks = mc.timer.renderPartialTicks;
        return new double[]{
                MathUtils.interpolate(entity.lastTickPosX, entity.posX, ticks) - mc.getRenderManager().viewerPosX,
                MathUtils.interpolate(entity.lastTickPosY, entity.posY, ticks) - mc.getRenderManager().viewerPosY,
                MathUtils.interpolate(entity.lastTickPosZ, entity.posZ, ticks) - mc.getRenderManager().viewerPosZ
        };
    }

    public static AxisAlignedBB getInterpolatedBoundingBox(Entity entity) {
        final double[] renderingEntityPos = getInterpolatedPos(entity);
        final double entityRenderWidth = entity.width / 1.5;
        return new AxisAlignedBB(renderingEntityPos[0] - entityRenderWidth,
                renderingEntityPos[1], renderingEntityPos[2] - entityRenderWidth, renderingEntityPos[0] + entityRenderWidth,
                renderingEntityPos[1] + entity.height + (entity.isSneaking() ? -0.3 : 0.18), renderingEntityPos[2] + entityRenderWidth).expand(0.05, 0.05, 0.05);
    }

    public static void renderBoundingBoxServer(EntityLivingBase entityLivingBase, Color color, float alpha) {
        AxisAlignedBB bb = getInterpolatedBoundingBoxServer(entityLivingBase);
        GlStateManager.pushMatrix();
        setup2DRendering();
        GLUtil.enableCaps(GL_BLEND, GL_POINT_SMOOTH, GL_POLYGON_SMOOTH, GL_LINE_SMOOTH);

        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(3);
        glColor4f(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        color(reAlpha(color, (int) alpha).getRGB());
        RenderGlobal.func_181561_a(bb, false, true);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);

        GLUtil.disableCaps();
        end2DRendering();

        GlStateManager.popMatrix();
    }

    public static void renderBoundingBox(EntityLivingBase entityLivingBase, Color color, float alpha) {
        AxisAlignedBB bb = getInterpolatedBoundingBox(entityLivingBase);
        GlStateManager.pushMatrix();
        setup2DRendering();
        GLUtil.enableCaps(GL_BLEND, GL_POINT_SMOOTH, GL_POLYGON_SMOOTH, GL_LINE_SMOOTH);

        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(3);
        glColor4f(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        color(reAlpha(color, (int) alpha).getRGB());
        RenderGlobal.func_181561_a(bb, false, true);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);

        GLUtil.disableCaps();
        end2DRendering();

        GlStateManager.popMatrix();
    }

    public static void drawVerticalGradientSideways(double x, double y, double width, double height, int topColor, int bottomColor) {
        setup2DRendering(() -> {
            glShadeModel(GL_SMOOTH);
            GLUtil.setupRendering(GL_QUADS, () -> {
                color(topColor);
                glVertex2d(x + width, y);
                glVertex2d(x, y);
                color(bottomColor);
                glVertex2d(x, y + height);
                glVertex2d(x + width, y + height);
                GlStateManager.resetColor();
            });
            glShadeModel(GL_FLAT);
        });
    }

    public static int width() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
    }

    public static int height() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
    }

    public static void color(int color, float alpha) {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GlStateManager.color(r, g, b, alpha);
    }

    public static void color(int color) {
        float f = (float) (color >> 24 & 255) / 255.0f;
        float f1 = (float) (color >> 16 & 255) / 255.0f;
        float f2 = (float) (color >> 8 & 255) / 255.0f;
        float f3 = (float) (color & 255) / 255.0f;
        GL11.glColor4f((float) f1, (float) f2, (float) f3, (float) f);
    }

    public static void startGlScissor(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        int scaleFactor = 1;
        int k = mc.gameSettings.guiScale;
        if (k == 0) {
            k = 1000;
        }
        while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        GL11.glPushMatrix();
        GL11.glEnable(3089);
        GL11.glScissor((int) (x * scaleFactor), (int) (mc.displayHeight - (y + height) * scaleFactor), (int) (width * scaleFactor), (int) (height * scaleFactor));
    }

    public static void stopGlScissor() {
        GL11.glDisable(3089);
        GL11.glPopMatrix();
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        RenderUtil.resetColor();
        RenderUtil.setAlphaLimit(0);
        GLUtil.setup2DRendering(true);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(left, bottom, 0.0D).color(color).endVertex();
        worldrenderer.pos( right, bottom, 0.0D).color(color).endVertex();
        worldrenderer.pos(right, top, 0.0D).color(color).endVertex();
        worldrenderer.pos(left, top, 0.0D).color(color).endVertex();
        tessellator.draw();
        GLUtil.end2DRendering();
    }

    public static void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }

    public static void drawGradientRectBordered(double left, double top, double right, double bottom, double width, int startColor, int endColor, int borderStartColor, int borderEndColor) {
        drawGradientRect(left + width, top + width, right - width, bottom - width, startColor, endColor);
        drawGradientRect(left + width, top, right - width, top + width, borderStartColor, borderEndColor);
        drawGradientRect(left, top, left + width, bottom, borderStartColor, borderEndColor);
        drawGradientRect(right - width, top, right, bottom, borderStartColor, borderEndColor);
        drawGradientRect(left + width, bottom - width, right - width, bottom, borderStartColor, borderEndColor);
    }
    public static void drawGradientRect(float x, float y, float width, float height, int firstColor, int secondColor, boolean perpendicular) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glPushMatrix();
        glShadeModel(GL_SMOOTH);
        glBegin(GL_QUADS);

        color(firstColor);
        glVertex2d(width, y);
        if(perpendicular)
            color(secondColor);
        glVertex2d(x, y);
        color(secondColor);
        glVertex2d(x, height);
        if(perpendicular)
            color(firstColor);
        glVertex2d(width, height);
        glEnd();
        glShadeModel(GL_FLAT);
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glPopMatrix();
    }


    public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor) {
        GLUtil.setup2DRendering();
        glEnable(GL_LINE_SMOOTH);
        glShadeModel(GL_SMOOTH);
        glPushMatrix();
        glBegin(GL_QUADS);
        color(startColor);
        glVertex2d(left, top);
        glVertex2d(left, bottom);
        color(endColor);
        glVertex2d(right, bottom);
        glVertex2d(right, top);
        glEnd();
        glPopMatrix();
        glDisable(GL_LINE_SMOOTH);
        GLUtil.end2DRendering();
        resetColor();
    }

    public static void drawRectWH(double x, double y, double width, double height, int color) {
        RenderUtil.resetColor();
        RenderUtil.setAlphaLimit(0);
        GLUtil.setup2DRendering(true);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(x, y, 0.0D).color(color).endVertex();
        worldrenderer.pos(x, y + height, 0.0D).color(color).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).color(color).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).color(color).endVertex();
        tessellator.draw();

        GLUtil.end2DRendering();
    }

    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }

    public static Color reAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static void scaleStart(float x, float y, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1);
        GlStateManager.translate(-x, -y, 0);
    }

    public static void scaleEnd() {
        GlStateManager.popMatrix();
    }

}
