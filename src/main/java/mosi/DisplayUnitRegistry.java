package mosi;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

import mosi.display.DisplayUnitFactory;
import mosi.display.units.DisplayUnit;
import mosi.utilities.FileUtilities;
import mosi.utilities.GsonHelper;

import com.google.gson.Gson;
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
        Gson gson = GsonHelper.createGson(true, true, new Class[] { DisplayRegistrySaveObject.class },
                new Object[] { new DisplayRegistrySaveObject.Serializer(displayFactory) });
        File displayListFile = DisplayUnitRegistry.getFile(configDirectory);
        DisplayRegistrySaveObject displayResult = GsonHelper.readOrCreateFromGson(
                FileUtilities.createReader(displayListFile, false), DisplayRegistrySaveObject.class, gson);
        if (displayResult.getDisplays().isPresent()) {
            displays = new ArrayList<DisplayUnit>(displayResult.getDisplays().get());
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
        Gson gson = GsonHelper.createGson(true, true, new Class[] { DisplayRegistrySaveObject.class },
                new Object[] { new DisplayRegistrySaveObject.Serializer(displayFactory) });
        File displayListFile = DisplayUnitRegistry.getFile(configDirectory);
        GsonHelper.writeToGson(FileUtilities.createWriter(displayListFile, true), new DisplayRegistrySaveObject(
                currentDisplays()), gson);
    }

    public static File getFile(File configDirectory) {
        return new File(configDirectory, DefaultProps.MOD_DIR + "DisplaySettings.cfg");
    }
}
