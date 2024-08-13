package cc.xylitol.module.impl.combat;


import static cc.xylitol.utils.PacketUtil.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;
import org.lwjglx.input.Keyboard;
import org.lwjglx.util.vector.Vector2f;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;

import cc.xylitol.Client;
import cc.xylitol.event.annotations.EventPriority;
import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventAttack;
import cc.xylitol.event.impl.events.EventKey;
import cc.xylitol.event.impl.events.EventMotion;
import cc.xylitol.event.impl.events.EventRender3D;
import cc.xylitol.event.impl.events.EventTick;
import cc.xylitol.event.impl.events.EventUpdate;
import cc.xylitol.manager.PacketManager;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.module.impl.misc.AntiBot;
import cc.xylitol.module.impl.misc.Teams;
import cc.xylitol.module.impl.move.TargetStrafe;
import cc.xylitol.module.impl.player.Blink;
import cc.xylitol.module.impl.world.Scaffold;
import cc.xylitol.module.impl.world.Stuck;
import cc.xylitol.ui.hud.notification.NotificationManager;
import cc.xylitol.ui.hud.notification.NotificationType;
import cc.xylitol.utils.TimerUtil;
import cc.xylitol.utils.player.RotationUtil;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.utils.render.animation.Animation;
import cc.xylitol.utils.render.animation.Direction;
import cc.xylitol.utils.render.animation.impl.EaseBackIn;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.ColorValue;
import cc.xylitol.value.impl.ModeValue;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.viamcp.fixes.AttackOrder;
import top.fl0wowp4rty.phantomshield.annotations.Native;

public class KillAura extends Module {
    public ModeValue priority = new ModeValue("Priority", new String[]{"Range", "Fov", "Angle", "Health"}, "Range");
    public ModeValue mode = new ModeValue("Mode", new String[]{"Single", "Switch", "Multi"}, "Single");
    public ModeValue attackMode = new ModeValue("Attack Mode", new String[]{"Pre", "Post"}, "Post");

    public ModeValue rotMode = new ModeValue("Rotation Mode", new String[]{"Normal", "HvH", "Smart", "CNM", "Improved"}, "HvH");
    public ModeValue aimPoint = new ModeValue("Aim Point", getAimPointNames(), "Closest", () -> rotMode.is("Improved"));

    public ModeValue abMode = new ModeValue("AutoBlock mode", new String[]{"Off", "Grim", "Key Bind", "UseItem", "Watchdog 1.9+", "Fake"}, "Grim");
    public ModeValue markMode = new ModeValue("Mark mode", new String[]{"Off", "Box", "Circle", "Plat", "Exhi"}, "Box");

    public NumberValue cps = new NumberValue("CPS", 13.0, 1.0, 20.0, 1.0);
    public static NumberValue range = new NumberValue("Range", 3.00, 2.00, 6.00, 0.01);
    public static NumberValue blockRange = new NumberValue("Block Range", 4.0D, 2.00, 6.00, 0.01);

    public NumberValue scanRange = new NumberValue("Scan Range", 5.00, 2.00, 6.00, 0.01);

    public NumberValue switchDelay = new NumberValue("Switch delay", 500.0, 0.0, 1000.0, 10.0);
    public BoolValue moveFixValue = new BoolValue("Movement Fix", true);
    public BoolValue strictValue = new BoolValue("Follow Player (Strict)", false, () -> moveFixValue.getValue());
    public BoolValue strictAltValue = new BoolValue("Toggle strict when alt key", false, () -> moveFixValue.getValue() && strictValue.isAvailable());

    public static BoolValue rayCastValue = new BoolValue("RayCast", false);

    public BoolValue playerValue = new BoolValue("Player", true);
    public BoolValue animalValue = new BoolValue("Animal", false);
    public BoolValue mobValue = new BoolValue("Mob", false);
    public BoolValue invisibleValue = new BoolValue("Invisible", true);
    public NumberValue markAlpha = new NumberValue("MarkAlpha", 255.0, 1.0, 255.0, 1.0);
    public ColorValue markColor = new ColorValue("MarkColor", Color.RED.getRGB());

