package cc.xylitol.ui.gui.splash.utils;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.TextureUtils;
import org.lwjgl.opengl.GL11;
public class Image extends RenderableEntity {
    private final ITextureObject texture;
    @Getter
    @Setter
    public ResourceLocation image;
    @Getter
    @Setter
    public Type type;

    public Image(ResourceLocation image, double x, double y, double width, double height, Type type) {
        super(x, y, width, height);
        this.setType(type);
        image = this.getImg(image);
        this.setImage(image);

        ITextureObject texture1 = mc.getTextureManager().getTexture(image);
        if (texture1 == null) {
            texture = new SimpleTexture(image);
            mc.getTextureManager().loadTexture(image, texture);
        } else {
            texture = texture1;
        }
    }

    private static ResourceLocation getImg(ResourceLocation resLocIn) {

        return resLocIn;

    }

    public static void draw(ResourceLocation img, double x, double y, double width, double height, Type type) {

        img = getImg(img);

        if (type == Type.Normal) {
            GL11.glColor4f(1, 1, 1, 1);
        }
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
//        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        ITextureObject textureObj = Minecraft.getMinecraft().getTextureManager().getTexture(img);
        if (textureObj != null && textureObj != TextureUtil.missingTexture) {
//            System.out.println("NULL: " + img);
            TextureUtils.bindTexture(textureObj.getGlTextureId());
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        } else if (textureObj != TextureUtil.missingTexture) {
            textureObj = new SimpleTexture(img);
            Minecraft.getMinecraft().getTextureManager().loadTexture(img, textureObj);
        }

        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
    }

    public static void draw(ResourceLocation img, double x, double y, double width, double height, double tWidth, double tHeight, Type type) {

        img = getImg(img);

        if (type == Type.Normal) {
            GL11.glColor4f(1, 1, 1, 1);
        }
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        ITextureObject textureObj = Minecraft.getMinecraft().getTextureManager().getTexture(img);
        if (textureObj != null && textureObj != TextureUtil.missingTexture) {
//            System.out.println("NULL: " + img);
            TextureUtils.bindTexture(textureObj.getGlTextureId());
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, tWidth, tHeight, width, height);
        } else if (textureObj != TextureUtil.missingTexture) {
            textureObj = new SimpleTexture(img);
            Minecraft.getMinecraft().getTextureManager().loadTexture(img, textureObj);
        }
//        GL11.glDisable(GL13.GL_MULTISAMPLE);



        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
    }

    public static void draw(int textureId, double x, double y, double width, double height, Type type) {

        if (type == Type.Normal) {
            GL11.glColor4f(1, 1, 1, 1);
        }
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.bindTexture(textureId);

        drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);

//        GL11.glDisable(GL13.GL_MULTISAMPLE);



        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
    }

    public static void drawModalRectWithCustomSizedTexture(double x, double y, double u, double v, double width, double height, double textureWidth, double textureHeight)
    {
        double f = 1.0F / textureWidth;
        double f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex(u * f, (v + height) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex((u + width) * f, (v + height) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex((u + width) * f, v * f1).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    public void draw() {
//        GlStateManager.pushAttrib();
        if (type == Type.Normal) {
            GL11.glColor4f(1, 1, 1, 1);
        }
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        TextureUtils.bindTexture(texture.getGlTextureId());
        Gui.drawModalRectWithCustomSizedTexture(getX(), getY(), 0, 0, getWidth(), getHeight(), getWidth(), getHeight());
//        GL11.glDisable(GL13.GL_MULTISAMPLE);

        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
//        GlStateManager.color(1, 1, 1);
//        GlStateManager.popAttrib();
    }

    public enum Type {
        NoColor, Normal
    }
}
