package mosi.display.units;

import java.util.ArrayList;
import java.util.Iterator;

import mosi.display.DisplayUnitFactory;
import mosi.display.units.DisplayUnit.ActionResult.NoAction;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;

import com.google.common.base.Optional;
import com.google.gson.JsonObject;

/**
 * Tree structure for propagating interactive events through nested DisplayUnits that wraps valid DisplayUnits
 * 
 * Not actually strictly speaking a DisplayUnit and should not be added to DisplayTicker/DisplayUnitRegistry
 */
public class DisplayWindow implements DisplayUnit {
    public static final String DISPLAY_ID = "DisplayUnitWindow";

    private DisplayUnit baseDisplay;
    // Child Windows that depend on this window for existence
    private ArrayList<DisplayWindow> children;
    // Temporary window that is closed automatically closed if anything else is interacted with, i.e. Right-Click menu
    private DisplayWindow tempWindow;

    public DisplayWindow(DisplayUnit baseDisplay) {
        this.baseDisplay = baseDisplay;
        this.children = new ArrayList<DisplayWindow>();
    }

    public boolean addWindow(DisplayWindow window) {
        tempWindow = null;
        return children.add(window);
    }

    public boolean removeWindow(DisplayWindow window) {
        tempWindow = null;
        return children.remove(window);
    }

    public void setTempWindow(DisplayWindow window) {
        tempWindow = window;
    }

    @Override
    public String getType() {
        StringBuilder sb = new StringBuilder();
        sb.append(DISPLAY_ID).append(":").append(baseDisplay.getType()).append("[");
        Iterator<DisplayWindow> iterator = children.iterator();
        while (iterator.hasNext()) {
            DisplayWindow window = iterator.next();
            sb.append(window.getType());
            if (iterator.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("]");
        return DISPLAY_ID.concat(":").concat(baseDisplay.getType()).concat("[");
    }

    @Override
    public Coord getOffset() {
        return baseDisplay.getOffset();
    }

    @Override
    public Coord getSize() {
        return baseDisplay.getSize();
    }

    @Override
    public VerticalAlignment getVerticalAlignment() {
        return baseDisplay.getVerticalAlignment();
    }

    @Override
    public HorizontalAlignment getHorizontalAlignment() {
        return baseDisplay.getHorizontalAlignment();
    }

    @Override
    public void onUpdate(Minecraft mc, int ticks) {
        throw new UnsupportedOperationException("DisplayWindows should not be updating logic");
    }

    @Override
    public boolean shouldRender(Minecraft mc) {
        return true;
    }

    @Override
    public void renderDisplay(Minecraft mc, Coord Position) {
        if (tempWindow != null) {
            tempWindow.renderDisplay(mc, Position);
        }
        for (DisplayWindow window : children) {
            window.renderDisplay(mc, Position);
        }
        baseDisplay.renderDisplay(mc, Position);
    }

    @Override
    public JsonObject saveCustomData(JsonObject jsonObject) {
        throw new UnsupportedOperationException("DisplayWindows do not have memory");
    }

    @Override
    public void loadCustomData(DisplayUnitFactory factory, JsonObject customData) {
        throw new UnsupportedOperationException("DisplayWindows do not have memory");
    }

    @Override
    public void mouseMove(int mouseLocalX, int mouseLocalY) {
    }

    @Override
    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
        return new NoAction();
    }

    @Override
    public ActionResult keyTyped(char eventCharacter, int eventKey) {
        return new NoAction();
    }
}
