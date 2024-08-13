package cc.xylitol.module.impl.render;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventRLE;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.ColorValue;
import cc.xylitol.value.impl.ModeValue;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Chams extends Module {


    public static ColorValue chamsColours = new ColorValue("ChamsColor", Color.RED.getRGB());
    public static ModeValue mode = new ModeValue("ChamsMode", new String[]{"Normal", "Colored"}, "Normal");
    public static BoolValue flat = new BoolValue("Flat", false);
    public static BoolValue teamCol = new BoolValue("TeamColor", false);
    public static NumberValue alpha = new NumberValue("Alpha", 170, 0, 255, 1);

    public Chams() {
        super("Chams", Category.Render);
    }

    @EventTarget
    public void onRenderLivingEntity(EventRLE evt) {
        if (evt.getEntity() != mc.thePlayer) {
            if (evt.isPre()) {
                if (mode.getValue().equals("Colored")) {
                    evt.setCancelled(true);
                    try {
                        Render<Entity> renderObject = mc.getRenderManager().getEntityRenderObject(evt.getEntity());
                        if (renderObject != null && mc.getRenderManager().renderEngine != null && renderObject instanceof RendererLivingEntity) {
                            GL11.glPushMatrix();
                            GL11.glDisable(GL11.GL_DEPTH_TEST);
                            GL11.glBlendFunc(770, 771);
                            GL11.glDisable(GL11.GL_TEXTURE_2D);
                            GL11.glEnable(GL11.GL_BLEND);
                            Color teamColor = null;

                            if (flat.getValue()) {
                                GlStateManager.disableLighting();
                            }

                            if (teamCol.getValue()) {
                                String text = evt.getEntity().getDisplayName().getFormattedText();
                                for (int i = 0; i < text.length(); i++) {
                                    if ((text.charAt(i) == (char) 0x00A7) && (i + 1 < text.length())) {
                                        char oneMore = Character.toLowerCase(text.charAt(i + 1));
                                        int colorCode = "0123456789abcdefklmnorg".indexOf(oneMore);
                                        if (colorCode < 16) {
                                            try {
                                                Color newCol = teamColor = new Color(mc.fontRendererObj.colorCode[colorCode]);
                                                GL11.glColor4f(newCol.getRed() / 255f, newCol.getGreen() / 255f, newCol.getBlue() / 255f, alpha.getValue().floatValue() / 255f);
                                            } catch (ArrayIndexOutOfBoundsException exception) {
                                                GL11.glColor4f(1, 0, 0, alpha.getValue().floatValue() / 255f);
                                            }
                                        }
                                    }
                                }
                            } else {
                                int c = RenderUtil.reAlpha(new Color(chamsColours.getValue()), alpha.getValue().intValue()).getRGB();
                                RenderUtil.color(c);
                            }


                            ((RendererLivingEntity) renderObject).renderModel(evt.getEntity(), evt.getLimbSwing(), evt.getLimbSwingAmount(), evt.getAgeInTicks(), evt.getRotationYawHead(), evt.getRotationPitch(), evt.getOffset());
                            GL11.glEnable(GL11.GL_DEPTH_TEST);

                            if (teamCol.getValue() && teamColor != null) {
                                GL11.glColor4f(teamColor.getRed() / 255f, teamColor.getGreen() / 255f, teamColor.getBlue() / 255f, alpha.getValue().floatValue() / 255f);
                            } else {
                                int c = chamsColours.getValue();
                                GL11.glColor4f(new Color(c).getRGB(), new Color(c).getRGB(), new Color(c).getRGB(), alpha.getValue().floatValue() / 255f);
                            }

                            ((RendererLivingEntity) renderObject).renderModel(evt.getEntity(), evt.getLimbSwing(), evt.getLimbSwingAmount(), evt.getAgeInTicks(), evt.getRotationYawHead(), evt.getRotationPitch(), evt.getOffset());
                            GL11.glEnable(GL11.GL_TEXTURE_2D);
                            GL11.glDisable(GL11.GL_BLEND);
                            GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha.getValue().floatValue() / 255f);

                            if (flat.getValue()) {
                                GlStateManager.enableLighting();
                            }

                            GL11.glPopMatrix();
                            ((RendererLivingEntity) renderObject).renderLayers(evt.getEntity(), evt.getLimbSwing(), evt.getLimbSwingAmount(), mc.timer.renderPartialTicks, evt.getAgeInTicks(), evt.getRotationYawHead(), evt.getRotationPitch(), evt.getOffset());
                            GL11.glPopMatrix();
                        }
                    } catch (Exception ignored) {
                    }
                } else {
                    GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
                    GL11.glPolygonOffset(1.0F, -1100000.0F);
                }
            } else if (mode.getValue() != "Colored" && evt.isPost()) {
                GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                GL11.glPolygonOffset(1.0F, 1100000.0F);
            }
        }
    }
}