    private final TimerUtil switchTimer = new TimerUtil();
    private final TimerUtil attackTimer = new TimerUtil();
    public List<Entity> targets = new ArrayList<Entity>();
    private final Animation auraESPAnim = new EaseBackIn(300, 1, 1);

    public static EntityLivingBase target;
    public static boolean isBlocking = false;
    public static boolean renderBlocking = false;
    private int swordTicks = 0;


    private int blockTicks = 0;
    private long blockMask = 0;
    public int index = 0;

    public TargetStrafe ts;

    public KillAura() {
        super("KillAura", Category.Combat);
    }

    public String getTag() {
        return mode.getValue();
    }

    private boolean heldSword() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    @Override
    public void onEnable() {
        switchTimer.reset();
        isBlocking = false;
        renderBlocking = false;
        target = null;
        targets.clear();
        if (abMode.is("Watchdog")) Client.instance.packetManager.blink(false, PacketManager.BlinkFlag.AUTO_BLOCK);

        ts = getModule(TargetStrafe.class);
        if (isBlocking && !abMode.getValue().equals("Off")) {
            stopBlocking(true);
        }
        index = 0;
        auraESPAnim.setDirection(Direction.FORWARDS);

    }

    @Override
    public void onDisable() {
        if (abMode.is("Watchdog")) Client.instance.packetManager.blink(false, PacketManager.BlinkFlag.AUTO_BLOCK);
        auraESPAnim.setDirection(Direction.BACKWARDS);
        if (mc.thePlayer == null) return;
        target = null;
        targets.clear();
        if (isBlocking && !abMode.getValue().equals("Off")) {
            stopBlocking(true);
        }
        isBlocking = false;
        renderBlocking = false;
        index = 0;
    }


    @EventTarget
    public void onTick(EventTick e) {
        if (target instanceof EntityPlayer) {
            int blockStatus = (((EntityPlayer) target).isUsingItem() && target.getHeldItem().getItem() instanceof ItemSword) ? 1 : 0;
            blockMask = (blockMask << 1) | blockStatus;
            blockTicks += blockStatus;
            if ((blockMask & (1 << 20)) != 0) {
                blockTicks--;
                blockMask ^= (1 << 20);
            }
        }
    }

    @EventTarget
    @EventPriority(12)
    public void onKey(EventKey e) {
        if (this.getState() && e.getKey() == Keyboard.KEY_LMENU && moveFixValue.getValue() && strictValue.isAvailable() && strictAltValue.getValue()) {
            strictValue.set(!strictValue.getValue());
            NotificationManager.post(NotificationType.INFO, "Movement", "Set to " + (strictValue.getValue() ? "Strict" : "Silent") + ".");
        }
    }

