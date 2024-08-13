
package cc.xylitol.module.impl.combat;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventAttack;
import cc.xylitol.event.impl.events.EventPacket;
import cc.xylitol.event.impl.events.EventUpdate;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.player.ItemUtils;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

import java.util.Objects;

public class AutoWeapon extends Module {
    public AutoWeapon(){
        super("AutoWeapon", Category.Combat);
    }
    private final BoolValue silentValue = new BoolValue("SpoofItem",false);
    private final NumberValue ticksValue = new NumberValue("SpoofTicks",10, 1, 20,1);
    private final BoolValue itemTool = new BoolValue("ItemTool",true);
    private boolean attackEnemy = false;
    private int spoofedSlot = 0;

    @EventTarget
    public void onAttack(EventAttack event){
        attackEnemy = true;
    }

    @EventTarget
    public void onPacket(EventPacket event){
        if (event.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity) event.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK && attackEnemy) {
            attackEnemy = false;

            // Find best weapon in hotbar (Kotlin Style)
            int slot = -1;
            double maxDamage = 0;

            for (int i = 0; i < 9; i++) {
                if (mc.thePlayer.inventory.getStackInSlot(i) != null
                        && (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemSword
                        || (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemTool && itemTool.getValue()))) {
                    double damage = (mc.thePlayer.inventory.getStackInSlot(i).getAttributeModifiers().get("generic.attackDamage").stream().findFirst().orElse(null) != null
                            ? Objects.requireNonNull(mc.thePlayer.inventory.getStackInSlot(i).getAttributeModifiers().get("generic.attackDamage").stream().findFirst().orElse(null)).getAmount() : 0) +
                            1.25 * ItemUtils.getEnchantment(mc.thePlayer.inventory.getStackInSlot(i), Enchantment.sharpness);

                    if (damage > maxDamage) {
                        maxDamage = damage;
                        slot = i;
                    }
                }
            }

            if (slot == mc.thePlayer.inventory.currentItem || slot == -1) // If in hand no need to swap
                return;

            // Switch to best weapon
            if (silentValue.getValue()) {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(slot));
                spoofedSlot = ticksValue.getValue().intValue();
            } else {
                mc.thePlayer.inventory.currentItem = slot;
                mc.playerController.updateController();
            }

            // Resend attack packet
            mc.getNetHandler().addToSendQueue(event.getPacket());
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate event){
        // Switch back to old item after some time
        if (spoofedSlot > 0) {
            if (spoofedSlot == 1)
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            spoofedSlot--;
        }
    }
}
