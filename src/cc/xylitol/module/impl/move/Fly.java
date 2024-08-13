package cc.xylitol.module.impl.move;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventUpdate;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.module.impl.world.Test;
import cc.xylitol.utils.player.MoveUtil;
import cc.xylitol.value.impl.NumberValue;

public class Fly extends Module {
    private NumberValue vanillaVSpeed = new NumberValue("V-Speed", 2.0, 0.1, 10.0, 0.1);
    private NumberValue vanillaSpeed = new NumberValue("H-Speed", 2.0, 0.1, 10.0, 0.1);

    public Fly() {
        super("Fly", Category.Movement);
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer==null)
            return;
        mc.thePlayer.handleStatusUpdate((byte) 2);

    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        mc.thePlayer.capabilities.isFlying = false;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
        if (mc.gameSettings.keyBindJump.isKeyDown())
            mc.thePlayer.motionY += vanillaVSpeed.get();
        if (mc.gameSettings.keyBindSneak.isKeyDown())
            mc.thePlayer.motionY -= vanillaVSpeed.get();
        MoveUtil.strafe(vanillaSpeed.get());
    }
}
