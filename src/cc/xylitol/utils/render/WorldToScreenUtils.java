package cc.xylitol.utils.render;

import cc.xylitol.event.impl.events.EventRender2D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import org.lwjglx.util.glu.GLU;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

public class WorldToScreenUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void onRender2D(EventRender2D event, Entity entity, WorldToScreenCallback callback) {
        final ScaledResolution sr = event.getScaledResolution();
        final float renderPartialTicks = event.getPartialTicks();
        GlStateManager.pushMatrix();
        GLUtil.setup2DRendering(() -> {
            final double scaling = sr.getScaleFactor() / Math.pow(sr.getScaleFactor(), 2);
            GlStateManager.scale(scaling, scaling, scaling);
            if (isInViewFrustum(entity)) {
                final double x = interpolate(entity.lastTickPosX, entity.posX, renderPartialTicks);
                final double y = interpolate(entity.lastTickPosY, entity.posY, renderPartialTicks);
                final double z = interpolate(entity.lastTickPosZ, entity.posZ, renderPartialTicks);
                final float width = entity.width - .15F;
                final float height = entity.height + .15F - (entity instanceof EntityPlayer ? entity.isSneaking() ? .25F : 0 : 0);
                final AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                final List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ),
                        new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ),
                        new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ),
                        new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ),
                        new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));
                mc.entityRenderer.setupCameraTransform(renderPartialTicks, 0);
                Vector4d position = null;
                for (Vector3d vector : vectors) {
                    vector = worldToScreen(sr, vector.x - mc.getRenderManager().viewerPosX, vector.y - mc.getRenderManager().viewerPosY, vector.z - mc.getRenderManager().viewerPosZ);
                    if (vector != null && vector.z >= .0 && vector.z < 1) {
                        if (position == null)
                            position = new Vector4d(vector.x, vector.y, vector.z, .0);
                        position.x = Math.min(vector.x, position.x);
                        position.y = Math.min(vector.y, position.y);
                        position.z = Math.max(vector.x, position.z);
                        position.w = Math.max(vector.y, position.w);
                    }
                }
                mc.entityRenderer.setupOverlayRendering();
                if (position != null) {
                    final double posX = position.x;
                    final double posY = position.y;
                    final double endPosX = position.z;
                    final double endPosY = position.w;
                    callback.run(posX, posY, endPosX, endPosY);
                }
            }
            mc.entityRenderer.setupOverlayRendering();
            GlStateManager.resetColor();
        });
        GlStateManager.popMatrix();

        GlStateManager.resetColor();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
    }

    public static Double interpolate(double oldValue, double newValue, double interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    private static final Frustum frustum = new Frustum();

    private static boolean isInViewFrustum(Entity entity) {
        final AxisAlignedBB bb = entity.getEntityBoundingBox();

        frustum.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        return frustum.isBoundingBoxInFrustum(bb) || entity.ignoreFrustumCheck;
    }

    private static final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private static final FloatBuffer modelView = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);

    private static Vector3d worldToScreen(ScaledResolution sr, double x, double y, double z) {
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelView);
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);

        if (GLU.gluProject((float) x, (float) y, (float) z, modelView, projection, viewport, vector))
            return new Vector3d(vector.get(0) / sr.getScaleFactor(), (mc.displayHeight - vector.get(1)) / sr.getScaleFactor(), vector.get(2));

        return null;
    }

}
