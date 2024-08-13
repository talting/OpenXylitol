package cc.xylitol.module.impl.combat;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventMotion;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.PacketUtil;
import cc.xylitol.utils.TimerUtil;
import cc.xylitol.utils.player.PlayerUtil;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.RandomUtils;

public class AutoSoup extends Module {
    private final BoolValue postValue = new BoolValue("Post",false);
    private final NumberValue health = new NumberValue("Health", 15, 0, 20, 1);
    private final NumberValue minDelay = new NumberValue("Min Delay", 300, 0, 1000, 1);
    private final NumberValue maxDelay = new NumberValue("Max Delay", 500, 0, 1000, 1);
    private final BoolValue dropBowl = new BoolValue("Drop Bowl",  true);
    private final BoolValue Legit = new BoolValue("Legit",false);
    private final TimerUtil timer = new TimerUtil();
    private boolean switchBack;
    private long decidedTimer;
    private int soup = -37;

    public AutoSoup(){
        super("AutoSoup", Category.Combat);
    }

    @Override
    public void onDisable() {
        switchBack = false;
        soup = -37;
    }

    @EventTarget
    public void onMotion(EventMotion event) {
        if ((postValue.getValue() && event.isPost()) || (!postValue.getValue() && event.isPre())) {
            if (switchBack) {
                PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                if (dropBowl.getValue()) {
                    PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                }
                if (Legit.getValue()) {
                    mc.playerController.updateController();
                } else {
                    PacketUtil.send(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }
                switchBack = false;
                return;
            }

            if (timer.delay(decidedTimer)) {
                if (mc.thePlayer.ticksExisted > 10 && mc.thePlayer.getHealth() < health.getValue().intValue()) {
                    soup = PlayerUtil.findSoup() - 36;

                    if (soup != -37) {
                        if (Legit.getValue()) {
                            mc.thePlayer.inventory.currentItem = soup;
                            mc.gameSettings.keyBindUseItem.setPressed(true);
                        } else {
                            PacketUtil.send(new C09PacketHeldItemChange(soup));
                            PacketUtil.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(soup)));
                        }
                        switchBack = true;
                    } else {
                        int soupInInventory = PlayerUtil.findItem(9, 36, Items.mushroom_stew);
                        if (soupInInventory != -1 && PlayerUtil.hasSpaceHotBar()) {

                            boolean openInventory = !(mc.currentScreen instanceof GuiInventory);
                            if (openInventory) {
                                mc.thePlayer.setSprinting(false);
                                mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                            }

                            mc.playerController.windowClick(0, soupInInventory, 0, 1, mc.thePlayer);

                            if (openInventory) {
                                mc.getNetHandler().addToSendQueue(new C0DPacketCloseWindow());
                            }
                        }
                    }

                    final int delayFirst = (int) Math.floor(Math.min(minDelay.getValue().intValue(), maxDelay.getValue().intValue()));
                    final int delaySecond = (int) Math.ceil(Math.max(minDelay.getValue().intValue(), maxDelay.getValue().intValue()));

                    decidedTimer = RandomUtils.nextInt(delayFirst, delaySecond);
                    timer.reset();
                }
            }
        }
    }
}
