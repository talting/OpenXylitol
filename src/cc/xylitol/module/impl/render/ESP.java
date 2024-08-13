package cc.xylitol.module.impl.render;

import cc.xylitol.Client;
import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventRender2D;
import cc.xylitol.event.impl.events.EventRender3D;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.module.impl.misc.AntiBot;
import cc.xylitol.module.impl.misc.Teams;
import cc.xylitol.module.impl.player.Blink;
import cc.xylitol.ui.font.FontManager;
import cc.xylitol.ui.font.RapeMasterFontManager;
import cc.xylitol.utils.render.ColorUtil;
import cc.xylitol.utils.render.Colors;
import cc.xylitol.utils.render.ESPUtil;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.utils.render.shader.ShaderElement;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjglx.opengl.Display;
import org.lwjglx.util.glu.GLU;
import org.lwjglx.util.vector.Vector4f;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.*;

import static cc.xylitol.utils.math.MathUtils.DF_1;

public class ESP extends Module {
    public ESP() {
        super("ESP", Category.Render);
    }

    private final BoolValue armorValue = new BoolValue("Armor", true);
    private final BoolValue healthValue = new BoolValue("Health", true);
    private final BoolValue boxValue = new BoolValue("Box", true);
    private final BoolValue nameValue = new BoolValue("Name", true);
    private final NumberValue width2d = new NumberValue("BoxWidth", 0.5, 0.1, 1.0, 0.1);
    private final DecimalFormat decimalFormat = new DecimalFormat("0.0#", new DecimalFormatSymbols(Locale.ENGLISH));
    private static final FloatBuffer modelView = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private static final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final List<Vec3> positions = new ArrayList<>();
    private final Map<Entity, Vector4f> entityPosition = new HashMap<>();

