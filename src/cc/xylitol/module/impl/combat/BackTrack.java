package cc.xylitol.module.impl.combat;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventAttack;
import cc.xylitol.event.impl.events.EventPacket;
import cc.xylitol.event.impl.events.EventTick;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.value.impl.ModeValue;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.Vec3;

public class BackTrack extends Module {
    private final ModeValue mode = new ModeValue("Mode",  new String[]{"Tick"},"Tick");
    private final NumberValue amount = new NumberValue("Amount", 1.0, 1.0, 3.0, 0.1);
    private final NumberValue range = new NumberValue("Range", 3.0, 1.0, 5.0, 0.1);
    private final NumberValue interval = new NumberValue("interval tick", 1, 0, 10, 1);
    private EntityLivingBase target;
    private Vec3 realTargetPosition = new Vec3(0.0D, 0.0D, 0.0D);
    int tick = 0;

    public BackTrack() {
        super("BackTrack", Category.Combat);
    }

    public void onDisable() {
        target = null;
        tick = 0;
    }

    @EventTarget
    public void onAttack(EventAttack e) {
        if(e.getTarget() != null)
            target = (EntityLivingBase) e.getTarget();
    }

    @EventTarget
    public void onTick(EventTick e) {
        setSuffix(this.mode.getValue());
        if (this.tick <= this.interval.getValue())
            this.tick++;
        if (target != null
                && mc.thePlayer.getDistanceToEntity(target) <= this.range.getValue()
                && (new Vec3(target.posX, target.posY, target.posZ)).distanceTo(this.realTargetPosition) < this.amount.getValue()
                && this.tick > this.interval.getValue()) {
            target.posX = target.prevPosX;
            target.posY = target.prevPosY;
            target.posZ = target.prevPosZ;
            tick = 0;
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof S18PacketEntityTeleport) {
            S18PacketEntityTeleport s18 = (S18PacketEntityTeleport) e.getPacket();
            if (target != null && s18.getEntityId() == target.getEntityId())
                realTargetPosition = new Vec3(s18.getX() / 32.0D, s18.getY() / 32.0D, s18.getZ() / 32.0D);
        }
    }
}
