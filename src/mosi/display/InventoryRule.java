package mosi.display;

import net.minecraft.item.ItemStack;

/**
 * Rule for determining/filtering if a particular item in an inventory is the one desired
 */
public interface InventoryRule {
    public boolean isMatch(ItemStack itemStack, int slotId, boolean armorSlot, boolean currentItem);
//    public boolean allowMultipleMatches();

    public static class ItemIdMatch implements InventoryRule {
        public final int minItemId;
        public final int maxItemId;

        public ItemIdMatch(int minItemId, int maxItemId) {
            this.minItemId = minItemId;
            this.maxItemId = maxItemId;
        }

        @Override
        public boolean isMatch(ItemStack itemStack, int slotId, boolean armorSlot, boolean currentItem) {
            if (itemStack == null) {
                return false;
            }
            if (minItemId <= maxItemId) {
                return (itemStack.itemID <= maxItemId && itemStack.itemID >= minItemId);
            } else {
                return !(itemStack.itemID < minItemId && itemStack.itemID > maxItemId);
            }
        }
    }

    public static class ItemMetaMatch extends ItemIdMatch {
        public final int minItemDamage;
        public final int maxItemDamage;

        public ItemMetaMatch(int minItemId, int maxItemId, int itemDamage) {
            this(minItemId, maxItemId, itemDamage, itemDamage);
        }

        public ItemMetaMatch(int minItemId, int maxItemId, int minItemDamage, int maxItemDamage) {
            super(minItemId, maxItemId);
            this.minItemDamage = minItemDamage;
            this.maxItemDamage = maxItemDamage;
        }

        @Override
        public boolean isMatch(ItemStack itemStack, int slotId, boolean armorSlot, boolean currentItem) {
            if (super.isMatch(itemStack, slotId, armorSlot, currentItem)) {
                if (minItemDamage <= maxItemDamage) {
                    return (itemStack.getItemDamage() <= maxItemDamage && itemStack.getItemDamage() >= minItemDamage);
                } else {
                    return !(itemStack.getItemDamage() < minItemDamage && itemStack.getItemDamage() > maxItemDamage);
                }
            }
            return false;
        }
    }

    public static class ItemSlotMatch implements InventoryRule {
        public final int slotId;
        public final boolean armorSlot;

        public ItemSlotMatch(int slotId, boolean armorSlot) {
            this.slotId = slotId;
            this.armorSlot = armorSlot;
        }

        @Override
        public boolean isMatch(ItemStack itemStack, int slotId, boolean armorSlot, boolean currentItem) {
            return this.armorSlot == armorSlot && this.slotId == slotId;
        }
    }
}
