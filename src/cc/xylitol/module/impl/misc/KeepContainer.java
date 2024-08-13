package cc.xylitol.module.impl.misc;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventKey;
import cc.xylitol.event.impl.events.EventPacket;
import cc.xylitol.event.impl.events.EventScreen;
import cc.xylitol.event.impl.events.EventUpdate;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.value.impl.BoolValue;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.*;
import org.lwjglx.input.Keyboard;

import static cc.xylitol.Client.mc;

public class KeepContainer extends Module {

    private GuiContainer container = null;
    private BoolValue Throw = new BoolValue("ThrowItem", false);
    private int times = 0;
    private boolean buyying = true;
    private int PacketTimes = 0;
    private boolean WantThrow = false;

    public KeepContainer() {
        super("KeepContainer",Category.Misc);
    }

    @Override
    public void onDisable() {
        if (container != null) {
            mc.getNetHandler().getNetworkManager().sendPacket(new C0DPacketCloseWindow(container.inventorySlots.windowId));
        }
        container = null;
    }

    @Override
    public void onEnable() {
        PacketTimes = 0;
    }

    @EventTarget
    public void onGui(EventScreen event) {
        if (event.getGuiScreen() instanceof GuiContainer && !(event.getGuiScreen()  instanceof GuiInventory))
            container = (GuiContainer) event.getGuiScreen() ;
    }

    @EventTarget
    public void onKey(EventKey event) {
        if (event.getKey() == Keyboard.KEY_INSERT) {
                buyying = true;
                mc.getNetHandler().getNetworkManager().sendPacket(new C0EPacketClickWindow(times, 0, 1, 4, mc.thePlayer.getHeldItem(), (short) 0));
                times += 1;
            } else {
                if (container == null) {
                    return;
                }
                mc.displayGuiScreen(container);
            }
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
            if (Throw.get()) {
                if (WantThrow) {
                    for (int i = 27; i <= 53; i++) {
                        mc.getNetHandler().getNetworkManager().sendPacket(new C0EPacketClickWindow(times, i, 0, 4, mc.thePlayer.getHeldItem(), (short) 0));
                    }
                    WantThrow = false;
                }
            if (!buyying) {
                if (PacketTimes >= 0 && PacketTimes <= 19) {
                    mc.getNetHandler().getNetworkManager().sendPacket(new C0EPacketClickWindow(times, 0, 1, 4, mc.thePlayer.getHeldItem(), (short) 0));
                    PacketTimes += 1;
                } else if (PacketTimes >= 20 && PacketTimes <= 40) {
                    mc.getNetHandler().getNetworkManager().sendPacket(new C0EPacketClickWindow(times, 1, 1, 1, mc.thePlayer.getHeldItem(), (short) 0));
                    PacketTimes += 1;
                } else if (PacketTimes >= 41 && PacketTimes <= 60) {
                    mc.getNetHandler().getNetworkManager().sendPacket(new C0EPacketClickWindow(times, 2, 1, 4, mc.thePlayer.getHeldItem(), (short) 0));
                    PacketTimes += 1;
                } else if (PacketTimes >= 61 && PacketTimes <= 80) {
                    mc.getNetHandler().getNetworkManager().sendPacket(new C0EPacketClickWindow(times, 3, 1, 0, mc.thePlayer.getHeldItem(), (short) 0));
                    PacketTimes += 1;
                } else if (PacketTimes >= 81 && PacketTimes <= 100) {
                    mc.getNetHandler().getNetworkManager().sendPacket(new C0EPacketClickWindow(times, 4, 1, 1, mc.thePlayer.getHeldItem(), (short) 0));
                    PacketTimes += 1;
                } else if (PacketTimes > 100) {
                    PacketTimes = 0;
                }
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        Packet<?> packet = event.getPacket();

            if (packet instanceof S0EPacketSpawnObject && Throw.get()) {
                event.setCancelled(true);
            }
            if (packet instanceof C02PacketUseEntity) {
                buyying = false;
            }
            if (packet instanceof C0EPacketClickWindow && (((C0EPacketClickWindow) packet).getSlotId() >= 9 && ((C0EPacketClickWindow) packet).getSlotId() <= 12) && buyying) {
                WantThrow = true;
            }

        if (packet instanceof C0DPacketCloseWindow) {
            event.setCancelled(true);
            if (buyying) {
                buyying = false;
            }
        }
        if(packet instanceof S29PacketSoundEffect && ((S29PacketSoundEffect) packet).getSoundName().equals("\"block.wood_pressureplate.click_on\"")){
            if(!buyying){
                event.setCancelled(true);
            }
        }
        if (packet instanceof S30PacketWindowItems) {
            event.setCancelled(true);
        }
            if (packet instanceof S2DPacketOpenWindow) {
                times = ((S2DPacketOpenWindow) packet).getWindowId();
                if (!buyying) {
                    event.setCancelled(true);
            }
        }
        if (packet instanceof S2EPacketCloseWindow) {
            S2EPacketCloseWindow packetCloseWindow = (S2EPacketCloseWindow) event.getPacket();
            if (container != null && container.inventorySlots != null && packetCloseWindow.windowId == container.inventorySlots.windowId) {
                container = null;
            }
        }
    }
}