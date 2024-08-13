package cc.xylitol.event.impl.events;


import cc.xylitol.event.impl.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventMotion implements Event {
    public EventState eventState;
    private double x, y, z;
    private float yaw, pitch;
    private boolean ground;

    public EventMotion(double x, double y, double z, float yaw, float pitch, boolean ground, EventState eventState) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.ground = ground;
        this.eventState = eventState;
    }

    public boolean isPre() {
        return this.eventState == EventState.PRE;
    }

    public boolean isPost() {
        return this.eventState == EventState.POST;
    }

    public enum EventState {
        PRE,
        POST
    }

}
