package cc.xylitol.module.impl.player;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventPacket;
import cc.xylitol.event.impl.events.EventUpdate;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.module.impl.world.Disabler;
import cc.xylitol.utils.PacketUtil;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class SpeedMine extends Module {
    private final NumberValue speed = new NumberValue("Speed", 1.1, 1.0, 3.0, 0.1);
    private final BoolValue speedCheckBypass = new BoolValue("VanillaCheckBypass", false);

    private EnumFacing facing;
    private BlockPos pos;
    private boolean boost = false;
    private float damage = 0.0F;

    public SpeedMine() {
        super("SpeedMine", Category.Player);
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null) return;
        if (speedCheckBypass.getValue()) {
            mc.thePlayer.removePotionEffect(Potion.digSpeed.id);
        }
    }

    @EventTarget
    private void onPacket(EventPacket e) {
        if (e.packet instanceof C07PacketPlayerDigging) {
            if (((C07PacketPlayerDigging) e.getPacket()).getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                boost = true;
                pos = ((C07PacketPlayerDigging) e.getPacket()).getPosition();
                facing = ((C07PacketPlayerDigging) e.getPacket()).getFacing();
                damage = 0.0F;
            } else if ((((C07PacketPlayerDigging) e.getPacket()).getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK)
                    || (((C07PacketPlayerDigging) e.getPacket()).getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK)) {
                boost = false;
                pos = null;
                facing = null;
            }
        }
    }

    @EventTarget
    private void onUpdate(EventUpdate e) {
        if (speedCheckBypass.getValue()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 996 * 9 * 10, 2));
        }
        if (mc.playerController.extendedReach()) {
            mc.playerController.blockHitDelay = 0;
        } else if (pos != null && boost) {
            IBlockState blockState = mc.theWorld.getBlockState(pos);
            damage += blockState.getBlock().getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, pos) * speed.getValue();
            if (damage >= 1.0F) {
                mc.theWorld.setBlockState(pos, Blocks.air.getDefaultState(), 11);
                Disabler disabler = getModule(Disabler.class);
                PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, pos, facing));

                PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, facing));
                damage = 0.0F;
                boost = false;
            }
        }
    }
}
