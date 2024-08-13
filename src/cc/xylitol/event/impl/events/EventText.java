package cc.xylitol.event.impl.events;

import cc.xylitol.event.impl.Event;

public class EventText implements Event {

   public String string;

    public EventText(String string) {
        this.string = string;
    }

    public String getText() {
        return string;
    }

    public void setText(String pass) {
        this.string = pass;
    }
}
