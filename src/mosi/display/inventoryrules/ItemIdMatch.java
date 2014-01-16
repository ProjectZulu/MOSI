package mosi.display.inventoryrules;

import net.minecraft.item.ItemStack;

public class ItemIdMatch implements InventoryRule {
    public final int minItemId;
    public final int maxItemId;
    public final boolean multipleMatches;

    public ItemIdMatch(int minItemId, int maxItemId, boolean multipleMatches) {
        this.minItemId = minItemId;
        this.maxItemId = maxItemId;
        this.multipleMatches = multipleMatches;
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

    @Override
    public boolean allowMultipleMatches() {
        return multipleMatches;
    }
}
