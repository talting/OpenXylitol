package cc.xylitol.module.impl.move;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventMotion;
import cc.xylitol.event.impl.events.EventWorldLoad;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.BlockUtil;
import cc.xylitol.value.impl.ModeValue;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import java.util.Map;

public class NoLiquid extends Module {
    private final ModeValue modeValue = new ModeValue("Mode", new String[]{"Vanilla", "Grim"}, "Grim");
    public static boolean shouldCancelWater;


    public NoLiquid() {
        super("NoLiquid", Category.Movement);
    }

    @Override
    public void onDisable() {
        shouldCancelWater = false;
    }

    @EventTarget
    public void onWorldLoad(EventWorldLoad e) {
        shouldCancelWater = false;
    }

    @EventTarget
    public void onUpdate(EventMotion e) {
        this.setSuffix(modeValue.getValue());

        if (mc.thePlayer == null)
            return;

        if (e.isPost()) return;

        shouldCancelWater = false;

        Map<BlockPos, Block> searchBlock = BlockUtil.searchBlocks(2);

        for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
            boolean checkBlock = mc.theWorld.getBlockState(block.getKey()).getBlock() == Blocks.water
                    || mc.theWorld.getBlockState(block.getKey()).getBlock() == Blocks.flowing_water
                    || mc.theWorld.getBlockState(block.getKey()).getBlock() == Blocks.lava
                    || mc.theWorld.getBlockState(block.getKey()).getBlock() == Blocks.flowing_lava;

        }
    }
}

