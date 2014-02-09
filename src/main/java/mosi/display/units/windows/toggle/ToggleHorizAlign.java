package mosi.display.units.windows.toggle;

import mosi.display.units.DisplayUnit.HorizontalAlignment;
import mosi.display.units.DisplayUnitSettable;
import mosi.display.units.windows.DisplayUnitToggle.Toggle;
import mosi.utilities.Coord;

public class ToggleHorizAlign implements Toggle {
    private DisplayUnitSettable displayToSet;
    private HorizontalAlignment alignmentToSet;

    public ToggleHorizAlign(DisplayUnitSettable displayToSet, HorizontalAlignment alignment) {
        this.displayToSet = displayToSet;
        this.alignmentToSet = alignment;
    }

    @Override
    public void toggle() {
        displayToSet.setHorizontalAlignment(alignmentToSet);
        // Reset position to prevent display from becoming lost outside screen
        displayToSet.setOffset(new Coord(0, 0));
    }

    @Override
    public boolean isToggled() {
        return displayToSet.getHorizontalAlignment() == alignmentToSet;
    }
}
