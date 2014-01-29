package mosi.display.units;

import mosi.DisplayUnitRegistry.DisplayChanger;
import mosi.display.units.windows.DisplayWindow;

public class DisplayUnitCreator extends DisplayWindow {
    private DisplayChanger displayChanger;

    public DisplayUnitCreator(DisplayUnit baseDisplay, DisplayChanger displayChanger) {
        super(baseDisplay);
        this.displayChanger = displayChanger;
    }
}