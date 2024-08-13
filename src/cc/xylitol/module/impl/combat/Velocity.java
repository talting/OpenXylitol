package cc.xylitol.module.impl.combat;

import cc.xylitol.Client;
import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventAttack;
import cc.xylitol.event.impl.events.EventLivingUpdate;
import cc.xylitol.event.impl.events.EventMotion;
import cc.xylitol.event.impl.events.EventPacket;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.module.impl.player.Blink;
import cc.xylitol.ui.hud.notification.NotificationManager;
import cc.xylitol.ui.hud.notification.NotificationType;
import cc.xylitol.utils.DebugUtil;
import cc.xylitol.utils.PacketUtil;
import cc.xylitol.utils.TimerUtil;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.ModeValue;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.*;
import net.minecraft.world.WorldSettings;
import net.vialoadingbase.ViaLoadingBase;
import net.viamcp.fixes.AttackOrder;
//import top.fl0wowp4rty.phantomshield.annotations.Native;

import javax.vecmath.Vector2d;


//@Native
public class Velocity extends Module {

    private ModeValue mode = new ModeValue("Mode", new String[]{"Grim", "Watchdog", "Watchdog Ignore"}, "Grim");

    private final BoolValue flagCheckValue = new BoolValue("Flag Check", false);
    public NumberValue flagTicksValue = new NumberValue("Flag Ticks", 6.0, 0.0, 30.0, 1.0);
    public BoolValue debugMessageValue = new BoolValue("Flag Message", false);
    public NumberValue attackCountValue = new NumberValue("Attack Counts", 12.0, 1.0, 16.0, 1.0);

    // pit 调成攻击发包调成6

    private final BoolValue fireCheckValue = new BoolValue("FireCheck", false);
    private final BoolValue waterCheckValue = new BoolValue("WaterCheck", false);
    private final BoolValue fallCheckValue = new BoolValue("FallCheck", false);
    private final BoolValue consumecheck = new BoolValue("ConsumableCheck", false);
    private final BoolValue raycastValue = new BoolValue("Ray cast", false);


    private TimerUtil timer = new TimerUtil();
    private TimerUtil flagtimer = new TimerUtil();

    public Velocity() {
        super("Velocity", Category.Combat);
    }

    public boolean velocityInput;
    private boolean grim_1_17Velocity;
    private boolean attacked;
    private double reduceXZ;
    private int flags;

    @Override
    public void onEnable() {
        velocityInput = false;
        attacked = false;
    }

