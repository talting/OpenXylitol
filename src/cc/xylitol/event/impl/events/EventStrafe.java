package cc.xylitol.event.impl.events;

import cc.xylitol.event.impl.CancellableEvent;
import cc.xylitol.utils.player.MoveUtil;
import lombok.Getter;
import lombok.Setter;

import static cc.xylitol.Client.mc;

@Getter
@Setter
public class EventStrafe extends CancellableEvent {
    public float strafe;
    public float forward;
    public float friction;
    public float yaw;

    public EventStrafe(float Strafe, float Forward, float Friction, float Yaw) {
        this.strafe = Strafe;
        this.forward = Forward;
        this.friction = Friction;
        this.yaw = Yaw;
    }

    public void setSpeed(final double speed) {
        setFriction((float) (getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed));
        MoveUtil.stop();
    }
}
