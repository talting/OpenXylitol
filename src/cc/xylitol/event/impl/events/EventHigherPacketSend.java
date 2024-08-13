package cc.xylitol.event.impl.events;

import cc.xylitol.event.impl.CancellableEvent;
import net.minecraft.network.Packet;;

public class EventHigherPacketSend
        extends CancellableEvent {
    public Packet packet;

    public EventHigherPacketSend(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}

