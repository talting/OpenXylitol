package cc.xylitol.module.impl.player;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventMotion;
import cc.xylitol.event.impl.events.EventPacket;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.module.impl.combat.KillAura;
import cc.xylitol.module.impl.world.Scaffold;
import cc.xylitol.utils.TimerUtil;
import cc.xylitol.utils.player.InventoryUtil;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.ModeValue;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static cc.xylitol.utils.player.InventoryUtil.windowClick;


public class InvCleaner extends Module {

    public InvCleaner() {
        super("InvManager", Category.World);
    }

    private final BoolValue postValue = new BoolValue("Post", false);

    private final ModeValue mode = new ModeValue("Mode", new String[]{"Spoof", "OpenInv"}, "Spoof");
    private final NumberValue delay = new NumberValue("Delay", 5, 0, 300, 10);
    private final NumberValue armorDelay = new NumberValue("Armor Delay", 20, 0, 300, 10);

    public final NumberValue slotWeapon = new NumberValue("Weapon Slot", 1, 1, 9, 1);
    public final NumberValue slotPick = new NumberValue("Pickaxe Slot", 2, 1, 9, 1);
    public final NumberValue slotAxe = new NumberValue("Axe Slot", 3, 1, 9, 1);
    public final NumberValue slotGapple = new NumberValue("Gapple Slot", 4, 1, 9, 1);

    public final NumberValue slotShovel = new NumberValue("Shovel Slot", 5, 1, 9, 1);
    public final NumberValue slotBow = new NumberValue("Bow Slot", 6, 1, 9, 1);
    public final NumberValue slotBlock = new NumberValue("Block Slot", 7, 1, 9, 1);
    public final NumberValue slotPearl = new NumberValue("Pearl Slot", 8, 1, 9, 1);

    public final String[] serverItems = {"选择游戏", "加入游戏", "职业选择菜单", "离开对局", "再来一局", "selector", "tracking compass", "(right click)", "tienda ", "perfil", "salir", "shop", "collectibles", "game", "profil", "lobby", "show all", "hub", "friends only", "cofre", "(click", "teleport", "play", "exit", "hide all", "jeux", "gadget", " (activ", "emote", "amis", "bountique", "choisir", "choose "};

    private final int[] bestArmorPieces = new int[4];
    private final List<Integer> trash = new ArrayList<>();
    private final int[] bestToolSlots = new int[3];
    private final List<Integer> gappleStackSlots = new ArrayList<>();
    private int bestSwordSlot;
    private int bestPearlSlot;

    private int bestBowSlot;
    private boolean serverOpen;
    private boolean clientOpen;

    private int ticksSinceLastClick;

    private boolean nextTickCloseInventory;
    private TimerUtil timer = new TimerUtil();


    @EventTarget
    private void onPacket(EventPacket event) {
        final Packet<?> packet = event.getPacket();
        if (packet instanceof S2DPacketOpenWindow) {
            this.clientOpen = false;
            this.serverOpen = false;
        }

    }

