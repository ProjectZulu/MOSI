package mosi.display.inventoryrules;

import java.util.ArrayList;
import java.util.Collection;

import mosi.display.units.DisplayUnitInventoryRule;
import mosi.display.units.windows.DisplayWindowScrollList;
import mosi.display.units.windows.DisplayWindowScrollList.Scrollable;
import mosi.display.units.windows.DisplayWindowScrollList.ScrollableElement;

import com.google.common.base.Optional;

public class ScrollableInventoryRules implements Scrollable<InventoryRule> {

    private InventoryRules rules;
    ArrayList<ScrollableElement<InventoryRule>> scrollableList;
    private Optional<ScrollableElement<InventoryRule>> selectedEntry = Optional.absent();

    public ScrollableInventoryRules(InventoryRules rules) {
        this.rules = rules;
        scrollableList = new ArrayList<ScrollableElement<InventoryRule>>();
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
    public Collection<? extends ScrollableElement<InventoryRule>> getElements() {
        return scrollableList;
    }

    @Override
    public boolean removeElement(ScrollableElement<InventoryRule> element) {
        rules.remove(element.getSource());
        return scrollableList.remove(element);
    }

    @Override
    public boolean addElement(ScrollableElement<InventoryRule> element) {
        rules.add(element.getSource());
        return scrollableList.add(element);
    }

    @Override
    public void setSelected(ScrollableElement<InventoryRule> element) {
        selectedEntry = element != null ? Optional.of(element) : Optional.<ScrollableElement<InventoryRule>> absent();
    }

    @Override
    public Optional<ScrollableElement<InventoryRule>> getSelected() {
        return selectedEntry;
    }
}
