package mosi;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

import mosi.display.DisplayUnitFactory;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnitItem;
import mosi.utilities.FileUtilities;
import mosi.utilities.FileUtilities.OptionalCloseable;
import mosi.utilities.GsonHelper;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class DisplayUnitRegistry {

    DisplayUnitFactory displayFactory;
    private ArrayList<DisplayUnit> displays = new ArrayList<DisplayUnit>();

    public ArrayList<DisplayUnit> currentDisplays() {
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

    public DisplayUnitRegistry(DisplayUnitFactory displayFactory, File configDirectory) {
        this.displayFactory = displayFactory;
        loadFromConfig(configDirectory);
    }

    public void loadFromConfig(File configDirectory) {
        Type type = new TypeToken<ArrayList<DisplayUnit>>() {
        }.getType();
        Gson gson = GsonHelper.createGson(true, true, new Type[] { type }, new Object[] { new DisplayListSerializer(
                displayFactory) });
        File displayListFile = DisplayListSerializer.getFile(configDirectory);
        Optional<ArrayList<DisplayUnit>> displayResult = GsonHelper.<ArrayList<DisplayUnit>> readFromGson(
                FileUtilities.createReader(displayListFile, false), type, gson);
        if (displayResult.isPresent()) {
            displays = displayResult.get();
        } else {
            // TODO: Create array of default displays
        }
        // Builder<DisplayUnit> builder = ImmutableList.<DisplayUnit> builder();
        // builder.add(new DisplayUnitItem());
        // builder.add(new DisplayUnitPotion());
        // builder.add(new DisplayUnitPotion());
        // displays.add(new DisplayUnitUnsortedPanel());
        // builder.add(new DisplayUnitSortedPanel());

        // Load implicitly saves changes due to errors/corrections appear i.e. a number that cannot be below zero is set
        // to zero and should be set as such in the config
        saveToConfig(configDirectory);
    }

    public void saveToConfig(File configDirectory) {
        Type type = new TypeToken<ArrayList<DisplayUnit>>() {
        }.getType();
        Gson gson = GsonHelper.createGson(true, true, new Type[] { type }, new Object[] { new DisplayListSerializer(
                displayFactory) });
        File displayListFile = DisplayListSerializer.getFile(configDirectory);
        GsonHelper.writeToGson(FileUtilities.createWriter(displayListFile, true), currentDisplays(), type, gson);
    }
}
