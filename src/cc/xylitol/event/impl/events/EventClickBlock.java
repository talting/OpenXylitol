package cc.xylitol.event.impl.events;

import cc.xylitol.event.impl.Event;
import lombok.Getter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
@Getter
public final class EventClickBlock implements Event {
    private final BlockPos clickedBlock;
    private final EnumFacing enumFacing;

    public EventClickBlock(final BlockPos clickedBlock, final EnumFacing enumFacing) {
        this.clickedBlock = clickedBlock;
        this.enumFacing = enumFacing;
    }

}
