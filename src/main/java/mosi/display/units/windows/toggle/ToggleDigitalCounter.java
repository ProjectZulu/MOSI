package mosi.display.units.windows.toggle;

import mosi.display.units.DisplayUnitCountable;
import mosi.display.units.windows.DisplayUnitToggle.Toggle;

public class ToggleDigitalCounter implements Toggle {
    private DisplayUnitCountable countable;

    public ToggleDigitalCounter(DisplayUnitCountable countable) {
        this.countable = countable;
    }

    @Override
    public void toggle() {
        countable.enableDigitalCounter(!countable.isDigitalEnabled());
    }

    @Override
    public boolean isToggled() {
        return countable.isDigitalEnabled();
    }
}
