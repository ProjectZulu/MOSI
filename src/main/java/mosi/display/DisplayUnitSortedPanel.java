package mosi.display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;

import com.google.common.base.Optional;
import com.google.gson.JsonObject;

public class DisplayUnitSortedPanel extends DisplayUnitPanel {
    public static final String DISPLAY_ID = "DisplayUnitSortedPanel";

    private List<DisplayUnitCountable> childDisplays;

    private SortMode sortMode;

    // TODO: For SortMode COUNT an interface DisplayUnitCountable needs to be created
    // Should Sorting be seperate DisplayUnit? i.e. DisplayUnitSortedPanel, this would maintain the ability for
    // DisplayUnitPanel to contain other DisplayUnitPanel
    // Both DisplayUnitPanel and DisplayUnitSortedPanel should extend common base class
    public enum SortMode {
        // No SORT, Sorted by order added to list or last sorted value
        NATURAL,
        // Sort by trackedCount
        HIGHLOW(new Comparator<DisplayUnitCountable>() {

            @Override
            public int compare(DisplayUnitCountable duc1, DisplayUnitCountable duc2) {
                return duc1.getCount() < duc2.getCount() ? +1 : duc1.getCount() > duc2.getCount() ? -1 : 0;
            }
        }), LOWHIGH(new Comparator<DisplayUnitCountable>() {

            @Override
            public int compare(DisplayUnitCountable duc1, DisplayUnitCountable duc2) {
                return duc1.getCount() > duc2.getCount() ? +1 : duc1.getCount() < duc2.getCount() ? -1 : 0;
            }
        });
        public final Optional<Comparator<DisplayUnitCountable>> sorter;

        SortMode() {
            this.sorter = Optional.absent();
        }

        SortMode(Comparator<DisplayUnitCountable> sorter) {
            this.sorter = Optional.of(sorter);
        }
    }

    public DisplayUnitSortedPanel() {
        super();
        sortMode = SortMode.LOWHIGH;
        childDisplays = new ArrayList<DisplayUnitCountable>();
        childDisplays.add(new DisplayUnitPotion(20, 1));
        childDisplays.add(new DisplayUnitPotion(20, 4));
        childDisplays.add(new DisplayUnitPotion(20, 8));
        childDisplays.add(new DisplayUnitPotion(20, 11));
        childDisplays.add(new DisplayUnitItem());
        childDisplays.add(new DisplayUnitPotion(20, 14));
    }

    @Override
    public String getType() {
        return DISPLAY_ID;
    }

    @Override
    public Coord getSize() {
        return new Coord(18, 18);
    }

    @Override
    public void update(Minecraft mc, int ticks) {
        if (sortMode.sorter.isPresent()) {
            Collections.sort(childDisplays, sortMode.sorter.get());
        }

        for (DisplayUnit displayUnit : childDisplays) {
            displayUnit.onUpdate(mc, ticks);
        }
    }

    @Override
    public List<? extends DisplayUnit> getDisplaysToRender() {
        return childDisplays;
    }

    @Override
    public JsonObject saveCustomData(JsonObject jsonObject) {
        super.saveCustomData(jsonObject);
        return null;
    }

    @Override
    public void loadCustomData(DisplayUnitFactory factory, JsonObject customData) {
        super.loadCustomData(factory, customData);
    }
}