    @EventTarget
    public void onR3D(EventRender3D event) {
        if (targets.isEmpty() || "Off".equals(markMode.getValue())) {
            auraESPAnim.setDirection(Direction.BACKWARDS);
            return; // 提前退出，避免不必要的代码执行
        }
        if (target != null) {


            // 以下是共享的计算，只需计算一次
            double partialTicks = event.getPartialTicks();
            double baseX = target.prevPosX + (target.posX - target.prevPosX) * partialTicks - RenderManager.renderPosX;
            double baseY = target.prevPosY + (target.posY - target.prevPosY) * partialTicks - RenderManager.renderPosY;
            double baseZ = target.prevPosZ + (target.posZ - target.prevPosZ) * partialTicks - RenderManager.renderPosZ;
            double tickOffsetX = (target.posX - target.prevPosX) * event.getPartialTicks();
            double tickOffsetY = (target.posY - target.prevPosY) * event.getPartialTicks();
            double tickOffsetZ = (target.posZ - target.prevPosZ) * event.getPartialTicks();

            double playerMotionOffsetX = mc.thePlayer.motionX + 0.01;
            double playerMotionOffsetY = mc.thePlayer.motionY - 0.005;
            double playerMotionOffsetZ = mc.thePlayer.motionZ + 0.01;
            int color = target.hurtTime > 3 ? new Color(235, 40, 40, 35).getRGB() : new Color(150, 255, 40, 35).getRGB();
            int color2 = this.target.hurtTime > 3 ? new Color(235, 40, 40, 75).getRGB() : this.target.hurtTime < 3 ? new Color(150, 255, 40, 35).getRGB() : new Color(255, 255, 255, 75).getRGB();

            auraESPAnim.setDirection(Direction.FORWARDS);


            for (Entity ent : targets) {
                AxisAlignedBB axisAlignedBB = target.getEntityBoundingBox();
                GlStateManager.pushMatrix();
                double translateX = baseX + tickOffsetX + playerMotionOffsetX;
                double translateY = baseY + tickOffsetY + playerMotionOffsetY;
                double translateZ = baseZ + tickOffsetZ + playerMotionOffsetZ;
                switch (markMode.getValue()) {
                    case "Box":
                        // 相关Box绘制代码
                        GL11.glShadeModel(7425);
                        GL11.glHint(3154, 4354);
                        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
                        RenderUtil.renderBoundingBox((EntityLivingBase) ent, markColor.getColorC(), markAlpha.getValue().intValue());
                        break;
                    case "Circle":
                        // 相关Circle绘制代码
                        if (mc.thePlayer.ticksExisted <= 5) break;
                        if (target != null && mc.theWorld != null) {
                            RenderUtil.drawCircle(target, 0.66, true);
                        }
                        break;
                    case "Plat":
                        // 相关Plat绘制代码
                        GlStateManager.translate(translateX, translateY, translateZ);
                        RenderUtil.drawAxisAlignedBB(new AxisAlignedBB(axisAlignedBB.minX - target.posX, axisAlignedBB.minY + target.getEyeHeight() + 0.11 - target.posY, axisAlignedBB.minZ - target.posZ, axisAlignedBB.maxX - target.posX, axisAlignedBB.maxY - 0.13 - target.posY, axisAlignedBB.maxZ - target.posZ), false, color);
                        break;
                    case "Exhi":
                        GlStateManager.translate(translateX, translateY, translateZ);
                        AxisAlignedBB offsetBB = axisAlignedBB.offset(-target.posX, -target.posY, -target.posZ).expand(0.1, 0.1, 0.1).offset(0.0, 0.1, 0.0);
                        RenderUtil.drawAxisAlignedBB(offsetBB, true, color2);
                        break;
                }

                RenderUtil.resetColor();
                GlStateManager.popMatrix();
            }

        }
    }

    @EventTarget
    public void rotations(final EventUpdate event) {
        if (!targets.isEmpty()) {
            if (this.index >= targets.size()) {
                this.index = 0;
            }
            if (mc.thePlayer.getClosestDistanceToEntity(targets.get(this.index)) <= range.getValue()) {
                target = (EntityLivingBase) targets.get(this.index);
            } else {
                target = (EntityLivingBase) targets.get(0);
            }
        }

        if (target != null && mc.thePlayer.getClosestDistanceToEntity(target) <= range.getValue()) {
            float[] rotation = getRot();
            Client.instance.rotationManager.setRotation(new Vector2f(rotation[0], rotation[1]), 180f, !ts.getState() && moveFixValue.getValue(), strictValue.getValue());
        }
    }

