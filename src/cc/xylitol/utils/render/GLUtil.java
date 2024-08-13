package cc.xylitol.utils.render;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import static net.minecraft.client.renderer.GlStateManager.glBegin;
import static net.minecraft.client.renderer.GlStateManager.glEnd;
import static org.lwjgl.opengl.GL11.*;

public class GLUtil {
    public static int[] enabledCaps = new int[32];

    public static void startBlend() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
    public static void setupRendering(int mode, Runnable runnable) {
        glBegin(mode);
        runnable.run();
        glEnd();
    }
    public static void enableDepth() {
        GL11.glDepthMask(true);
        glEnable(2929);
    }
    public static void disableCaps() {
        for (int cap : enabledCaps) glDisable(cap);
    }

    public static void enableCaps(int... caps) {
        for (int cap : caps) glEnable(cap);
        enabledCaps = caps;
    }
    public static void enableTexture2D() {
        glEnable(3553);
    }

    public static void disableTexture2D() {
        glDisable(3553);
    }

    public static void enableBlending() {
        glEnable(3042);
        GL11.glBlendFunc(770, 771);
    }
    public static void disableDepth() {
        GL11.glDepthMask(false);
        glDisable(2929);
    }
    public static void disableBlending() {
        glDisable(3042);
    }

    public static void endBlend() {
        GlStateManager.disableBlend();
    }
    public static void render(int mode, Runnable render){
        glBegin(mode);
        render.run();
        glEnd();
    }

    public static void setup2DRendering(Runnable f) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_TEXTURE_2D);
        f.run();
        glEnable(GL_TEXTURE_2D);
        GlStateManager.disableBlend();
    }
    public static void setup2DRendering() {
        setup2DRendering(true);
    }
    public static void setup2DRendering(boolean blend) {
        if (blend) {
            startBlend();
        }
        GlStateManager.disableTexture2D();
    }
    public static void end2DRendering() {
        GlStateManager.enableTexture2D();
        endBlend();
    }

    public static void rotate(float x, float y, float rotate, Runnable f) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.rotate(rotate, 0, 0, -1);
        GlStateManager.translate(-x, -y, 0);
        f.run();
        GlStateManager.popMatrix();
    }


}
