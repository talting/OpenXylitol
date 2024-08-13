package cc.xylitol.module.impl.combat;


import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventMotion;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.DebugUtil;
import cc.xylitol.utils.player.RaytraceUtil;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.ModeValue;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

import java.io.IOException;
import java.util.List;

import static cc.xylitol.utils.player.RotationUtil.getNearestPointBB;

public class TickBase extends Module {
    public static TickBase INSTANCE;
    private final ModeValue mode = new ModeValue("Mode", new String[]{"RayCast", "Radius"}, "RayCast");
    private final NumberValue minDistance = new NumberValue("MinDistance", 3.0, 0.0, 4.0, 0.1);
    private final NumberValue maxDistance = new NumberValue("MaxDistance", 4.0, 3.0, 7.0, 0.1);
    private final ModeValue rangeMode = new ModeValue("RangeMode", new String[]{"Setting", "Smart"}, "Smart");
    private final NumberValue maxTimeValue = new NumberValue("MaxTime", 3.0, 0.0, 20.0, 1.0);
    private final NumberValue delayValue = new NumberValue("Delay", 5.0, 0.0, 20.0, 1.0);
    private final NumberValue maxHurtTimeValue = new NumberValue("TargetMaxHurtTime", 2.0, 0.0, 10.0, 1.0);
    private final BoolValue onlyKillAura = new BoolValue("OnlyKillAura", true);
    private final BoolValue auraClick = new BoolValue("AuraClick", true);
    private final BoolValue onlyPlayer = new BoolValue("OnlyPlayer", true);
    private final BoolValue debug = new BoolValue("Debug", false);
    private final BoolValue betterAnimation = new BoolValue("BetterAnimation", true);
    private final BoolValue reverseValue = new BoolValue("Reverse", false);
    private final NumberValue maxReverseRange = new NumberValue("MaxReverseRange", 2.8, 1.0, 4.0, 0.1);
    private final NumberValue minReverseRange = new NumberValue("MinReverseRange", 2.5, 1.0, 4.0, 0.1);
    private final NumberValue reverseTime = new NumberValue("ReverseStopTime", 3.0, 1.0, 10, 1.0);
    private final NumberValue reverseTickTime = new NumberValue("ReverseTickTime", 3.0, 0.0, 10.0, 1.0);
    private final NumberValue reverseDelay = new NumberValue("ReverseDelay", 5.0, 0.0, 20.0, 1.0);
    private final NumberValue reverseTargetMaxHurtTime = new NumberValue("ReverseTargetMaxHurtTime", 3.0, 0.0, 10.0, 1.0);
    private KillAura killAura;
    private static boolean working = false;
    private static boolean stopWorking = false;
    private static double lastNearest = 10.0;
    private static int cooldown = 0;
    private static int freezeTicks = 0;
    private static boolean reverseFreeze = true;
    private static boolean firstAnimation = true;

    public TickBase() {
        super("TickBase", Category.Combat);
        INSTANCE = this;
    }


    @Override
    public void onEnable() {
        killAura = getModule(KillAura.class);
    }