    private float[] getRot() {
        float[] rot = RotationUtil.getHVHRotation(target, range.getValue());
        switch (rotMode.getValue()) {
            case "Normal":
                Vector2f vec = RotationUtil.calculate(target, true, range.getValue() + 0.2f, range.getValue() + 0.2, true, true);
                rot = new float[]{vec.x, vec.y};
                break;
            case "CNM":
                rot = RotationUtil.getAngles(target);
                break;
            case "HvH":
                rot = RotationUtil.getHVHRotation(target, range.getValue());
                break;
            case "Smart":

                Vector3d targetPos; // i paste
                final double yDist = target.posY - mc.thePlayer.posY;
                if (yDist >= 1.7) {
                    targetPos = new Vector3d(target.posX, target.posY, target.posZ);
                } else if (yDist <= -1.7) {
                    targetPos = new Vector3d(target.posX, target.posY + target.getEyeHeight(), target.posZ);
                } else {
                    targetPos = new Vector3d(target.posX, target.posY + target.getEyeHeight() / 2, target.posZ);
                }
                Vector2f temp = RotationUtil.getRotationFromEyeToPoint(targetPos);
                rot = new float[]{temp.getX(), temp.getY()};
                break;
            case "Improved":
                final Vec3 hitOrigin = RotationUtil.getHitOrigin(this.mc.thePlayer);
                final Vec3 attackHitVec = this.getAttackHitVec(hitOrigin, target);

                rot = RotationUtil.getRotations(
                        Client.instance.rotationManager.lastRotation,
                        0f,
                        hitOrigin,
                        attackHitVec);
                break;
        }
        return rot;
    }

    private Vec3 getAttackHitVec(final Vec3 hitOrigin, final EntityLivingBase entity) {
        final AxisAlignedBB boundingBox = RotationUtil.getHittableBoundingBox(entity, 0);
        // Get optimal attack hit vec
        return RotationUtil.getAttackHitVec(this.mc, hitOrigin, boundingBox,
                getRAimPointByName(this.aimPoint.getValue()).getHitVec(hitOrigin, boundingBox),
                true, -1);
    }

    @EventTarget
    @EventPriority(9)
    public void onUpdate(final EventMotion event) {
        if ((getModule(Blink.class).getState() && getModule(Scaffold.class).getState() || getModule(Stuck.class).getState()))
            return;

        this.setSuffix(mode.getValue());


        if (event.isPost()) {
            if (mc.thePlayer.isDead || mc.thePlayer.isSpectator()) {
                return;
            }

            targets = getTargets(scanRange.getValue());

            if (targets.isEmpty()) {
                target = null;
            }
            sortTargets();


            if (targets.size() > 1 && mode.getValue().equals("Switch") || mode.getValue().equals("Multi")) {
                if (switchTimer.delay(switchDelay.getValue().longValue()) || mode.getValue().equals("Multi")) {
                    ++this.index;
                    switchTimer.reset();
                }
            }
            if (targets.size() > 1 && mode.getValue().equals("Single")) {
                if (mc.thePlayer.getClosestDistanceToEntity(target) > scanRange.getValue()) {
                    ++index;
                } else if (target.isDead) {
                    ++index;
                }
            }


        }
    }

    public static boolean shouldAttack() {
        final MovingObjectPosition movingObjectPosition = mc.objectMouseOver;

        return ((mc.thePlayer.canEntityBeSeen(target) ? mc.thePlayer.getClosestDistanceToEntity(target) : mc.thePlayer.getDistanceToEntity(target)) <= range.getValue()) && (((!rayCastValue.getValue()) || !mc.thePlayer.canEntityBeSeen(target)) ||
                (rayCastValue.getValue() && movingObjectPosition != null && movingObjectPosition.entityHit == target));
    }

    public static boolean shouldBlock() {
        return target != null && mc.thePlayer.getClosestDistanceToEntity(target) <= blockRange.getValue();
    }


