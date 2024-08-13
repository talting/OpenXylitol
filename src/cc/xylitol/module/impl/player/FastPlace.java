package cc.xylitol.module.impl.player;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventTick;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.item.ItemBlock;

public class FastPlace extends Module {
    private final NumberValue ticks = new NumberValue("Ticks", 0, 0, 4, 1);

    public FastPlace() {
        super("FastPlace", Category.Player);
    }

    @EventTarget
    public void onTick(EventTick e) {
        if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
            // 取消防止方块延迟
            mc.rightClickDelayTimer = Math.min(0, ticks.getValue().intValue());
        }
    }
}
