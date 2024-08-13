package cc.xylitol.module.impl.combat;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventUpdate;
import cc.xylitol.event.impl.events.EventWorldLoad;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import com.sun.org.apache.xpath.internal.operations.Mod;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;

public class AutoRunaway extends Module {
    public AutoRunaway() {
        super("AutoRunaway", Category.Combat);
    }
    boolean openInventory = mc.currentScreen instanceof GuiInventory;

    boolean wating2 = false;

    @EventTarget
    public void onUpdate(EventUpdate event){
        //保装备
        if (mc.thePlayer.getHealth() <= 5) {
            for (int i = 1; i <= 3; i++) {
                    int armorSlot = 3 - i;
                    move(8 - armorSlot, true);

                if (mc.thePlayer.getTotalArmorValue() < 4 && wating2) {
                    mc.thePlayer.sendChatMessage("/hub");
                    wating2 = false;
                }
            }
        }
    }

    private void move(int item , Boolean isArmorSlot ) {
        if (item != -1) {
            if (openInventory) {
                mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY));
            }

           mc.playerController.windowClick( mc.thePlayer.inventoryContainer.windowId, isArmorSlot? item : ((item < 9) ? item + 36 : item), 0, 1, mc.thePlayer);
            if (openInventory) {
                mc.getNetHandler().getNetworkManager().sendPacket(new C0DPacketCloseWindow());
            }
        }
    }

    @EventTarget
    public void onUpdate(EventWorldLoad event){

        wating2 = true;
    }
}
