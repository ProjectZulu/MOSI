package mosi.display;

import java.util.ArrayList;
import java.util.List;

import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;

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
        // Sorted by order added to list
        NATURAL,
        // Sort by trackedCount
        COUNT;
    }

    public DisplayUnitSortedPanel() {
        super();
        sortMode = SortMode.NATURAL;
        childDisplays = new ArrayList<DisplayUnitCountable>();
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
