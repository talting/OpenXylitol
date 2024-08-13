package cc.xylitol.module.impl.combat;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventAttack;
import cc.xylitol.event.impl.events.EventMotion;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;

public class SuperKnockBack extends Module {
    public static boolean sprint = true, wTap;

    public SuperKnockBack() {
        super("SuperKnockBack", Category.Combat);
    }

    @Override
    public void onDisable() {
        sprint = true;
    }



    @Override
    public void onEnable() {
        sprint = true;
    }

    @EventTarget
    public void onAttack(EventAttack event) {

        wTap = Math.random() * 100 < 100;

        //if (!wTap) return;

    }

    @EventTarget
    public void onPre(EventMotion event) {
        if (event.isPre()) {
           // if (!wTap) return;

            if (mc.thePlayer.moveForward > 0 && mc.thePlayer.serverSprintState == mc.thePlayer.isSprinting()) {
                sprint = !mc.thePlayer.serverSprintState;
            }
        }

    }

}
