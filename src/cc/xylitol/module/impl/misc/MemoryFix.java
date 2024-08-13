package cc.xylitol.module.impl.misc;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventTick;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.TimerUtil;
import cc.xylitol.value.impl.NumberValue;

public class MemoryFix extends Module {

    private final NumberValue delay = new NumberValue("Delay", 120.0F, 10.0F, 600.0F, 10.0F);
    private final NumberValue limit = new NumberValue("Limit", 80.0F, 20.0F, 95.0F, 1.0F);
    public TimerUtil timer = new TimerUtil();

    public MemoryFix() {
        super("MemoryFix", Category.Misc);
    }

    @EventTarget
    public void onTick(EventTick event) {
        long maxMem = Runtime.getRuntime().maxMemory();
        long totalMem = Runtime.getRuntime().totalMemory();
        long freeMem = Runtime.getRuntime().freeMemory();
        long usedMem = totalMem - freeMem;
        float pct = (float) (usedMem * 100L / maxMem);
        if (timer.hasReached(delay.getValue() * 1000.0D) && limit.getValue() <= (double) pct) {
            Runtime.getRuntime().gc();
            timer.reset();
        }
    }
}