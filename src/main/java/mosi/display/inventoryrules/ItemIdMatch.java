package mosi.display.inventoryrules;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemIdMatch implements InventoryRule {
    public String itemId;
    public boolean multipleMatches;

    public ItemIdMatch(String itemId, boolean multipleMatches) {
        this.itemId = itemId;
        this.multipleMatches = multipleMatches;
    }

    @Override
    public boolean isMatch(ItemStack itemStack, int slotId, boolean armorSlot, boolean currentItem) {
        if (itemStack == null) {
            return false;
        }
        return itemStack.getItem().equals(Item.field_150901_e.getObject(itemId));
    }

    @Override
    public boolean allowMultipleMatches() {
        return multipleMatches;
    }
}
