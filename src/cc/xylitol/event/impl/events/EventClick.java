package cc.xylitol.event.impl.events;

import cc.xylitol.event.impl.CancellableEvent;
import lombok.Getter;

@Getter
public class EventClick extends CancellableEvent {
    private int key;

    public EventClick(int key) {
        this.key = key;
    }

}
