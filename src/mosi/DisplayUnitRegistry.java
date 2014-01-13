package mosi;

import mosi.display.DisplayUnit;
import mosi.display.DisplayUnitFactory;

import com.google.common.collect.ImmutableList;

public class DisplayUnitRegistry {

    DisplayUnitFactory displayFactory;
    private ImmutableList<DisplayUnit> displays = ImmutableList.<DisplayUnit> of();

    public ImmutableList<DisplayUnit> currentDisplays() {
        return displays;
    }

    public DisplayUnitRegistry(DisplayUnitFactory displayFactory) {
        this.displayFactory = displayFactory;
        loadFromConfig();
    }

    public void loadFromConfig() {

        // Load implicitly saves changes due to errors/corrections appear i.e. a number that cannot be below zero is set
        // to zero and should be set as such in the config
        saveToConfig();
    }

    public void saveToConfig() {

    }
}