    private void attack(EventMotion eventMotion) {

        if (shouldAttack() && attackTimer.hasTimeElapsed(700L / cps.getValue().intValue())) {
            Client.instance.eventManager.call(new EventAttack(target, true));
            AttackOrder.sendFixedAttackByPacket(mc.thePlayer, target);
            Client.instance.eventManager.call(new EventAttack(target, false));

            attackTimer.reset();
        }

    }

    @EventTarget
    public void onMotion(EventMotion event) {
        if (event.isPre()) {

            if ((shouldBlock() && !isBlocking) || (abMode.is("Grim") && shouldBlock())) {
                doBlock();
            }

            if (getModule(Blink.class).getState() && isBlocking) {
                stopBlocking(true);
            }
            //pre攻击格挡释放代码
            if (targets.isEmpty() && attackMode.is("Pre") && isBlocking) {
                stopBlocking(true);
            }

            //pre攻击的Attack代码
            if (attackMode.is("Pre") && target != null) {
                attack(event);
            }

            if (abMode.is("Watchdog 1.9+") && shouldBlock()) {
                doBlock();
            }

            if (isBlocking && abMode.is("Grim")) {
                mc.getNetHandler().addToSendQueueUnregisteredNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }

        } else {

            if (isBlocking && abMode.is("Grim")) {
                for (int i = 1; i <= 5; i++) {
                    mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    PacketWrapper useItemMainHand = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                    useItemMainHand.write(Type.VAR_INT, 1);
                    PacketUtil.sendToServer(useItemMainHand, Protocol1_8To1_9.class, true, true);
                }
            }

            if (abMode.is("Watchdog 1.9+") && !shouldBlock()) {
                stopBlocking(true);
            }
        }

        if (event.isPost() && attackMode.is("Post")) {
            if (target != null) {
                attack(event);
            } else if (targets.isEmpty() && isBlocking) {
                stopBlocking(true);
            }
        }
    }

    public boolean isSword() {
        return Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem() != null && Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }


    private void stopBlocking(boolean render) {
        if (isSword() && renderBlocking) {
            switch (abMode.getValue()) {
                case "Key Bind":
                case "Grim":
                case "Watchdog 1.9+":
                    mc.gameSettings.keyBindUseItem.pressed = false;
                    sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    break;
                case "Fake":
                    break;
            }
            if (render)
                renderBlocking = false;
            isBlocking = false;
        }
    }

    @Native
    private void doBlock() {
        if (isSword() && !getModule(Blink.class).getState()) {
            switch (abMode.getValue()) {

                case "Key Bind":
                    mc.gameSettings.keyBindUseItem.pressed = true;
                    break;
                case "UseItem":
                    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
                    break;
                case "Grim":
                    break;
                case "Watchdog 1.9+":
                    sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));

                    PacketWrapper use = PacketWrapper.create(29, null,
                            Via.getManager().getConnectionManager().getConnections().iterator().next());
                    use.write(Type.VAR_INT, 1);
                    PacketUtil.sendToServer(use, Protocol1_8To1_9.class, true, true);
                case "Fake":
                    break;
            }
            if (!abMode.is("Fake")) isBlocking = true;

