package cc.xylitol.event.impl.events;


import cc.xylitol.event.impl.Event;

public class EventRenderPlayer
        implements Event {

    public REventState eventState;

    public EventRenderPlayer(REventState eventState) {
        this.eventState = eventState;
    }

    public enum REventState {
        PRE,
        POST
    }
}

