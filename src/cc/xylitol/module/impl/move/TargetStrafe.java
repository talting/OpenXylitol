
package cc.xylitol.module.impl.move;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventJump;
import cc.xylitol.event.impl.events.EventMoveInput;
import cc.xylitol.event.impl.events.EventStrafe;
import cc.xylitol.event.impl.events.EventUpdate;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.module.impl.combat.KillAura;
import cc.xylitol.module.impl.misc.AntiBot;
import cc.xylitol.module.impl.misc.Teams;
import cc.xylitol.utils.player.MoveUtil;
import cc.xylitol.utils.player.PlayerUtil;
import cc.xylitol.utils.player.RotationUtil;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public class TargetStrafe
        extends Module {
    public TargetStrafe() {
        super("TargetStrafe", Category.Movement);
    }

    private final NumberValue range = new NumberValue("Range", 1.0, 0.5, 1.5, 0.25);
    private final BoolValue autoJump = new BoolValue("Auto Jump", false);
    private float yaw;
    private EntityLivingBase target;
    private boolean left;
    private boolean colliding;

    @EventTarget
    public void onMove(EventMoveInput event) {
        if (this.target != null && this.distanceToTarget() <= 2.0) {
            this.setRotation();
            event.setForward(1.0f);
            event.setStrafe(0.0f);
            event.setSneak(false);
        }
    }

    ;

    @EventTarget
    public void onJUmp(EventJump event) {
        if (this.target != null && this.distanceToTarget() <= 2.0) {
            this.setRotation();
            event.setYaw(this.yaw);

        }
    }

    ;

    @EventTarget
    public void onStrafe(EventStrafe event) {
        if (this.target != null && this.distanceToTarget() <= 2.0) {
            this.setRotation();
            event.setYaw(this.yaw);
            if (mc.thePlayer.hurtTime != 0) {
                return;
            }
            if (this.autoJump.getValue() && mc.thePlayer.onGround) {
                mc.thePlayer.jump();
            }
            float friction = 0.2f;
            event.setFriction(friction);
            MoveUtil.strafe(0.05f, this.yaw);
        }
    }

    ;

    @EventTarget
    public void onYpdate(EventUpdate event) {
        this.updateTarget();
        if (this.target == null) {
            return;
        }
        if (mc.gameSettings.keyBindLeft.isKeyDown()) {
            this.left = true;
        }
        if (mc.gameSettings.keyBindRight.isKeyDown()) {
            this.left = false;
        }
        if (mc.thePlayer.isCollidedHorizontally || !PlayerUtil.isBlockUnder(5.0)) {
            if (!this.colliding) {
                this.left = !this.left;
            }
            this.colliding = true;
        } else {
            this.colliding = false;
        }
        if (target != null&& this.distanceToTarget() <= 2.0) {

            mc.thePlayer.setSprinting(false);
            mc.gameSettings.keyBindSprint.pressed = false;
        }
    }

    ;
    private final Predicate<Entity> ENTITY_FILTER = entity -> entity.isEntityAlive() && mc.thePlayer.getDistanceToEntity(entity) <= 6.0f && entity instanceof EntityLivingBase && entity != mc.thePlayer && !(entity instanceof EntityArmorStand) && (!AntiBot.isServerBot((EntityLivingBase) entity)) && (!Teams.isSameTeam(entity)) && (entity.getEntityId() != -1337 && entity.getEntityId() != -8);

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    private void setRotation() {
        if (this.target == null) {
            return;
        }
        float yaw = RotationUtil.smoothReal(RotationUtil.calculate(this.target)).x + 135.0f * (float) (this.left ? -1 : 1);
        double range = (this.range.getValue()).doubleValue();
        double posX = (double) (-MathHelper.sin((float) Math.toRadians(yaw))) * range + this.target.posX;
        double posZ = (double) MathHelper.cos((float) Math.toRadians(yaw)) * range + this.target.posZ;
        this.yaw = RotationUtil.smoothReal(RotationUtil.calculate(new Vec3(posX, this.target.posY + (double) this.target.getEyeHeight(), posZ))).x;
    }

    private double distanceToTarget() {
        return mc.thePlayer.getDistanceToEntity(this.target);
    }

    private void updateTarget() {
        KillAura aura = getModule(KillAura.class);
        if (aura.getState() && aura.target != null || this.target == null || !this.target.isEntityAlive() || this.distanceToTarget() > 6.0) {
            this.target = aura.getState() && aura.target != null ? (EntityLivingBase) aura.target : this.getTarget();
        }
    }

    private EntityLivingBase getTarget() {
        return (EntityLivingBase) StreamSupport.stream(mc.theWorld.loadedEntityList.spliterator(), true).filter(this.ENTITY_FILTER).findFirst().orElse(null);
    }
}

