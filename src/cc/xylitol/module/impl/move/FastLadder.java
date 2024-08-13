package cc.xylitol.module.impl.move;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventBlockCollision;
import cc.xylitol.event.impl.events.EventMotion;
import cc.xylitol.event.impl.events.EventWorldLoad;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.module.impl.combat.KillAura;
import cc.xylitol.utils.BlockUtil;
import cc.xylitol.utils.PacketUtil;
import cc.xylitol.utils.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.Map;

public class FastLadder extends Module {

    public FastLadder() {
        super("FastLadder", Category.Movement);
    }
    private final TimerUtil timer = new TimerUtil();

    @Override
    public void onEnable() {
    }

    @EventTarget
    public void onWorld(EventWorldLoad e) {
    }

    @EventTarget
    public void onUpdate(final EventMotion event) {
        if (KillAura.target != null)
            return;


        if (mc.thePlayer.isOnLadder() && mc.gameSettings.keyBindJump.pressed) {
            if (mc.thePlayer.motionY >= 0.0) {
                mc.thePlayer.motionY = 0.1786;
            }
        }
        Map<BlockPos, Block> searchBlock = BlockUtil.searchBlocks(2);


        for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
            if (mc.thePlayer.isOnLadder() && !mc.gameSettings.keyBindJump.pressed) {
                if (mc.theWorld.getBlockState(block.getKey()).getBlock() instanceof BlockLadder) {
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, block.getKey(), EnumFacing.DOWN));
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block.getKey(), EnumFacing.DOWN));
                    mc.theWorld.setBlockToAir(block.getKey());
                }
            }
        }

    }


    @EventTarget
    public void onBlock(final EventBlockCollision event) {
    }
}
