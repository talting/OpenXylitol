package cc.xylitol.utils;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

import java.util.HashMap;
import java.util.Map;

import static cc.xylitol.utils.player.RotationUtil.mc;

public class BlockUtil {


    public static boolean isAirBlock(final BlockPos blockPos) {
        final Block block = Minecraft.getMinecraft().theWorld.getBlockState(blockPos).getBlock();
        return block instanceof BlockAir;
    }

    public static boolean isValidBock(final BlockPos blockPos) {
        final Block block = Minecraft.getMinecraft().theWorld.getBlockState(blockPos).getBlock();
        return !(block instanceof BlockLiquid) && !(block instanceof BlockAir) && !(block instanceof BlockChest) && !(block instanceof BlockFurnace) && !(block instanceof BlockLadder) && !(block instanceof BlockTNT);
    }
    public static BlockPos getBlockCorner(BlockPos start, BlockPos end) {
        for (int x = 0; x <= 1; ++x) {
            for (int y = 0; y <= 1; ++y) {
                for (int z = 0; z <= 1; ++z) {
                    BlockPos pos = new BlockPos(end.getX() + x, end.getY() + y, end.getZ() + z);
                    if (!isBlockBetween(start, pos)) {
                        return pos;
                    }
                }
            }
        }

        return null;
    }

    public static boolean isBlockBetween(BlockPos start, BlockPos end) {
        int startX = start.getX();
        int startY = start.getY();
        int startZ = start.getZ();
        int endX = end.getX();
        int endY = end.getY();
        int endZ = end.getZ();
        double diffX = (double) (endX - startX);
        double diffY = (double) (endY - startY);
        double diffZ = (double) (endZ - startZ);
        double x = (double) startX;
        double y = (double) startY;
        double z = (double) startZ;
        double STEP = 0.1D;
        int STEPS = (int) Math.max(Math.abs(diffX), Math.max(Math.abs(diffY), Math.abs(diffZ))) * 4;

        for (int i = 0; i < STEPS - 1; ++i) {
            x += diffX / (double) STEPS;
            y += diffY / (double) STEPS;
            z += diffZ / (double) STEPS;
            if (x != (double) endX || y != (double) endY || z != (double) endZ) {
                BlockPos pos = new BlockPos(x, y, z);
                Block block = mc.theWorld.getBlockState(pos).getBlock();
                if (block.getMaterial() != Material.air && block.getMaterial() != Material.water && !(block instanceof BlockVine) && !(block instanceof BlockLadder)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Block getBlock(final BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos).getBlock();
    }

    public static Map<BlockPos, Block> searchBlocks(final int radius) {
        final Map<BlockPos, Block> blocks = new HashMap<BlockPos, Block>();
        for (int x = radius; x > -radius; --x) {
            for (int y = radius; y > -radius; --y) {
                for (int z = radius; z > -radius; --z) {
                    final BlockPos blockPos = new BlockPos(mc.thePlayer.lastTickPosX + x, mc.thePlayer.lastTickPosY + y, mc.thePlayer.lastTickPosZ + z);
                    final Block block = getBlock(blockPos);
                    blocks.put(blockPos, block);
                }
            }
        }
        return blocks;
    }
}
