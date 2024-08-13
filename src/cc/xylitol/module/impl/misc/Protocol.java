package cc.xylitol.module.impl.misc;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventTick;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.value.impl.ModeValue;

public class Protocol extends Module {
    public static ModeValue mode = new ModeValue("Mode", new String[]{"Quick Macro"}, "Quick Macro");
    public Protocol() {
        super("Protocol", Category.Misc);
    }
}
