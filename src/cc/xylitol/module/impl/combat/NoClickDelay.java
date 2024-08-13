package cc.xylitol.module.impl.combat;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventMotion;
import cc.xylitol.event.impl.events.EventStrafe;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;


/**
 * @author Alan Jr. (Not Billionaire)
 * @since 19/9/2022
 */

public class NoClickDelay extends Module {
    public NoClickDelay() {
        super("NoClickDelay", Category.Combat);
    }
    @EventTarget
    public void onMotion(EventMotion e) {
      if (mc.thePlayer != null && mc.theWorld != null){
          mc.leftClickCounter = 0;
      }
    }
}