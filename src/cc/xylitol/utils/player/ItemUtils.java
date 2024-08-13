
package cc.xylitol.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Optional;

public final class ItemUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final int[] itemHelmet;
    private static final int[] itemChestPlate;
    private static final int[] itemLeggings;
    private static final int[] itemBoots;

    static {
        itemHelmet = new int[]{298, 302, 306, 310, 314};
        itemChestPlate = new int[]{299, 303, 307, 311, 315};
        itemLeggings = new int[]{300, 304, 308, 312, 316};
        itemBoots = new int[]{301, 305, 309, 313, 317};
    }

    public static float getSwordDamage(ItemStack itemStack) {
        float damage = 0f;
        Optional<AttributeModifier> attributeModifier = itemStack.getAttributeModifiers().values().stream().findFirst();
        if (attributeModifier.isPresent()) {
            damage = (float) attributeModifier.get().getAmount();
        }
        return damage + EnchantmentHelper.getModifierForCreature(itemStack, EnumCreatureAttribute.UNDEFINED);
    }


    public static boolean isBestSword(ContainerChest c, ItemStack item) {
        float itemdamage1 = getSwordDamage(item);
        float itemdamage2 = 0f;
        for (int i = 0; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                float tempdamage = getSwordDamage(mc.thePlayer.inventoryContainer.getSlot(i).getStack());
                if (tempdamage >= itemdamage2)
                    itemdamage2 = tempdamage;
            }
        }
        for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
            if (c.getLowerChestInventory().getStackInSlot(i) != null) {
                float tempdamage = getSwordDamage(c.getLowerChestInventory().getStackInSlot(i));
                if (tempdamage >= itemdamage2)
                    itemdamage2 = tempdamage;
            }
        }
        return itemdamage1 == itemdamage2;
    }


    public static boolean isBestArmor(ContainerChest c, ItemStack item) {
        float itempro1 = ((ItemArmor) item.getItem()).damageReduceAmount;
        float itempro2 = 0f;
        if (isContain(itemHelmet, Item.getIdFromItem(item.getItem()))) {
            for (int i = 0; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isContain(itemHelmet,
                        Item.getIdFromItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()))) {
                    float temppro = ((ItemArmor) mc.thePlayer.inventoryContainer.getSlot(i).getStack()
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
            for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) != null && isContain(itemHelmet,
                        Item.getIdFromItem(c.getLowerChestInventory().getStackInSlot(i).getItem()))) {
                    float temppro = ((ItemArmor) c.getLowerChestInventory().getStackInSlot(i)
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
        }

        if (isContain(itemChestPlate, Item.getIdFromItem(item.getItem()))) { // �ؼ�
            for (int i = 0; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isContain(itemChestPlate,
                        Item.getIdFromItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()))) {
                    float temppro = ((ItemArmor) mc.thePlayer.inventoryContainer.getSlot(i).getStack()
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
            for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) != null && isContain(itemChestPlate,
                        Item.getIdFromItem(c.getLowerChestInventory().getStackInSlot(i).getItem()))) {
                    float temppro = ((ItemArmor) c.getLowerChestInventory().getStackInSlot(i)
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
        }

        if (isContain(itemLeggings, Item.getIdFromItem(item.getItem()))) { // ����
            for (int i = 0; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isContain(itemLeggings,
                        Item.getIdFromItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()))) {
                    float temppro = ((ItemArmor) mc.thePlayer.inventoryContainer.getSlot(i).getStack()
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
            for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) != null && isContain(itemLeggings,
                        Item.getIdFromItem(c.getLowerChestInventory().getStackInSlot(i).getItem()))) {
                    float temppro = ((ItemArmor) c.getLowerChestInventory().getStackInSlot(i)
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
        }

        if (isContain(itemBoots, Item.getIdFromItem(item.getItem()))) { // Ь��
            for (int i = 0; i < 45; ++i) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isContain(itemBoots,
                        Item.getIdFromItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()))) {
                    float temppro = ((ItemArmor) mc.thePlayer.inventoryContainer.getSlot(i).getStack()
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
            for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
                if (c.getLowerChestInventory().getStackInSlot(i) != null && isContain(itemBoots,
                        Item.getIdFromItem(c.getLowerChestInventory().getStackInSlot(i).getItem()))) {
                    float temppro = ((ItemArmor) c.getLowerChestInventory().getStackInSlot(i)
                            .getItem()).damageReduceAmount;
                    if (temppro > itempro2)
                        itempro2 = temppro;
                }
            }
        }

        return itempro1 == itempro2;
    }

    public static boolean isContain(int[] arr, int targetValue) {
        return ArrayUtils.contains(arr, targetValue);
    }

    public static boolean isPotionNegative(ItemStack itemStack) {
        ItemPotion potion = (ItemPotion) itemStack.getItem();

        List<PotionEffect> potionEffectList = potion.getEffects(itemStack);

        return potionEffectList.stream().map(potionEffect -> Potion.potionTypes[potionEffect.getPotionID()])
                .anyMatch(Potion::isBadEffect);
    }

    public static int getEnchantment(final ItemStack itemStack, final Enchantment enchantment) {
        if (itemStack == null || itemStack.getEnchantmentTagList() == null || itemStack.getEnchantmentTagList().hasNoTags()) {
            return 0;
        }
        for (int i = 0; i < itemStack.getEnchantmentTagList().tagCount(); ++i) {
            final NBTTagCompound tagCompound = itemStack.getEnchantmentTagList().getCompoundTagAt(i);
            if (tagCompound.getShort("ench") == enchantment.effectId || tagCompound.getShort("id") == enchantment.effectId) {
                return tagCompound.getShort("lvl");
            }
        }
        return 0;
    }
}
