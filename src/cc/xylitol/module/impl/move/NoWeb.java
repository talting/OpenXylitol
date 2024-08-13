package cc.xylitol.module.impl.move;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventMotion;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.BlockUtil;
import cc.xylitol.utils.PacketUtil;
import cc.xylitol.value.impl.ModeValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import top.fl0wowp4rty.phantomshield.annotations.Native;

import java.util.Map;

@Native
public class NoWeb extends Module {
    private final ModeValue modeValue = new ModeValue("Mode", new String[]{"Vanilla", "Grim", "AAC", "LowAAC", "Rewind"}, "Grim");

    public NoWeb() {
        super("NoWeb", Category.Movement);
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
    }

    @EventTarget
    private void onUpdate(EventMotion e) {
        if (e.isPost()) return;

        this.setSuffix(modeValue.getValue());

        if (!mc.thePlayer.isInWeb) {
            return;
        }

        switch (modeValue.getValue()) {
            case "Vanilla":
                mc.thePlayer.isInWeb = false;
                break;
            case "Grim":
                Map<BlockPos, Block> searchBlock = BlockUtil.searchBlocks(2);
                for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
                    if (mc.theWorld.getBlockState(block.getKey()).getBlock() instanceof BlockWeb) {
                        PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, block.getKey(), EnumFacing.DOWN));
                        PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block.getKey(), EnumFacing.DOWN));
                    }
                }
                mc.thePlayer.isInWeb = false;
                break;
            case "AAC":
                mc.thePlayer.jumpMovementFactor = 0.59f;

                if (!mc.gameSettings.keyBindSneak.isKeyDown())
                    mc.thePlayer.motionY = 0.0;
                break;
            case "LowAAC":
                mc.thePlayer.jumpMovementFactor = mc.thePlayer.movementInput.moveStrafe != 0f ? 1.0f : 1.21f;

                if (!mc.gameSettings.keyBindSneak.isKeyDown())
                    mc.thePlayer.motionY = 0.0;

                if (mc.thePlayer.onGround)
                    mc.thePlayer.jump();
                break;
            case "Rewind":
                mc.thePlayer.jumpMovementFactor = 0.42f;

                if (mc.thePlayer.onGround)
                    mc.thePlayer.jump();
                break;
        }
    }
}