package mosi.display.inventoryrules;

import java.util.ArrayList;
import java.util.Collection;

import mosi.display.units.DisplayUnitInventoryRule;
import mosi.display.units.windows.DisplayWindowScrollList.Scrollable;
import mosi.display.units.windows.DisplayWindowScrollList.ScrollableElement;

public class ScrollableInventoryRules implements Scrollable {

    private InventoryRules rules;
    ArrayList<ScrollableElement> scrollableList;

    public ScrollableInventoryRules(InventoryRules rules) {
        this.rules = rules;
        scrollableList = new ArrayList<ScrollableElement>();
        // TODO: This is beyond ugly, something should be done... eventually
        for (InventoryRule inventoryRule : rules) {
            if (inventoryRule instanceof ItemHandMatch) {
                scrollableList.add(new DisplayUnitInventoryRule((ItemHandMatch) inventoryRule, this));
            } else if (inventoryRule instanceof ItemIdMatch) {
                scrollableList.add(new DisplayUnitInventoryRule((ItemIdMatch) inventoryRule, this));
            } else if (inventoryRule instanceof ItemMetaMatch) {
                scrollableList.add(new DisplayUnitInventoryRule((ItemMetaMatch) inventoryRule, this));
            } else if (inventoryRule instanceof ItemSlotMatch) {
                scrollableList.add(new DisplayUnitInventoryRule((ItemSlotMatch) inventoryRule, this));
            } else {
                throw new IllegalArgumentException("Unknown InventoryRule type " + inventoryRule);
            }
        }
    }

    @Override
    public Collection<? extends ScrollableElement> getElements() {
        return scrollableList;
    }

    @Override
    public boolean removeElement(ScrollableElement element) {
        int index = scrollableList.indexOf(element);
        rules.removeRule(index);
        return scrollableList.remove(element);
    }
}
