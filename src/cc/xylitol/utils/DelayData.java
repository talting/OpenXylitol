package cc.xylitol.utils;

import net.minecraft.network.Packet;

public class DelayData {
    private Packet<?> packet;
    private long delay;

    public DelayData(Packet<?> packet, long delay) {
        this.packet = packet;
        this.delay = delay;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DelayData delayData = (DelayData) o;
        if (delay != delayData.delay) return false;
        return packet != null ? packet.equals(delayData.packet) : delayData.packet == null;
    }

    @Override
    public int hashCode() {
        int result = packet != null ? packet.hashCode() : 0;
        result = 31 * result + (int) (delay ^ (delay >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "DelayData{" +
                "packet=" + packet +
                ", delay=" + delay +
                '}';
    }
}
