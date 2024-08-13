package cc.xylitol.manager;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventMotion;

import static cc.xylitol.Client.mc;

public final class FallDistanceManager {

    public float distance;
    private float lastDistance;

    @EventTarget
    private void onMotion(EventMotion event) {
        if (event.isPre()) {
            final float fallDistance = mc.thePlayer.fallDistance;

            if (fallDistance == 0) {
                distance = 0;
            }

            distance += fallDistance - lastDistance;
            lastDistance = fallDistance;
        }
    }
}
