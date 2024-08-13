package cc.xylitol.module.impl.combat;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventMotion;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class AntiFireBall extends Module {

    public AntiFireBall() {
        super("AntiFireBall", Category.Combat);
    }

    public static final TimerUtil timer = new TimerUtil();



    @EventTarget
    public void onUpdate(final EventMotion event) {
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityFireball) {
                if (mc.thePlayer.getDistanceToEntity(entity) < 6.0 && timer.hasTimeElapsed(0L)) {
                    mc.getNetHandler().getNetworkManager().sendPacket((new C02PacketUseEntity(entity,C02PacketUseEntity.Action.ATTACK)));
                    mc.thePlayer.swingItem();
                }
            }
        }
    }
}
