package cc.xylitol.event.impl.events;


import cc.xylitol.event.impl.CancellableEvent;
import cc.xylitol.event.impl.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventSlowDown extends CancellableEvent {

    private float strafeMultiplier;
    private float forwardMultiplier;

    public EventSlowDown(float strafeMultiplier, float forwardMultiplier) {
        this.strafeMultiplier = strafeMultiplier;
        this.forwardMultiplier = forwardMultiplier;
    }
}
