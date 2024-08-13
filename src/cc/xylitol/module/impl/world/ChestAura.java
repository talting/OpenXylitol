package cc.xylitol.module.impl.world;

import cc.xylitol.Client;
import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventUpdate;
import cc.xylitol.event.impl.events.EventWorldLoad;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.module.impl.combat.KillAura;
import cc.xylitol.module.impl.player.Blink;
import cc.xylitol.utils.TimerUtil;
import cc.xylitol.utils.player.PlayerUtil;
import cc.xylitol.utils.player.RotationUtil;
import cc.xylitol.utils.vector.Vector3d;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFurnace;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjglx.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class ChestAura extends Module {
    private final NumberValue range = new NumberValue("Range", 4, 1, 6, 0.1);
    private final BoolValue rotation = new BoolValue("Rotation", false);
    private final BoolValue movementCorrection = new BoolValue("Movement Fix", false);

    private final NumberValue delay = new NumberValue("Delay", 50, 0, 1000, 20);

    private TimerUtil stopWatch = new TimerUtil();
    private long nextWait = 0;
    private List<BlockPos> found = new ArrayList<>();

    public ChestAura() {
        super("ContainerAura", Category.Player);

    }

    @Override
    public void onEnable() {
        stopWatch.reset();
        found.clear();
    }

    @EventTarget
    private void onWorld(EventWorldLoad e) {
        found.clear();
        stopWatch.reset();
    }

    @EventTarget
//    @EventPriority(12)
    private void onPre(EventUpdate event) {
        if (!stopWatch.delay(nextWait) || mc.currentScreen != null) return;
        int reach = range.getValue().intValue();
        if (KillAura.target != null
                || mc.thePlayer.isUsingItem() || getModule(Scaffold.class).getState() ||
                getModule(Blink.class).getState())
            return;

        for (int x = -reach; x <= reach; x++) {
            for (int y = -reach; y <= reach; y++) {
                for (int z = -reach; z <= reach; z++) {
                    final BlockPos blockPos = new BlockPos(mc.thePlayer).add(x, y, z);
                    if (found.contains(blockPos)) continue;

                    final Block block = PlayerUtil.blockRelativeToPlayer(x, y, z);
                    final Vector3d position = new Vector3d(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);

                    if (block instanceof BlockChest || block instanceof BlockFurnace || block instanceof BlockBrewingStand) {
                        final Vector2f vector2f = RotationUtil.calculate(position);

                        if (rotation.getValue()) {
                            Client.instance.rotationManager.setRotation(vector2f, 180, movementCorrection.getValue());
                            if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && check() && mc.objectMouseOver.getBlockPos().equals(blockPos)) {
//                                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(), mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec);
                                KeyBinding.setKeyBindState(-99, true);
                                KeyBinding.onTick(-99);
                                KeyBinding.setKeyBindState(-99, false);

                                found.add(blockPos);
                                nextWait = delay.getValue().intValue();
                                stopWatch.reset();
                                return;
                            }
                        } else {
                            if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && check()) {
                                KeyBinding.setKeyBindState(-99, true);
                                KeyBinding.onTick(-99);
                                KeyBinding.setKeyBindState(-99, false);

                                found.add(blockPos);
                                nextWait = delay.getValue().intValue();
                                stopWatch.reset();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean check() {
        return mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() instanceof BlockChest || mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() instanceof BlockFurnace || mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() instanceof BlockBrewingStand;
    }
}
