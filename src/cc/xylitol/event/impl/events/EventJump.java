/*
 * Decompiled with CFR 0_132.
 */
package cc.xylitol.event.impl.events;


import cc.xylitol.event.impl.CancellableEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventJump extends CancellableEvent {
    public float motion;
    public float yaw;

    public EventJump(float yaw, float motion) {
        this.yaw = yaw;
        this.motion = motion;
    }

}

