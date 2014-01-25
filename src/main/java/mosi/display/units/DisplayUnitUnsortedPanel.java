package mosi.display.units;

import java.util.ArrayList;
import java.util.List;

import mosi.MOSI;
import mosi.display.DisplayUnitFactory;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;

import com.google.gson.JsonObject;

public class DisplayUnitUnsortedPanel extends DisplayUnitPanel {
    public static final String DISPLAY_ID = "DisplayUnitUnsortedPanel";
    private List<DisplayUnit> childDisplays;

    public DisplayUnitUnsortedPanel() {
        super();
        childDisplays = new ArrayList<DisplayUnit>();
        // MOSI.getDisplayFactory().createDisplay(type, jsonObject)
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
    public void update(Minecraft mc, int ticks) {
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
