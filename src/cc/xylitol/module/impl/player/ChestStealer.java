package cc.xylitol.module.impl.player;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventMotion;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.TimerUtil;
import cc.xylitol.utils.player.ItemUtils;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.item.*;
import net.minecraft.util.MathHelper;

public class ChestStealer extends Module {
    private final BoolValue postValue = new BoolValue("Post", false);
    private final BoolValue chest = new BoolValue("Chest", true);
    private final BoolValue furnace = new BoolValue("Furnace", true);
    private final BoolValue brewingStand = new BoolValue("BrewingStand", true);

    public static final TimerUtil timer = new TimerUtil();
    public static boolean isChest = false;
    public static TimerUtil openChestTimer = new TimerUtil();
    private final NumberValue delay = new NumberValue("StealDelay", 100, 0, 1000, 10);
    private final BoolValue trash = new BoolValue("PickTrash", true);
    public final BoolValue silentValue = new BoolValue("Silent", true);

    private int nextDelay = 0;

    public ChestStealer() {
        super("ChestStealer", Category.Player);
    }

    @EventTarget
    public void onMotion(EventMotion event) {

        if ((postValue.getValue() && event.isPost()) || (!postValue.getValue() && event.isPre())) {
            if (mc.thePlayer.openContainer == null)
                return;

            if (mc.thePlayer.openContainer instanceof ContainerFurnace && furnace.getValue()) {
                ContainerFurnace container = (ContainerFurnace) mc.thePlayer.openContainer;

                if (isFurnaceEmpty(container) && openChestTimer.delay(100) && timer.delay(100)) {
                    mc.thePlayer.closeScreen();
                    return;
                }

                for (int i = 0; i < container.tileFurnace.getSizeInventory(); ++i) {
                    if (container.tileFurnace.getStackInSlot(i) != null) {
                        if (timer.delay(nextDelay)) {

//                            for (int j = 0; j < 21; ++j) {
                                mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
//                            }
                            nextDelay = (int) (delay.getValue() * MathHelper.getRandomDoubleInRange(0.75, 1.25));
                            timer.reset();
                        }
                    }
                }
            }

            if (mc.thePlayer.openContainer instanceof ContainerBrewingStand && brewingStand.getValue()) {
                ContainerBrewingStand container = (ContainerBrewingStand) mc.thePlayer.openContainer;

                if (isBrewingStandEmpty(container) && openChestTimer.delay(100) && timer.delay(100)) {
                    mc.thePlayer.closeScreen();
                    return;
                }

                for (int i = 0; i < container.tileBrewingStand.getSizeInventory(); ++i) {
                    if (container.tileBrewingStand.getStackInSlot(i) != null) {
                        if (timer.delay(nextDelay)) {
//                            for (int j = 0; j < 21; ++j) {
                                mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
//                            }
                            nextDelay = (int) (delay.getValue() * MathHelper.getRandomDoubleInRange(0.75, 1.25));
                            timer.reset();
                        }
                    }
                }
            }

            if (mc.thePlayer.openContainer instanceof ContainerChest && chest.getValue() && isChest) {
                ContainerChest container = (ContainerChest) mc.thePlayer.openContainer;


                if (isChestEmpty(container) && openChestTimer.delay(100) && timer.delay(100)) {
                    mc.thePlayer.closeScreen();
                    return;
                }

                for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); ++i) {
                    if (container.getLowerChestInventory().getStackInSlot(i) != null) {
                        if (timer.delay(nextDelay) && (isItemUseful(container, i) || trash.getValue())) {
//                            for (int j = 0; j < 21; ++j) {
                                mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
//                            }
                            nextDelay = (int) (delay.getValue() * MathHelper.getRandomDoubleInRange(0.75, 1.25));
                            timer.reset();
                        }
                    }
                }
            }
        }
    }

    private boolean isChestEmpty(ContainerChest c) {
        for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
            if (c.getLowerChestInventory().getStackInSlot(i) != null) {
                if (isItemUseful(c, i) || trash.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isFurnaceEmpty(ContainerFurnace c) {
        for (int i = 0; i < c.tileFurnace.getSizeInventory(); ++i) {
            if (c.tileFurnace.getStackInSlot(i) != null) {
                return false;
            }
        }

        return true;
    }

    private boolean isBrewingStandEmpty(ContainerBrewingStand c) {
        for (int i = 0; i < c.tileBrewingStand.getSizeInventory(); ++i) {
            if (c.tileBrewingStand.getStackInSlot(i) != null) {
                return false;
            }
        }

        return true;
    }

    private boolean isItemUseful(ContainerChest c, int i) {
        ItemStack itemStack = c.getLowerChestInventory().getStackInSlot(i);
        Item item = itemStack.getItem();

        if (item instanceof ItemAxe || item instanceof ItemPickaxe) {
            return true;
        }

        if (item instanceof ItemFood)
            return true;
        if (item instanceof ItemBow || item == Items.arrow)
            return true;

        if (item instanceof ItemPotion && !ItemUtils.isPotionNegative(itemStack))
            return true;
        if (item instanceof ItemSword && ItemUtils.isBestSword(c, itemStack))
            return true;
        if (item instanceof ItemArmor && ItemUtils.isBestArmor(c, itemStack))
            return true;
        if (item instanceof ItemBlock)
            return true;

        return item instanceof ItemEnderPearl;
    }
}
