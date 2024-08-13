package cc.xylitol.module.impl.player;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventUpdate;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.ui.hud.notification.NotificationManager;
import cc.xylitol.ui.hud.notification.NotificationType;
import cc.xylitol.utils.DebugUtil;
import cc.xylitol.utils.TimerUtil;
import cc.xylitol.value.impl.ModeValue;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import org.lwjglx.input.Mouse;

public class MidPearl extends Module {
    public MidPearl() {
        super("MidPearl", Category.Player);
    }

    private final ModeValue mode = new ModeValue("Mouse", new String[]{"Middle", "Mouse4", "Mouse5"}, "Middle");
    private boolean pearlThrown = false; // 新增变量来检查末影珍珠是否已被扔出
    private boolean Down; // 新增变量来检查末影珍珠是否已被扔出
    private TimerUtil timer = new TimerUtil();

    @EventTarget
    public void onUpdate(EventUpdate event) {
        handlePearlThrow();
    }

    public void handlePearlThrow() {
        boolean currentDownState = false;
        String modeValue = mode.getValue();
        switch (modeValue) {
            case "Middle":
                Down = Mouse.isButtonDown(2); // 中键
                break;
            case "Mouse4":
                Down = Mouse.isButtonDown(3); // 鼠标按键4
                break;
            case "Mouse5":
                Down = Mouse.isButtonDown(4); // 鼠标按键5
                break;
        }

        // 检测鼠标按钮从按下到释放的瞬间
        if (!currentDownState && Down && !pearlThrown) {
            throwPearl();
            pearlThrown = true; // 执行完毕后标记为已扔出
        } else if (!currentDownState) {
            // 如果当前状态是未按下，且之前的状态也是未按下，则重置pearlThrown状态
            // 这样可以在下一次按键时重新执行throwPearl
            pearlThrown = false;
        }

        // 更新Down状态为当前状态，为下一次检测做准备
        Down = currentDownState;
    }


    private void throwPearl() {
        EntityPlayerSP player = mc.thePlayer;

        if (player != null) {
            int pearlSlot = findEnderPearlSlot(player);
            // 检查是否找到末影珍珠
            if (pearlSlot != -1) {
                // 记录当前的槽位
                int originalSlot = player.inventory.currentItem;
                // 设置玩家当前的槽位为末影珍珠的槽位
                DebugUtil.log(1);
                player.inventory.currentItem = pearlSlot;
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(player.inventory.currentItem));

                if (timer.delay(200)) {
                    // 发送一条包来模拟物品使用动作
                    mc.playerController.sendUseItem(player, mc.theWorld, mc.thePlayer.getHeldItem());
                    timer.reset();
                }

                // 恢复到原先选中的槽位
                player.inventory.currentItem = originalSlot;
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(player.inventory.currentItem));
            } else {
                // 如果没有找到末影珍珠，不执行任何操作
                DebugUtil.log("No Ender Pearl found in inventory.");
                NotificationManager.post(NotificationType.SUCCESS, "MidPearl", "No Ender Pearl Found In Inventory.", 10);
                pearlThrown = false; // 重置珍珠抛掷状态
            }
        } else {
            pearlThrown = false; // 如果玩家对象为null，也重置状态
        }
    }


    private int findEnderPearlSlot(EntityPlayerSP player) {
        for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
            ItemStack itemstack = player.inventory.mainInventory[i];
            if (itemstack != null && itemstack.getItem() == Items.ender_pearl) {
                return i;
            }
        }
        return -1;
    }
}