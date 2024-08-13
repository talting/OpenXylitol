package cc.xylitol.module.impl.player;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventRender3D;
import cc.xylitol.event.impl.events.EventTick;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.TimerUtil;
import cc.xylitol.utils.render.ColorUtil;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.NumberValue;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class XRay extends Module {
    public XRay() {
        super("XRay", Category.Render);
    }

    /* fields */
    public static int alpha;
    public static boolean isEnabled;
    public static List<Integer> blockIdList = Lists.newArrayList();
    public static List<BlockPos> blockPosList = new CopyOnWriteArrayList<>();
    private TimerUtil timer = new TimerUtil();
    private final NumberValue opacity = new NumberValue("Opacity", 160, 0, 255, 5);
    private static final BoolValue esp = new BoolValue("ESP", true);
    private final BoolValue tracers = new BoolValue("Tracers", true);
    private final BoolValue dia = new BoolValue("Diamond", true);
    private final BoolValue rs = new BoolValue("Redstone", true);
    private final BoolValue emb = new BoolValue("Emerald", true);
    private final BoolValue lap = new BoolValue("Lapis", true);
    private final BoolValue iron = new BoolValue("Iron", true);
    private final BoolValue coal = new BoolValue("Coal", true);
    private final BoolValue gold = new BoolValue("Gold", true);
    private static final NumberValue distance = new NumberValue("Distance", 42, 16, 256, 4);
    private final BoolValue update = new BoolValue("Chunk-Update", true);
    private final NumberValue delay = new NumberValue("Delay", 10.0, 1.0, 30.0, 0.5);

    @Override
    public void onEnable() {
        onToggle(true);
    }

    @Override
    public void onDisable() {
        onToggle(false);
        blockIdList.clear();
        timer.reset();
    }

    private void onToggle(boolean enabled) {
        blockPosList.clear();
        mc.renderGlobal.loadRenderers();
        isEnabled = enabled;
    }

    @EventTarget
    public void update(EventTick event) {
        if (alpha != opacity.getValue()) {
            mc.renderGlobal.loadRenderers();
            alpha = opacity.getValue().intValue();

        } else if (update.getValue()) {
            if (timer.delay(1000 * delay.getValue().longValue())) {
                mc.renderGlobal.loadRenderers();
                timer.reset();
            }
        }
    }

    @EventTarget
    public void onRender3D(EventRender3D e) {
        if (esp.getValue()) {
            for (BlockPos pos : blockPosList) {
                if (mc.thePlayer.getDistance(pos.getX(), pos.getZ()) <= distance.getValue()) {
                    Block block = mc.theWorld.getBlockState(pos).getBlock();

                    if (block == Blocks.diamond_ore && dia.getValue()) {
                        int id = Block.getIdFromBlock(block);
                        if(!blockIdList.contains(id)) blockIdList.add(id);
                        render3D(pos, 0, 255, 255);
                    } else if (block == Blocks.iron_ore && iron.getValue()) {

                        int id = Block.getIdFromBlock(block);
                        if(!blockIdList.contains(id)) blockIdList.add(id);
                        render3D(pos, 225, 225, 225);
                    } else if (block == Blocks.lapis_ore && lap.getValue()) {

                        int id = Block.getIdFromBlock(block);
                        if(!blockIdList.contains(id)) blockIdList.add(id);
                        render3D(pos, 0, 0, 255);
                    } else if (block == Blocks.redstone_ore && rs.getValue()) {

                        int id = Block.getIdFromBlock(block);
                        if(!blockIdList.contains(id)) blockIdList.add(id);
                        render3D(pos, 255, 0, 0);
                    } else if (block == Blocks.coal_ore && coal.getValue()) {

                        int id = Block.getIdFromBlock(block);
                        if(!blockIdList.contains(id)) blockIdList.add(id);
                        render3D(pos, 0, 30, 30);
                    } else if (block == Blocks.emerald_ore && emb.getValue()) {

                        int id = Block.getIdFromBlock(block);
                        if(!blockIdList.contains(id)) blockIdList.add(id);
                        render3D(pos, 0, 255, 0);
                    } else if (block == Blocks.gold_ore && gold.getValue()) {

                        int id = Block.getIdFromBlock(block);
                        if(!blockIdList.contains(id)) blockIdList.add(id);
                        render3D(pos, 255, 255, 0);
                    }
                }
            }
        }
    }

    private void render3D(BlockPos pos, int red, int green, int blue) {
        if (esp.getValue()) {
            RenderUtil.drawSolidBlockESP(pos, ColorUtil.getColor(red, green, blue));
        }

        if (tracers.getValue()) {
            RenderUtil.drawLine(pos, ColorUtil.getColor(red, green, blue));
        }
    }

    public static boolean showESP() {
        return XRay.esp.getValue();
    }

    public static double getDistance() {
        return XRay.distance.getValue();
    }
}
