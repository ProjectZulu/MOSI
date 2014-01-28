package mosi;

import java.util.ArrayList;

import mosi.display.DisplayUnitFactory;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnitItem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class DisplayUnitRegistry {

    DisplayUnitFactory displayFactory;
    private ImmutableList<DisplayUnit> displays = ImmutableList.<DisplayUnit> of();

    public ImmutableList<DisplayUnit> currentDisplays() {
        return displays;
    }

    private ChangeManager displayChanger = new ChangeManager();

    public DisplayChanger getDisplayChanger() {
        return displayChanger;
    }

    /** Public interface to allow external components to cause addition/removal */
    public static interface DisplayChanger {
        public abstract void addToRemoveList(DisplayUnit display);

        public abstract void addToAddList(DisplayUnit display);
    }

    /**
     * Responsible for adding/removing display in a non-conflicting manner
     */
    private static class ChangeManager implements DisplayChanger {
        public ArrayList<DisplayUnit> addList;
        public ArrayList<DisplayUnit> removeList;

        private ChangeManager() {
            addList = new ArrayList<DisplayUnit>();
            removeList = new ArrayList<DisplayUnit>();
        }

        @Override
        public void addToAddList(DisplayUnit display) {
            addList.add(display);
        }

        @Override
        public void addToRemoveList(DisplayUnit display) {
            removeList.add(display);
        }
    }

    public DisplayUnitRegistry(DisplayUnitFactory displayFactory) {
        this.displayFactory = displayFactory;
        loadFromConfig();
    }

    public void loadFromConfig() {
        Builder<DisplayUnit> builder = ImmutableList.<DisplayUnit> builder();
        builder.add(new DisplayUnitItem());
        // builder.add(new DisplayUnitPotion());
        // builder.add(new DisplayUnitUnsortedPanel());
        // builder.add(new DisplayUnitSortedPanel());
        displays = builder.build();

        // Load implicitly saves changes due to errors/corrections appear i.e. a number that cannot be below zero is set
        // to zero and should be set as such in the config
        saveToConfig();
    }

    public void saveToConfig() {

    }
}
