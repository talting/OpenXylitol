package cc.xylitol.event.impl.events;

import cc.xylitol.event.impl.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class EventAttack implements Event {
    private final boolean pre;
    private Entity target;

    public EventAttack(Entity entity, boolean pre) {
        this.target = entity;
        this.pre = pre;
    }

    public Entity getTarget() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    public boolean isPre() {
        return pre;
    }

    public boolean isPost() {
        return !pre;
    }
}