    @EventTarget
    public void onPre(EventMotion event) {
        if (event.isPre()) {
            if (mode.is("Watchdog")) {
                if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava())
                    return;

                if (mc.thePlayer.hurtTime > 8) {
                    mc.thePlayer.motionX = -MathHelper.sin((float) mc.thePlayer.rotationYaw) * 0.5;
                    mc.thePlayer.motionZ = MathHelper.cos((float) mc.thePlayer.rotationYaw) * 0.5;
                } else if (timer.hasTimeElapsed(80L))
                    return;

            }
        }
    }

    @EventTarget
    public void onUpdate(EventLivingUpdate event) {
        this.setSuffix(mode.is("Grim") ? (ViaLoadingBase.getInstance().getTargetVersion().getVersion() >= 755 ? "Grim1.17+" : "Reduce") : mode.getValue());
        switch (mode.getValue()) {
            case "Grim": {
                if (grim_1_17Velocity) {
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(mc.thePlayer).up(), EnumFacing.DOWN));
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(mc.thePlayer).up(), EnumFacing.DOWN));
                    grim_1_17Velocity = false;
                }
                if (flagCheckValue.getValue()) {
                    if (flags > 0)
                        flags--;
                }
                if (ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 47) {

                    if (velocityInput) {

                        if (attacked) {
                            mc.thePlayer.motionX *= reduceXZ;
                            mc.thePlayer.motionZ *= reduceXZ;
                            attacked = false;
                        }
                        if (mc.thePlayer.hurtTime == 0) {
                            velocityInput = false;
                        }

                    }


                } else {
                    //The velocity mode 1.8.9 ok!
                    if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.onGround) {
                        mc.thePlayer.addVelocity(-1.3E-10, -1.3E-10, -1.3E-10);
                        mc.thePlayer.setSprinting(false);
                    }
                }
            }
            break;
        }
    }

    //Player
    @EventTarget
    public void onPacket(EventPacket event) {
        if (mc.thePlayer == null) return;

        Packet<?> packet = event.getPacket();

        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            flagtimer.reset();
            if (flagCheckValue.getValue()) {
                flags = flagTicksValue.getValue().intValue();
                if (debugMessageValue.getValue())
                    NotificationManager.post(NotificationType.WARNING, "Velocity", "Flagged! Disabled Velocity 1s");
            }
        }
        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            if (mode.is("Grim")) {
                if (flags != 0) return;
                if (mc.thePlayer.isDead) return;
                if (mc.currentScreen instanceof GuiGameOver) return;
                if (mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR) return;
                if (mc.thePlayer.isOnLadder()) return;
                if (mc.thePlayer.isBurning() && fireCheckValue.getValue()) return;
                if (mc.thePlayer.isInWater() && waterCheckValue.getValue()) return;
                if (mc.thePlayer.fallDistance > 1.5 && fallCheckValue.getValue()) return;
                if (flagCheckValue.getValue() && !flagtimer.hasTimeElapsed(1000)) return;
                if (mc.thePlayer.isEatingOrDrinking() && consumecheck.getValue()) return;
                if (soulSandCheck()) return;
            }
            if (((S12PacketEntityVelocity) event.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
                S12PacketEntityVelocity s12 = ((S12PacketEntityVelocity) event.getPacket());
                attacked = false;
                switch (mode.getValue()) {
                    case "Grim": {
                        if (ViaLoadingBase.getInstance().getTargetVersion().getVersion() >= 755) {
                            event.setCancelled(true);
                            grim_1_17Velocity = true;
                        } else {
                            double horizontalStrength = new Vector2d(s12.getMotionX(), s12.getMotionZ()).length();
                            if (horizontalStrength <= 1000) return;
                            if (debugMessageValue.getValue()) {
                                DebugUtil.log("Received Velocity: " + EnumChatFormatting.RED + horizontalStrength);
                            }
                            MovingObjectPosition mouse = mc.objectMouseOver;
                            velocityInput = true;
                            Entity entity = null;
                            reduceXZ = 1;

                            if (mouse.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mouse.entityHit instanceof EntityLivingBase && mc.thePlayer.getClosestDistanceToEntity(mouse.entityHit) <= 3) {
                                entity = mouse.entityHit;
                            }

                            if (entity == null && !raycastValue.getValue()) {
                                Entity target = KillAura.target;
                                if (target != null && KillAura.shouldAttack()) {
                                    entity = KillAura.target;
                                }
                            }

                            boolean state = mc.thePlayer.serverSprintState;

                            if (entity != null) {
                                if (!state) {
                                    PacketUtil.send(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                                }
                                Client.instance.eventManager.call(new EventAttack(entity, true));
                                Client.instance.eventManager.call(new EventAttack(entity, false));
                                int count = attackCountValue.get().intValue();
                                for (int i = 1; i <= count; i++) {
                                    AttackOrder.sendFixedAttackByPacket(mc.thePlayer, entity);
                                }
                                if (!state) {
                                    PacketUtil.send(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                                }
                                attacked = true;
                                reduceXZ = 0.07776;
                            }
                        }
                    }
                    break;
                    case "Watchdog":
                        if (mc.thePlayer.onGround) mc.thePlayer.jump();
                        timer.reset();
                        break;
                    case "Watchdog Ignore":
                        PacketUtil.send(new C03PacketPlayer(mc.thePlayer.onGround));
                        event.setCancelled(true);
                        break;
                }
            }
        }
        if (packet instanceof S27PacketExplosion && ViaLoadingBase.getInstance().getTargetVersion().getVersion() >= 755) {
            event.setCancelled(true);
            grim_1_17Velocity = true;
        }
    }


    public static boolean soulSandCheck() {
        final AxisAlignedBB par1AxisAlignedBB = Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().contract(0.001, 0.001,
                0.001);
        final int var4 = MathHelper.floor_double(par1AxisAlignedBB.minX);
        final int var5 = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0);
        final int var6 = MathHelper.floor_double(par1AxisAlignedBB.minY);
        final int var7 = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0);
        final int var8 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        final int var9 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0);
        for (int var11 = var4; var11 < var5; ++var11) {
            for (int var12 = var6; var12 < var7; ++var12) {
                for (int var13 = var8; var13 < var9; ++var13) {
                    final BlockPos pos = new BlockPos(var11, var12, var13);
                    final Block var14 = Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock();
                    if (var14 instanceof BlockSoulSand) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
