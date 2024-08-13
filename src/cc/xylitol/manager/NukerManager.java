package cc.xylitol.manager;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventRenderBlock;
import cc.xylitol.event.impl.events.EventUpdate;
import net.minecraft.block.BlockBed;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;

public class NukerManager {
    public ArrayList<BlockPos> list = new ArrayList<BlockPos>();

    public void update() {
        Minecraft.getMinecraft().renderGlobal.loadRenderers();
        list.clear();
    }

    @EventTarget
    public void onRenderBlock(EventRenderBlock e) {
        BlockPos pos = new BlockPos(e.x, e.y, e.z);
        if (!list.contains(pos) && e.block instanceof BlockBed) {
            list.add(pos);
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        list.removeIf(pos -> !(Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock() instanceof BlockBed));
    }
}
