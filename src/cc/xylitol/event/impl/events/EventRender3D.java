package cc.xylitol.event.impl.events;

import cc.xylitol.event.impl.Event;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;

@Getter
public class EventRender3D implements Event {
    private float ticks;
    private float partialTicks;
    private ScaledResolution scaledResolution;
    public EventRender3D(float ticks) {
        this.ticks = ticks;
    }
    public void Render3DEvent(ScaledResolution scaledResolution, float partialTicks) {
        this.scaledResolution = scaledResolution;
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

}

