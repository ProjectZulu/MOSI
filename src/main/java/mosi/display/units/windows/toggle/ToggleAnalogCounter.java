package mosi.display.units.windows.toggle;

import mosi.display.units.DisplayUnitCountable;
import mosi.display.units.windows.DisplayUnitToggle.Toggle;

public class ToggleAnalogCounter implements Toggle {
    private DisplayUnitCountable countable;

    public ToggleAnalogCounter(DisplayUnitCountable countable) {
        this.countable = countable;
    }

    @Override
    public void toggle() {
        countable.enableAnalogDisplay(!countable.isAnalogEnabled());
    }

    @Override
    public boolean isToggled() {
        return countable.isAnalogEnabled();
    }
}
