package cc.xylitol.module.impl.player;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventClickBlock;
import cc.xylitol.event.impl.events.EventMotion;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.PacketUtil;
import cc.xylitol.value.impl.BoolValue;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class AutoTool extends Module {

    public AutoTool() {
        super("AutoTool", Category.Player);
    }



    private int oldSlot;
    private int tick;

    @EventTarget
    public void onClick(EventMotion event) {
        if(event.isPre()){
            if (mc.playerController.isBreakingBlock()) {
                tick++;

                if (tick == 1) {
                    oldSlot = mc.thePlayer.inventory.currentItem;
                }

                mc.thePlayer.updateTool(mc.objectMouseOver.getBlockPos());
            } else if (tick > 0) {
                    mc.thePlayer.inventory.currentItem = oldSlot;

                tick = 0;
            }
        }
    }
}