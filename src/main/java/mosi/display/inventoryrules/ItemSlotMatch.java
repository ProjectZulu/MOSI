package mosi.display.inventoryrules;

import net.minecraft.item.ItemStack;

public class ItemSlotMatch implements InventoryRule {
    public final int slotId;
    public final boolean armorSlot;
    public final boolean multipleMatches;

    public ItemSlotMatch(int slotId, boolean armorSlot, boolean multipleMatches) {
        this.slotId = slotId;
        this.armorSlot = armorSlot;
        this.multipleMatches = multipleMatches;
    }

    @Override
    public boolean isMatch(ItemStack itemStack, int slotId, boolean armorSlot, boolean currentItem) {
        return this.armorSlot == armorSlot && this.slotId == slotId;
    }

    @Override
    public boolean allowMultipleMatches() {
        return multipleMatches;
    }
}
