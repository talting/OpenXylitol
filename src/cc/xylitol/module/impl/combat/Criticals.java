package cc.xylitol.module.impl.combat;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventAttack;
import cc.xylitol.event.impl.events.EventStrafe;
import cc.xylitol.event.impl.events.EventTick;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import net.minecraft.util.BlockPos;

public class Criticals extends Module {

    private boolean working;

    public Criticals() {
        super("Criticals", Category.Combat);
    }

    @Override
    public void onEnable() {
        working = false;

    }

    public void legitJump() {
        if (mc.thePlayer.onGround) {
            mc.thePlayer.jump();
        }
    }

    @EventTarget
    public void onTick(EventTick e) {
        setSuffix("Legit");
    }

    @EventTarget
    public void onStrafe(EventStrafe e) {
        if (working) {
            legitJump();
            if (mc.thePlayer.hurtTime % 2 == 0) working = false;
        }
    }

    @EventTarget
    public void onAttack(EventAttack e) {
        boolean canCrit = e.getTarget() != null && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock().isFullBlock() && !mc.thePlayer.isOnLadder() && mc.thePlayer.ridingEntity == null;
        if (canCrit) {
            if (mc.thePlayer.hurtTime % 2 == 1) {
                working = true;
            }
        }
    }
}