    @EventTarget
    public void onMotion(EventMotion event) {
        if (event.getEventState() == EventMotion.EventState.PRE) return;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer thePlayer = mc.thePlayer;
        if (onlyKillAura.get() && !killAura.getState()) return;
        if (mode.get().equals("RayCast")) {
            Entity entity = RaytraceUtil.raycastEntity(maxDistance.get() + 1.0, new RaytraceUtil.IEntityFilter() {
                @Override
                public boolean canRaycast(Entity entity) {
                    return entity != null && entity instanceof EntityLivingBase && (!onlyPlayer.get() || entity instanceof EntityPlayer);
                }
            });
            if (entity == null || !(entity instanceof EntityLivingBase)) {
                lastNearest = 10.0;
                return;
            }
            Vec3 vecEyes = thePlayer.getPositionEyes(1f);
            Vec3 predictEyes = rangeMode.get().equals("Smart") ? thePlayer.getPositionEyes((float) (maxTimeValue.get() + 1f)) : thePlayer.getPositionEyes(3f);
            AxisAlignedBB entityBox = entity.getEntityBoundingBox().expands(entity.getCollisionBorderSize(), true, true);
            Vec3 box = getNearestPointBB(vecEyes, entityBox);
            Vec3 box2 = getNearestPointBB(predictEyes, entity instanceof EntityOtherPlayerMP ? entityBox.offset(((EntityOtherPlayerMP) entity).getOtherPlayerMPX() - entity.posX, ((EntityOtherPlayerMP) entity).getOtherPlayerMPY() - entity.posY, ((EntityOtherPlayerMP) entity).getOtherPlayerMPZ() - entity.posZ) : entityBox);
            double range = box.distanceTo(vecEyes);
            if (!killAura.isValid(entity, range)) return;
            double afterRange = box2.distanceTo(predictEyes);
            if (!working && reverseValue.get()) {
                if (range <= maxReverseRange.get() && range >= minReverseRange.get() && cooldown <= 0 && ((EntityLivingBase) entity).hurtTime <= reverseTargetMaxHurtTime.get()) {
                    freezeTicks = reverseTime.get().intValue();
                    firstAnimation = false;
                    reverseFreeze = true;
                    return;
                }
            }
            if (range < minDistance.get()) {
                stopWorking = true;
            } else if (((rangeMode.get().equals("Smart") && range > minDistance.get() && afterRange < minDistance.get() && afterRange < range) || (rangeMode.get().equals("Setting") && range <= maxDistance.get() && range < lastNearest && afterRange < range)) && ((EntityLivingBase) entity).hurtTime <= maxHurtTimeValue.get()) {
                stopWorking = false;
                foundTarget();
            }
            lastNearest = range;
        } else {
            List<Entity> entityList = mc.theWorld.getEntitiesWithinAABBExcludingEntity(thePlayer, thePlayer.getEntityBoundingBox().expands(maxDistance.get() + 1.0, true, true));
            if (!entityList.isEmpty()) {
                Vec3 vecEyes = thePlayer.getPositionEyes(1f);
                Vec3 afterEyes = rangeMode.get().equals("Smart") ? thePlayer.getPositionEyes((float) (maxTimeValue.get() + 1f)) : thePlayer.getPositionEyes(3f);
                boolean targetFound = false;
                boolean targetInRange = false;
                double nearest = 10.0;
                for (Entity entity : entityList) {
                    if (!(entity instanceof EntityLivingBase)) continue;
                    if (onlyPlayer.get() && !(entity instanceof EntityPlayer)) continue;
                    AxisAlignedBB entityBox = entity.getEntityBoundingBox().expands(entity.getCollisionBorderSize(), true, true);
                    Vec3 box = getNearestPointBB(vecEyes, entityBox);
                    Vec3 box2 = getNearestPointBB(afterEyes, entity instanceof EntityOtherPlayerMP ? entityBox.offset(((EntityOtherPlayerMP) entity).getOtherPlayerMPX() - entity.posX, ((EntityOtherPlayerMP) entity).getOtherPlayerMPY() - entity.posY, ((EntityOtherPlayerMP) entity).getOtherPlayerMPZ() - entity.posZ) : entityBox);
                    double range = box.distanceTo(vecEyes);
                    if (!killAura.isValid(entity, range)) continue;

                    double afterRange = box2.distanceTo(afterEyes);
                    if (!working && reverseValue.get()) {
                        if (range <= maxReverseRange.get() && range >= minReverseRange.get() && cooldown <= 0 && ((EntityLivingBase) entity).hurtTime <= reverseTargetMaxHurtTime.get()) {
                            freezeTicks = reverseTime.get().intValue();
                            firstAnimation = false;
                            reverseFreeze = true;
                            return;
                        }
                    }
                    if (range < minDistance.get()) {
                        targetInRange = true;
                        break;
                    } else if (range <= maxDistance.get() && afterRange < range && ((EntityLivingBase) entity).hurtTime <= maxHurtTimeValue.get()) {
                        targetFound = true;
                    }
                    nearest = Math.min(nearest, range);
                }
                if (targetInRange) {
                    stopWorking = true;
                } else if (targetFound && nearest < lastNearest) {
                    stopWorking = false;
                    foundTarget();
                }
                lastNearest = nearest;
            } else {
                lastNearest = 10.0;
            }
        }
    }

    public void foundTarget() {
        if (cooldown > 0 || freezeTicks != 0 || maxTimeValue.get() == 0) return;
        cooldown = delayValue.get().intValue();
        working = true;
        freezeTicks = 0;
        if (betterAnimation.get()) firstAnimation = false;
        while (freezeTicks <= maxTimeValue.get() - (auraClick.get() ? 1 : 0) && !stopWorking) {
            ++freezeTicks;
            try {
                mc.runTick();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (debug.get()) DebugUtil.log("Timer-ed");
        if (auraClick.get()) {
            //killAura.setClicks(killAura.getClicks() + 1);
            ++freezeTicks;
            try {
                mc.runTick();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (debug.get()) DebugUtil.log("Clicked");
        }
        stopWorking = false;
        working = false;
    }

    public boolean handleTick() {
        if (working || freezeTicks < 0) return true;
        if (getState() && freezeTicks > 0) {
            --freezeTicks;
            return true;
        }
        if (reverseFreeze) {
            reverseFreeze = false;
            int time = reverseTickTime.get().intValue();
            working = true;
//            if (reverseAuraClick.get().equals("BeforeTimer")) killAura.setClicks(killAura.getClicks() + 1);
            while (time > 0) {
                --time;
                try {
                    mc.runTick();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            working = false;
            cooldown = reverseDelay.get().intValue();
//            if (reverseAuraClick.get().equals("AfterTimer")) killAura.setClicks(killAura.getClicks() + 1);
        }
        if (cooldown > 0) --cooldown;
        return false;
    }

    public boolean freezeAnimation() {
        if (freezeTicks != 0) {
            if (!firstAnimation) {
                firstAnimation = true;
                return false;
            }
            return true;
        }
        return false;
    }

}


