package cc.xylitol.module.impl.move;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventStrafe;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", Category.Movement);
    }

    @EventTarget
    public void onStrafe(EventStrafe e) {
        mc.gameSettings.keyBindSprint.pressed = true;
    }
}