    @EventTarget
    public void onRender3D(EventRender3D event) {
        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer || this.isValid(entity)) {
                updateView();
            }
        }
    }

    private boolean shouldRender(Entity entity) {
        if (entity.isDead || entity.isInvisible()) {
            return false;
        }
        if (AntiBot.isServerBot(entity)) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            if (entity == mc.thePlayer) {
                return mc.gameSettings.thirdPersonView != 0;
            }
            return !entity.getDisplayName().getUnformattedText().contains("[NPC");
        }
        return false;
    }

    @EventTarget
    public void onRender3DEvent(EventRender3D e) {

        entityPosition.clear();
        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (shouldRender(entity) && ESPUtil.isInView(entity)) {
                entityPosition.put(entity, ESPUtil.getEntityPositionsOn2D(entity));
            }
        }
    }

    @EventTarget
    public void onRender2DEvent(EventRender2D e) {
        final ScaledResolution sr = RenderUtil.getScaledResolution();
        GlStateManager.pushMatrix();
        final double twoScale = sr.getScaleFactor() / Math.pow(sr.getScaleFactor(), 2.0);
        GlStateManager.scale(twoScale, twoScale, twoScale);
        for (final EntityPlayer entity : getLoadedPlayers()) {
            if (this.isValid(entity) && RenderUtil.isInViewFrustrum(entity)) {
                this.updatePositions(entity);
                int maxLeft = Integer.MAX_VALUE;
                int maxRight = Integer.MIN_VALUE;
                int maxBottom = Integer.MIN_VALUE;
                int maxTop = Integer.MAX_VALUE;
                final Iterator<Vec3> iterator2 = this.positions.iterator();
                boolean canEntityBeSeen = false;
                while (iterator2.hasNext()) {
                    final Vec3 screenPosition = WorldToScreen(iterator2.next());
                    if (screenPosition != null && screenPosition.zCoord >= 0.0 && screenPosition.zCoord < 1.0) {
                        maxLeft = (int) Math.min(screenPosition.xCoord, maxLeft);
                        maxRight = (int) Math.max(screenPosition.xCoord, maxRight);
                        maxBottom = (int) Math.max(screenPosition.yCoord, maxBottom);
                        maxTop = (int) Math.min(screenPosition.yCoord, maxTop);
                        canEntityBeSeen = true;
                    }
                }
                if (canEntityBeSeen) {
                    if (this.healthValue.getValue()) {
                        this.drawHealth(entity, (float) maxLeft, (float) maxTop, (float) maxBottom);
                    }
                    if (this.armorValue.getValue()) {
                        this.drawArmor(entity, (float) maxTop, (float) maxRight, (float) maxBottom);
                    }
                    if (this.boxValue.getValue()) {
                        this.drawBox(maxLeft, maxTop, maxRight, maxBottom);
                    }
                    /*if (this.nameValue.getValue()) {
                        GlStateManager.scale(1, 1, 1);
                        this.drawName(entity, maxLeft, maxTop, maxRight);
                    }*/
                }
            }
        }
        GlStateManager.popMatrix();


        for (Entity entity : entityPosition.keySet()) {
            Vector4f pos = entityPosition.get(entity);
            float x = pos.getX(),
                    y = pos.getY(),
                    right = pos.getZ();

            if (entity instanceof EntityLivingBase) {
                if (this.nameValue.getValue()) {
                    this.drawName(entity, (int) x, (int) y, (int) right);
                }
            }

        }
    }

    private void drawName(final Entity e, final int left, final int top, final int right) {
        Vector4f pos = entityPosition.get(e);

        RapeMasterFontManager font = FontManager.font18;
        EntityLivingBase renderingEntity = (EntityLivingBase) e;
        String rank = "";
        // 仅计算一次是否同队以减少重复调用
        boolean isSameTeam = Client.instance.moduleManager.getModule(Teams.class).getState() && Teams.isSameTeam(renderingEntity);

        if (renderingEntity == mc.thePlayer) {
            rank = "§a[You] ";
        }  else {
            rank = "§a[Team]";
        }
/*
        if (IRC.client.isConnected()) {
            for (IRCUser user : IRC.client.getOnlineUsers()) {
                if (renderingEntity.getName().equals(user.getIngamename())) {
                    rank = "§a[" + user.getUsername() + "] " + rank;
                    break;
                }
            }
        }*/
        String formattedRank = !rank.isEmpty() ? rank + " " : "";
        String name = renderingEntity.getDisplayName().getFormattedText();
        String health = " " + DF_1.format(renderingEntity.getHealth()); // 前后添加空格以便与其他文本分隔
        String formattedText = formattedRank + name + health;

        float textWidth = FontManager.font16.getStringWidth(formattedText);
        float extraSpace = 16;
        float width = textWidth + extraSpace;
        float height = 14;
        float x = pos.x;
        float y = pos.y;
//            RoundedUtil.drawRound(x, y, width, height, 3f, new Color(0, 0, 0, 150));
        RenderUtil.drawRectWH(x, y, width, height, ColorUtil.getColor(0, 0, 0, 76));

        ShaderElement.addBlurTask(() -> RenderUtil.drawRectWH(x, y, width, height, -1));
        ShaderElement.addBloomTask(() -> RenderUtil.drawRectWH(x, y, width, height, ColorUtil.getColor(0, 0, 0, 200)));

        RenderUtil.drawRectWH(x, y + 3, 1, 8, HUD.color(1).getRGB());

        FontManager.font16.drawString(formattedText, x + 6f, y + 4, -1);
    }

    public static List<EntityPlayer> getLoadedPlayers() {
        return mc.theWorld.playerEntities;
    }

    private void drawBox(int left, int top, int right, int bottom) {
        RenderUtil.drawRectBordered(left + 0.5, top + 0.5, right - 0.5, bottom - 0.5, 1.0, Colors.getColor(0, 0, 0, 0), Colors.WHITE);
        RenderUtil.drawRectBordered(left - 0.5, top - 0.5, right + 0.5, bottom + 0.5, 1.0, Colors.getColor(0, 0), Colors.getColor(0));
        RenderUtil.drawRectBordered(left + 1.5, top + 1.5, right - 1.5, bottom - 1.5, 1.0, Colors.getColor(0, 0), Colors.getColor(0));
    }

    private void drawArmor(final EntityLivingBase entityLivingBase, final float top, final float right, final float bottom) {
        final float height = bottom + 1.0f - top;
        final float currentArmor = (float) entityLivingBase.getTotalArmorValue();
        final float armorPercent = currentArmor / 20.0f;
        final float MOVE = 2.0f;
        final int line = 1;
        RenderUtil.drawESPRect(right + 2.0f + 1.0f + MOVE, top - 2.0f, right + 1.0f - 1.0f + MOVE, bottom + 1.0f, new Color(25, 25, 25, 150).getRGB());
        RenderUtil.drawESPRect(right + 3.0f + MOVE, top + height * (1.0f - armorPercent) - 1.0f, right + 1.0f + MOVE, bottom, new Color(78, 206, 229).getRGB());
        RenderUtil.drawESPRect(right + 3.0f + MOVE + line, bottom + 1.0f, right + 3.0f + MOVE, top - 2.0f, new Color(0, 0, 0, 255).getRGB());
        RenderUtil.drawESPRect(right + 1.0f + MOVE, bottom + 1.0f, right + 1.0f + MOVE - line, top - 2.0f, new Color(0, 0, 0, 255).getRGB());
        RenderUtil.drawESPRect(right + 1.0f + MOVE, top - 1.0f, right + 3.0f + MOVE, top - 2.0f, new Color(0, 0, 0, 255).getRGB());
        RenderUtil.drawESPRect(right + 1.0f + MOVE, bottom + 1.0f, right + 3.0f + MOVE, bottom, new Color(0, 0, 0, 255).getRGB());
    }

    private void drawHealth(final EntityLivingBase entityLivingBase, final float left, final float top, final float bottom) {
        final float height = bottom + 1.0f - top;
        final float currentHealth = entityLivingBase.getHealth();
        final float maxHealth = entityLivingBase.getMaxHealth();
        final float healthPercent = currentHealth / maxHealth;
        final float MOVE = 2.0f;
        final int line = 1;
        final String healthStr = "§f" + this.decimalFormat.format(currentHealth) + "§c❤";
        final float bottom2 = top + height * (1.0f - healthPercent) - 1.0f;
        final float health = entityLivingBase.getHealth();
        final float[] fractions = {0.0f, 0.5f, 1.0f};
        final Color[] colors = {Color.RED, Color.YELLOW, Color.GREEN};
        final float progress = health / entityLivingBase.getMaxHealth();
        final Color customColor = (health >= 0.0f) ? Colors.blendColors(fractions, colors, progress).brighter() : Color.RED;
        mc.fontRendererObj.drawStringWithShadow(healthStr, left - 3.0f - MOVE - mc.fontRendererObj.getStringWidth(healthStr), bottom2, -1);
        RenderUtil.drawESPRect(left - 3.0f - MOVE, bottom, left - 1.0f - MOVE, top - 1.0f, new Color(25, 25, 25, 150).getRGB());
        RenderUtil.drawESPRect(left - 3.0f - MOVE, bottom, left - 1.0f - MOVE, bottom2, customColor.getRGB());
        RenderUtil.drawESPRect(left - 3.0f - MOVE, bottom + 1.0f, left - 3.0f - MOVE - line, top - 2.0f, new Color(0, 0, 0, 255).getRGB());
        RenderUtil.drawESPRect(left - 1.0f - MOVE + line, bottom + 1.0f, left - 1.0f - MOVE, top - 2.0f, new Color(0, 0, 0, 255).getRGB());
        RenderUtil.drawESPRect(left - 3.0f - MOVE, top - 1.0f, left - 1.0f - MOVE, top - 2.0f, new Color(0, 0, 0, 255).getRGB());
        RenderUtil.drawESPRect(left - 3.0f - MOVE, bottom + 1.0f, left - 1.0f - MOVE, bottom, new Color(0, 0, 0, 255).getRGB());
    }

    private int getColor(EntityLivingBase ent) {
        if (AntiBot.isServerBot(ent)) {
            return new Color(255, 0, 0).getRGB();
        }
        if (Teams.isSameTeam(ent) || ent instanceof EntityPlayerSP) {
            return new Color(0, 255, 0).getRGB();
        }
        return new Color(255, 0, 0).getRGB();
    }

    private static Vec3 WorldToScreen(final Vec3 position) {
        final FloatBuffer screenPositions = BufferUtils.createFloatBuffer(3);
        final boolean result = GLU.gluProject((float) position.xCoord, (float) position.yCoord, (float) position.zCoord, ESP.modelView, ESP.projection, ESP.viewport, screenPositions);
        if (result) {
            return new Vec3(screenPositions.get(0), Display.getHeight() - screenPositions.get(1), screenPositions.get(2));
        }
        return null;
    }

    public void updatePositions(final Entity entity) {
        this.positions.clear();
        final Vec3 position = getEntityRenderPosition(entity);
        final double x = position.xCoord - entity.posX;
        final double y = position.yCoord - entity.posY;
        final double z = position.zCoord - entity.posZ;
        final double height = (entity instanceof EntityItem) ? 0.5 : (entity.height + 0.1);
        final double width = (entity instanceof EntityItem) ? 0.25 : this.width2d.getValue();
        final AxisAlignedBB aabb = new AxisAlignedBB(entity.posX - width + x, entity.posY + y, entity.posZ - width + z, entity.posX + width + x, entity.posY + height + y, entity.posZ + width + z);
        this.positions.add(new Vec3(aabb.minX, aabb.minY, aabb.minZ));
        this.positions.add(new Vec3(aabb.minX, aabb.minY, aabb.maxZ));
        this.positions.add(new Vec3(aabb.minX, aabb.maxY, aabb.minZ));
        this.positions.add(new Vec3(aabb.minX, aabb.maxY, aabb.maxZ));
        this.positions.add(new Vec3(aabb.maxX, aabb.minY, aabb.minZ));
        this.positions.add(new Vec3(aabb.maxX, aabb.minY, aabb.maxZ));
        this.positions.add(new Vec3(aabb.maxX, aabb.maxY, aabb.minZ));
        this.positions.add(new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ));
    }

    private static Vec3 getEntityRenderPosition(final Entity entity) {
        return new Vec3(getEntityRenderX(entity), getEntityRenderY(entity), getEntityRenderZ(entity));
    }

    private static double getEntityRenderX(final Entity entity) {
        return entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * Minecraft.getMinecraft().timer.renderPartialTicks - Minecraft.getMinecraft().getRenderManager().renderPosX;
    }

    private static double getEntityRenderY(final Entity entity) {
        return entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * Minecraft.getMinecraft().timer.renderPartialTicks - Minecraft.getMinecraft().getRenderManager().renderPosY;
    }

    private static double getEntityRenderZ(final Entity entity) {
        return entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * Minecraft.getMinecraft().timer.renderPartialTicks - Minecraft.getMinecraft().getRenderManager().renderPosZ;
    }

    private boolean isValid(final Entity entity) {
        final Blink blink = (Blink) Client.instance.moduleManager.getModule(Blink.class);
        if (entity == mc.thePlayer && mc.gameSettings.thirdPersonView == 0) {
            return false;
        }
        if (entity.isInvisible()) {
            return false;
        }
        if (entity instanceof EntityArmorStand) {
            return false;
        }
        if (blink.getState()) {
            return true;
        }
        return entity instanceof EntityPlayer;
    }

    private static void updateView() {
        GL11.glGetFloatv(2982, modelView);
        GL11.glGetFloatv(2983, projection);
        GL11.glGetIntegerv(2978, viewport);
    }
}