            renderBlocking = true;
        }
    }


    public List<Entity> getTargets(Double value) {
        return Minecraft.getMinecraft().theWorld.loadedEntityList.stream().filter(e -> mc.thePlayer.getClosestDistanceToEntity(e) <= value && isValid(e, value)).collect(Collectors.toList());
    }


    public boolean isValid(Entity entity, double range) {
        if (mc.thePlayer.getClosestDistanceToEntity(entity) > range)
            return false;
        if (entity.isInvisible() && !invisibleValue.getValue())
            return false;
        if (!entity.isEntityAlive())
            return false;
        if (entity == Minecraft.getMinecraft().thePlayer || entity.isDead || Minecraft.getMinecraft().thePlayer.getHealth() == 0F)
            return false;
        if ((entity instanceof EntityMob || entity instanceof EntityGhast || entity instanceof EntityGolem
                || entity instanceof EntityDragon || entity instanceof EntitySlime) && mobValue.getValue())
            return true;
        if ((entity instanceof EntitySquid || entity instanceof EntityBat || entity instanceof EntityVillager)
                && animalValue.getValue())
            return true;
        if (entity instanceof EntityAnimal && animalValue.getValue())
            return true;
        if (AntiBot.isServerBot(entity)) {
            return false;
        }
        if (entity.getEntityId() == -8 || entity.getEntityId() == -1337) {
            return false;
        }
        if (Teams.isSameTeam(entity))
            return false;

        return entity instanceof EntityPlayer && playerValue.getValue() && !Client.instance.friendManager.isFriend(entity.getName());
    }

    private void sortTargets() {
        if (!targets.isEmpty()) {
            EntityPlayerSP thePlayer = mc.thePlayer;
            switch (priority.getValue()) {
                case "Range":
                    targets.sort((o1, o2) -> (int) (o1.getClosestDistanceToEntity(thePlayer) - o2.getClosestDistanceToEntity(thePlayer)));
                    break;
                case "Fov":
                    targets.sort(Comparator.comparingDouble(o -> this.getDistanceBetweenAngles(thePlayer.rotationPitch, RotationUtil.getRotationsNeeded(o)[0])));
                    break;
                case "Angle":
                    targets.sort((o1, o2) -> {
                        float[] rot1 = RotationUtil.getRotationsNeeded(o1);
                        float[] rot2 = RotationUtil.getRotationsNeeded(o2);
                        return (int) (thePlayer.rotationYaw - rot1[0] - (thePlayer.rotationYaw - rot2[0]));
                    });
                    break;
                case "Health":
                    targets.sort((o1, o2) -> (int) (((EntityLivingBase) o1).getHealth() - ((EntityLivingBase) o2).getHealth()));
                    break;
            }
        }
    }

    private float getDistanceBetweenAngles(float angle1, float angle2) {
        float agl = Math.abs(angle1 - angle2) % 360.0f;
        if (agl > 180.0f) {
            agl = 0.0f;
        }
        return agl - 1;
    }

    public static RotationsPoint getRAimPointByName(String name) {
        for (RotationsPoint point : RotationsPoint.values()) {
            if (point.name.equalsIgnoreCase(name)) {
                return point;
            }
        }
        return null;
    }

    public static String[] getAimPointNames() {
        RotationsPoint[] points = RotationsPoint.values();
        String[] names = new String[points.length];
        for (int i = 0; i < points.length; i++) {
            names[i] = points[i].name;
        }
        return names;
    }

    public enum RotationsPoint {
        CLOSEST("Closest", RotationUtil::getClosestPoint),
        HEAD("Head", (start, hitBox) -> {
            return RotationUtil.getCenterPointOnBB(hitBox, 0.9);
        }),
        CHEST("Chest", (start, hitBox) -> {
            return RotationUtil.getCenterPointOnBB(hitBox, 0.7);
        }),
        PELVIS("Pelvis", (start, hitBox) -> {
            return RotationUtil.getCenterPointOnBB(hitBox, 0.5);
        }),
        LEGS("Legs", (start, hitBox) -> {
            return RotationUtil.getCenterPointOnBB(hitBox, 0.3);
        }),
        FEET("Feet", (start, hitBox) -> {
            return RotationUtil.getCenterPointOnBB(hitBox, 0.1);
        });

        private final String name;
        private final BiFunction<Vec3, AxisAlignedBB, Vec3> getHitVecFunc;

        RotationsPoint(final String name, BiFunction<Vec3, AxisAlignedBB, Vec3> getHitVecFunc) {
            this.name = name;
            this.getHitVecFunc = getHitVecFunc;
        }

        public Vec3 getHitVec(final Vec3 start, final AxisAlignedBB hitBox) {
            return this.getHitVecFunc.apply(start, hitBox);
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
