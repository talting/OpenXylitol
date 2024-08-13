package cc.xylitol.module.impl.render;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventRender3D;
import cc.xylitol.event.impl.events.EventUpdate;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.BlockUtil;
import cc.xylitol.utils.TimerUtil;
import cc.xylitol.utils.render.ColorUtil;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.ColorValue;
import cc.xylitol.value.impl.ModeValue;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;



public class BlockESP extends Module {
    public BlockESP() {
        super("BlockESP", Category.Render);
    }
    public ModeValue mode = new ModeValue("renderMode", new String[]{"Box", "TwoD", "Outline"}, "Single");

    private final NumberValue blockValue = new NumberValue("BlockID", 26, 1, 168, 1);
    private final NumberValue radiusValue = new NumberValue("Radius", 40, 5, 120, 1);
    public ColorValue renderColor = new ColorValue("RenderColor", Color.WHITE.getRGB());
    private final BoolValue colorRainbow = new BoolValue("Rainbow", false);
    private final TimerUtil searchTimer = new TimerUtil();
    private final List<BlockPos> posList = new ArrayList<>();
    private Thread thread;



    @EventTarget
    public void onUpdate(EventUpdate event){
        if (searchTimer.delay(1000L) && (thread == null || !thread.isAlive())) {
            final int radius = radiusValue.getValue().intValue();
            final Block selectedBlock = Block.getBlockById(blockValue.getValue().intValue());

            if (selectedBlock == Blocks.air)
                return;

            thread = new Thread(() -> {
                final List<BlockPos> blockList = new ArrayList<>();

                for (int x = -radius; x < radius; x++) {
                    for (int y = radius; y > -radius; y--) {
                        for (int z = -radius; z < radius; z++) {
                            final int xPos = ((int) mc.thePlayer.posX + x);
                            final int yPos = ((int) mc.thePlayer.posY + y);
                            final int zPos = ((int) mc.thePlayer.posZ + z);

                            final BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
                            final Block block = BlockUtil.getBlock(blockPos);
                            if (block == selectedBlock)
                                blockList.add(blockPos);
                        }
                    }
                }

                searchTimer.reset();

                synchronized (posList) {
                    posList.clear();
                    posList.addAll(blockList);
                }
            }, "BlockESP-BlockFinder");
            thread.start();
        }
    }

    @EventTarget
    public void onRender3D(EventRender3D event){
        synchronized (posList) {
            final Color color = colorRainbow.getValue() ? ColorUtil.rainbow() : RenderUtil.getColor(renderColor.getValue());

            for (final BlockPos blockPos : posList) {
                switch (mode.getValue()) {
                    case "Box":
                        RenderUtil.drawBlockBox(blockPos, color, false);
                        break;
                    case "TwoD":
                        RenderUtil.draw2D(blockPos, color.getRGB(), Color.BLACK.getRGB());
                        break;
                    case "Outline":
                        RenderUtil.drawBlockBox(blockPos, color, false);
                        RenderUtil.renderOne();
                        RenderUtil.drawBlockBox(blockPos, color, false);
                        RenderUtil.renderTwo();
                        RenderUtil.drawBlockBox(blockPos, color, false);
                        RenderUtil.renderThree();
                        RenderUtil.renderFour(color.getRGB());
                        RenderUtil.drawBlockBox(blockPos, color, true);
                        RenderUtil.renderFive();
                        break;
                }
            }
        }
    }
}
