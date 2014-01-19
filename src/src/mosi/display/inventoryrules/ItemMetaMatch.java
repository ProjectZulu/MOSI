package mosi.display.inventoryrules;

import net.minecraft.item.ItemStack;

public class ItemMetaMatch extends ItemIdMatch {
    public final int minItemDamage;
    public final int maxItemDamage;

    public ItemMetaMatch(int minItemId, int maxItemId, int itemDamage, boolean multipleMatches) {
        this(minItemId, maxItemId, itemDamage, itemDamage, multipleMatches);
    }

    public ItemMetaMatch(int minItemId, int maxItemId, int minItemDamage, int maxItemDamage, boolean multipleMatches) {
        super(minItemId, maxItemId, multipleMatches);
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