    @EventTarget
    private void onPacketSend(EventPacket event) {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof C16PacketClientStatus) {
            final C16PacketClientStatus clientStatus = (C16PacketClientStatus) packet;

            if (clientStatus.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                this.clientOpen = true;
                this.serverOpen = true;
            }
        } else if (packet instanceof C0DPacketCloseWindow) {
            final C0DPacketCloseWindow packetCloseWindow = (C0DPacketCloseWindow) packet;

            if (packetCloseWindow.windowId == mc.thePlayer.inventoryContainer.windowId) {
                this.clientOpen = false;
                this.serverOpen = false;
            }
        } else if (packet instanceof C0EPacketClickWindow && !mc.thePlayer.isUsingItem()) {
            this.ticksSinceLastClick = 0;

        }
    }


    private boolean dropItem(final List<Integer> listOfSlots) {

        if (!listOfSlots.isEmpty()) {
            int slot = listOfSlots.remove(0);
            windowClick(mc, slot, 1, InventoryUtil.ClickType.DROP_ITEM);
            return true;
        }
        return false;
    }


    @EventTarget
    private void onMotion(EventMotion event) {
        if ((postValue.getValue() && event.isPost()) || (!postValue.getValue() && event.isPre())) {
            if (!mc.thePlayer.isOnLadder() && !(getModule(Blink.class).getState()) && !mc.thePlayer.isUsingItem() && (mc.currentScreen == null || mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiInventory || !mc.thePlayer.isSpectator() || mc.currentScreen instanceof GuiIngameMenu) && KillAura.target == null && (!getModule(Scaffold.class).getState())) {

                this.ticksSinceLastClick++;

                if (this.ticksSinceLastClick < Math.floor(this.delay.getValue().doubleValue() / 50)) return;

                if (this.clientOpen || (mc.currentScreen == null && !this.mode.getValue().equals("OpenInv"))) {
                    this.clear();

                    for (int slot = InventoryUtil.INCLUDE_ARMOR_BEGIN; slot < InventoryUtil.END; slot++) {
                        final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(slot).getStack();

                        if (stack != null) {
                            if (stack.getItem() instanceof ItemSword && InventoryUtil.isBestSword(mc.thePlayer, stack)) {
                                this.bestSwordSlot = slot;
                            } else if (stack.getItem() instanceof ItemTool && InventoryUtil.isBestTool(mc.thePlayer, stack)) {
                                final int toolType = InventoryUtil.getToolType(stack);
                                if (toolType != -1 && slot != this.bestToolSlots[toolType])
                                    this.bestToolSlots[toolType] = slot;
                            } else if (stack.getItem() instanceof ItemArmor && InventoryUtil.isBestArmor(mc.thePlayer, stack)) {
                                final ItemArmor armor = (ItemArmor) stack.getItem();

                                final int pieceSlot = this.bestArmorPieces[armor.armorType];

                                if (pieceSlot == -1 || slot != pieceSlot)
                                    this.bestArmorPieces[armor.armorType] = slot;
                            } else if (stack.getItem() instanceof ItemBow && InventoryUtil.isBestBow(mc.thePlayer, stack)) {
                                if (slot != this.bestBowSlot)
                                    this.bestBowSlot = slot;
                            } else if (stack.getItem() instanceof ItemAppleGold) {
                                this.gappleStackSlots.add(slot);
                            } else if (stack.getItem() instanceof ItemEnderPearl) {
                                this.bestPearlSlot = slot;
                            } else if (!this.trash.contains(slot) && !isValidStack(stack)) {
                                if (Arrays.stream(serverItems).anyMatch(stack.getDisplayName()::contains)) continue;
                                if (stack.getItem() instanceof ItemSkull) continue;
                                this.trash.add(slot);
                            }
                        }
                    }

                    final boolean busy = (!this.trash.isEmpty()) || this.equipArmor(false) || this.sortItems(false);

                    if (!busy) {
                        if (this.nextTickCloseInventory) {
                            if (mode.is("Spoof")) this.close();
                            this.nextTickCloseInventory = false;
                        } else {
                            this.nextTickCloseInventory = true;
                        }
                        return;
                    } else {
                        boolean waitUntilNextTick = !this.serverOpen;

                        if (mode.is("Spoof")) this.open();

                        if (this.nextTickCloseInventory)
                            this.nextTickCloseInventory = false;

                        if (waitUntilNextTick) return;
                    }


                    if (timer.hasTimeElapsed(this.armorDelay.getValue().floatValue()) && this.equipArmor(true))
                        return;
                    if (this.dropItem(this.trash)) return;
                    this.sortItems(true);
                }
            }
        }
    }

    ;

    private boolean sortItems(final boolean moveItems) {
        int goodSwordSlot = this.slotWeapon.getValue().intValue() + 35;

        if (this.bestSwordSlot != -1) {
            if (this.bestSwordSlot != goodSwordSlot) {
                if (moveItems) {
                    this.putItemInSlot(goodSwordSlot, this.bestSwordSlot);
                    this.bestSwordSlot = goodSwordSlot;
                }

                return true;
            }
        }
        int goodBowSlot = this.slotBow.getValue().intValue() + 35;

        if (this.bestBowSlot != -1) {
            if (this.bestBowSlot != goodBowSlot) {
                if (moveItems) {
                    this.putItemInSlot(goodBowSlot, this.bestBowSlot);
                    this.bestBowSlot = goodBowSlot;
                }
                return true;
            }
        }
        int goodGappleSlot = this.slotGapple.getValue().intValue() + 35;

        if (!this.gappleStackSlots.isEmpty()) {
            this.gappleStackSlots.sort(Comparator.comparingInt(slot -> mc.thePlayer.inventoryContainer.getSlot(slot).getStack().stackSize));

            final int bestGappleSlot = this.gappleStackSlots.get(0);

            if (bestGappleSlot != goodGappleSlot) {
                if (moveItems) {
                    this.putItemInSlot(goodGappleSlot, bestGappleSlot);
                    this.gappleStackSlots.set(0, goodGappleSlot);
                }
                return true;
            }
        }


        final int[] toolSlots = {
                slotPick.getValue().intValue() + 35,
                slotAxe.getValue().intValue() + 35,
                slotShovel.getValue().intValue() + 35};

        for (final int toolSlot : this.bestToolSlots) {
            if (toolSlot != -1) {
                final int type = InventoryUtil.getToolType(mc.thePlayer.inventoryContainer.getSlot(toolSlot).getStack());

                if (type != -1) {
                    if (toolSlot != toolSlots[type]) {
                        if (moveItems) {
                            this.putToolsInSlot(type, toolSlots);
                        }
                        return true;
                    }
                }
            }
        }

        int goodBlockSlot = this.slotBlock.getValue().intValue() + 35;
        int mostBlocksSlot = getMostBlocks();
        if (mostBlocksSlot != -1 && mostBlocksSlot != goodBlockSlot) {
            Slot dss = mc.thePlayer.inventoryContainer.getSlot(goodBlockSlot);
            ItemStack dsis = dss.getStack();
            if (!(dsis != null && dsis.getItem() instanceof ItemBlock && dsis.stackSize >= mc.thePlayer.inventoryContainer.getSlot(mostBlocksSlot).getStack().stackSize && Arrays.stream(serverItems).noneMatch(dsis.getDisplayName().toLowerCase()::contains))) {
                this.putItemInSlot(goodBlockSlot, mostBlocksSlot);
            }
        }

        int goodPearlSlot = this.slotPearl.getValue().intValue() + 35;

        if (this.bestPearlSlot != -1) {
            if (this.bestPearlSlot != goodPearlSlot) {
                if (moveItems) {
                    this.putItemInSlot(goodPearlSlot, this.bestPearlSlot);
                    this.bestPearlSlot = goodPearlSlot;
                }
                return true;
            }
        }
        return false;
    }

    public int getMostBlocks() {
        int stack = 0;
        int biggestSlot = -1;
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack is = slot.getStack();
            if (is != null && is.getItem() instanceof ItemBlock && is.stackSize > stack && Arrays.stream(serverItems).noneMatch(is.getDisplayName().toLowerCase()::contains)) {
                stack = is.stackSize;
                biggestSlot = i;
            }
        }
        return biggestSlot;
    }

    private boolean equipArmor(boolean moveItems) {
        for (int i = 0; i < this.bestArmorPieces.length; i++) {
            final int piece = this.bestArmorPieces[i];

            if (piece != -1) {
                int armorPieceSlot = i + 5;
                final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(armorPieceSlot).getStack();
                if (stack != null)
                    continue;

                if (moveItems) {
                    windowClick(mc, piece, 0, InventoryUtil.ClickType.SHIFT_CLICK);
                }
                timer.reset();
                return true;
            }
        }
        return false;
    }

    private void putItemInSlot(final int slot, final int slotIn) {
        windowClick(mc, slotIn,
                slot - 36,
                InventoryUtil.ClickType.SWAP_WITH_HOT_BAR_SLOT);
    }

    private void putToolsInSlot(final int tool, final int[] toolSlots) {
        final int toolSlot = toolSlots[tool];

        windowClick(mc, this.bestToolSlots[tool],
                toolSlot - 36,
                InventoryUtil.ClickType.SWAP_WITH_HOT_BAR_SLOT);
        this.bestToolSlots[tool] = toolSlot;
    }

    private static boolean isValidStack(final ItemStack stack) {
        if (stack.getItem() instanceof ItemBlock && InventoryUtil.isStackValidToPlace(stack)) {
            return true;
        } else if (stack.getItem() instanceof ItemPotion && InventoryUtil.isBuffPotion(stack)) {
            return true;
        } else if (stack.getItem() instanceof ItemFood && InventoryUtil.isGoodFood(stack)) {
            return true;
        } else {
            return InventoryUtil.isGoodItem(stack);
        }
    }

    @Override
    public void onEnable() {
        this.ticksSinceLastClick = 0;

        this.clientOpen = mc.currentScreen instanceof GuiInventory;
        this.serverOpen = this.clientOpen;
    }

    @Override
    public void onDisable() {
        this.close();
        this.clear();
    }

    private void open() {
        if (!this.clientOpen && !this.serverOpen) {
            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            this.serverOpen = true;
        }
    }

    private void close() {
        if (!this.clientOpen && this.serverOpen) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
            this.serverOpen = false;
        }
    }

    private void clear() {
        this.trash.clear();
        this.bestBowSlot = -1;
        this.bestSwordSlot = -1;
        this.gappleStackSlots.clear();
        Arrays.fill(this.bestArmorPieces, -1);
        Arrays.fill(this.bestToolSlots, -1);
    }
}
