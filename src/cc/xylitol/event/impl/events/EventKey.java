package cc.xylitol.event.impl.events;

import cc.xylitol.event.impl.CancellableEvent;
import lombok.Getter;

@Getter
public class EventKey extends CancellableEvent {
    private int key;

    public EventKey(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
