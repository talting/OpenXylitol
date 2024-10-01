package cc.xylitol.module.impl.move;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.*;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.module.impl.combat.Gapple;
import cc.xylitol.module.impl.combat.KillAura;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.ModeValue;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.Unpooled;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import top.fl0wowp4rty.phantomshield.annotations.Native;

import static cc.xylitol.utils.PacketUtil.sendPacketNoEvent;

@Native
public class NoSlow extends Module {
    public NoSlow() {
        super("Noslow", Category.Movement);
    }

    private boolean doDropSlow = false;
    private int ticks = 0;
    private final ModeValue mode = new ModeValue("Mode", new String[]{"Grim", "Old Grim", "Watchdog", "16v16"}, "Grim");
    private final BoolValue sword = new BoolValue("Sword", true);

    private final BoolValue food = new BoolValue("Food (Drop, Golden Apple Only)", true);
    private final BoolValue bow = new BoolValue("Bow", true);

    private boolean isHoldingPotionAndSword(ItemStack stack, boolean checkSword, boolean checkPotionFood) {
        if (stack == null) {
            return false;
        } else if (stack.getItem() instanceof ItemAppleGold && checkPotionFood) {
            return food.getValue();
        } else if (stack.getItem() instanceof ItemPotion && checkPotionFood) {
            return food.getValue() && !ItemPotion.isSplash(stack.getMetadata());
        } else if (stack.getItem() instanceof ItemFood && checkPotionFood) {
            return food.getValue();
        } else if (stack.getItem() instanceof ItemSword && checkSword) {
            return sword.getValue();
        } else if (stack.getItem() instanceof ItemBow && checkPotionFood) {
            return bow.getValue();
        } else return stack.getItem() instanceof ItemBucketMilk && checkPotionFood && food.getValue();
    }


    @Override
    public void onEnable() {

        doDropSlow = false;
        ticks = 0;
    }

    @EventTarget
    public void onTick(EventTick e) {

        if (doDropSlow) {
            ticks++;
            if (ticks == 35) {
                doDropSlow = false;
                ticks = 0;
            }
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer.getHeldItem() != null && food.getValue() && (mc.thePlayer.getHeldItem().getItem() instanceof ItemAppleGold) /*&& !Gapple.startEat*/) {
            if (doDropSlow) {
                if (ticks > 1)
                    mc.thePlayer.stopUsingItem();
            }
        }
    }


    @EventTarget
    public void onMotion(EventMotion event) {
        if (mc.thePlayer.getHeldItem() != null) {
            if (event.isPre()) {

                if ((mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) && mc.thePlayer.isUsingItem() && bow.getValue() && mode.getValue().contains("Grim")) {
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                    mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("test", new PacketBuffer(Unpooled.buffer())));
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }

                if (isHoldingPotionAndSword(mc.thePlayer.getHeldItem(), true, false) && mc.thePlayer.isUsingItem() && !KillAura.isBlocking) {
                    switch (mode.get()) {
                        case "Grim":
                        case "16v16":
                            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                            mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("test", new PacketBuffer(Unpooled.buffer())));
                            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                            break;
                        case "Old Grim":
                            mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
                            break;
                        case "Watchdog":
                            sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));

                            PacketWrapper useItemMainHand = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                            useItemMainHand.write(Type.VAR_INT, 1);
                            PacketUtil.sendToServer(useItemMainHand, Protocol1_8To1_9.class, true, true);
                            break;
                    }

                }
                if (mode.is("Watchdog")) {

                    if ((mc.thePlayer.isEatingOrDrinking() || (mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() instanceof ItemBow)) && mc.thePlayer.ticksExisted % 3 == 0) {
                        sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), EnumFacing.UP.getIndex(), null, 0, 0, 0));
                    }
                }
            }

            if (isHoldingPotionAndSword(mc.thePlayer.getHeldItem(), true, false) && mc.thePlayer.isUsingItem() && !KillAura.isBlocking && mc.thePlayer.getHeldItem().stackSize >= 2) {
                if (event.isPost()) {
                    switch (mode.get()) {
                        case "Old Grim":
                        case "Grim":
                            mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));

                            PacketWrapper useItemMainHand = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                            useItemMainHand.write(Type.VAR_INT, 1);
                            PacketUtil.sendToServer(useItemMainHand, Protocol1_8To1_9.class, true, true);
                            break;

                    }
                }
            }

        }
    }


    @EventTarget
    public void onPacketReceive(EventPacket event) {
        if (event.getEventType() == EventPacket.EventState.SEND) {

            Packet<?> packet = event.getPacket();
            if (mc.thePlayer == null || mc.isSingleplayer()) return;
            if (mc.thePlayer.getHeldItem() != null && food.getValue() /*&& !Gapple.startEat*/ && mode.getValue().contains("Grim") && (mc.thePlayer.getHeldItem().getItem() instanceof ItemAppleGold)) {
                if (packet instanceof C08PacketPlayerBlockPlacement && ((C08PacketPlayerBlockPlacement) packet).getPosition().getY() == -1) {
                    if (!doDropSlow) {
                        mc.getNetHandler().addToSendQueue(
                                new C07PacketPlayerDigging(
                                        C07PacketPlayerDigging.Action.DROP_ITEM,
                                        BlockPos.ORIGIN,
                                        EnumFacing.DOWN
                                )
                        );
                        doDropSlow = true;
                    } else {
                        event.setCancelled(true);
                    }
                }
                if (doDropSlow) {
                    if (packet instanceof C09PacketHeldItemChange) {
                        doDropSlow = false;
                    }
                    if (packet instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging) packet).getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                        event.setCancelled(true);
                    }
                }
            }

        }
    }

    @EventTarget
    public void onSlowDown(EventSlowDown event) {
        if (mc.thePlayer.isUsingItem() && (isHoldingPotionAndSword(mc.thePlayer.getHeldItem(), true, mode.is("Watchdog") || (mc.thePlayer.getHeldItem().getItem() instanceof ItemBow || ticks > 4)))) {
            event.setCancelled(true);
            mc.thePlayer.setSprinting(true);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        } else {
            event.setForwardMultiplier(0.2f);
            event.setStrafeMultiplier(0.2f);
        }
    }

}
