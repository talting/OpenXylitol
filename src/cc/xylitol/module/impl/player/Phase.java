package cc.xylitol.module.impl.player;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventJump;
import cc.xylitol.event.impl.events.EventPacket;
import cc.xylitol.event.impl.events.EventTick;
import cc.xylitol.event.impl.events.EventWorldLoad;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.DebugUtil;
import net.minecraft.block.BlockPane;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldSettings;

public final class Phase extends Module {

    private boolean enable = true;

    public Phase() {
        super("AutoClip", Category.Player);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        enable = true;
    }

    /* events */
    @EventTarget
    public void onPre(EventWorldLoad event) {
        enable = true;

    }

    @EventTarget
    public void onTick(EventTick event) {
        boolean canClip = mc.thePlayer.capabilities.allowFlying && mc.playerController.getCurrentGameType() == WorldSettings.GameType.ADVENTURE && enable;
        if (canClip) {
            enable = false;
//            this.setState(false);
        }
        if (!canClip && !enable) {
//            if (mc.thePlayer.onGround) getModule(BalanceTimer.class).setState(false);
        }
    }

    @EventTarget
    public void onJump(final EventJump event) {
//        event.setMotion(event.getMotion() * 4);

    }
    @EventTarget
    public void onPacket(EventPacket e) {

        if (e.getPacket() instanceof S45PacketTitle) {
            S45PacketTitle packet = (S45PacketTitle) e.getPacket();
            if (packet.getType() == S45PacketTitle.Type.SUBTITLE) {
                String s = packet.getMessage() != null ? packet.getMessage().getFormattedText() : "";
                DebugUtil.log(s);
                if (s.contains("Fighting")) {

//                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 12, mc.thePlayer.posZ);

                    mc.thePlayer.motionY = 4f;
                    mc.thePlayer.jump();
                    this.setState(false);
                }
            }
        }
    }

}