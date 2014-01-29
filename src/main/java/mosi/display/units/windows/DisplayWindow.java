package mosi.display.units.windows;

import java.util.ArrayList;
import java.util.Iterator;

import mosi.display.DisplayUnitFactory;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnit.ActionResult;
import mosi.display.units.DisplayUnit.HorizontalAlignment;
import mosi.display.units.DisplayUnit.MouseAction;
import mosi.display.units.DisplayUnit.VerticalAlignment;
import mosi.display.units.DisplayUnit.ActionResult.INTERACTION;
import mosi.display.units.DisplayUnit.ActionResult.NoAction;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;

import com.google.common.base.Optional;
import com.google.gson.JsonObject;

/**
 * Tree structure for propagating interactive events through nested DisplayUnits that wraps valid DisplayUnits
 * 
 * Not actually strictly speaking a DisplayUnit and should not be added to DisplayTicker/DisplayUnitRegistry
 * 
 * Implicitly assumes Children are 'above' the base display and are a priority for input events
 */
public class DisplayWindow implements DisplayUnit {
    public static final String DISPLAY_ID = "DisplayUnitWindow";

    private DisplayUnit baseDisplay;

    public DisplayUnit getBaseDisplay() {
        return baseDisplay;
    }

    // Child Windows that depend on this window for existence
    private ArrayList<DisplayWindow> children;

    public DisplayWindow(DisplayUnit baseDisplay) {
        if (baseDisplay == null) {
            throw new IllegalArgumentException("Display cannot be null");
        }
        this.baseDisplay = baseDisplay;
        this.children = new ArrayList<DisplayWindow>();
    }

    public boolean addWindow(DisplayWindow window) {
        return children.add(window);
    }

    public boolean removeWindow(DisplayWindow window) {
        return children.remove(window);
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
        // Does DisplayWindow do any rendering?
        return true;
    }

    @Override
    public void renderDisplay(Minecraft mc, Coord Position) {
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
    public void mousePosition(Coord localMouse) {
        baseDisplay.mousePosition(localMouse);
        for (DisplayWindow window : children) {
            window.mousePosition(localMouse);
        }
    }

    public static class WindowActionResult extends ActionResult {
        public final Optional<DisplayWindow> display;

        public WindowActionResult(boolean stopActing, INTERACTION interaction, DisplayWindow display) {
            super(stopActing, interaction, null);
            this.display = Optional.of(display);
        }
    }

    @Override
    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
        for (DisplayWindow window : children) {
            if (processActionResult(window.mouseAction(localMouse, action, actionData), window)) {
                break;
            }
        }
        return baseDisplay.mouseAction(localMouse, action, actionData);
    }

    /**
     * @return StopProcessing - true if processing should be stopped
     */
    private boolean processActionResult(ActionResult action, DisplayWindow provider) {
        switch (action.interaction) {
        case CLOSE:
            if (action.display.isPresent()) {
                children.remove(action.display);
            }
            break;
        case REPLACE:
            if (action.display.isPresent()) {
                children.remove(provider);
                children.add(action.display.get());
            }
            break;
        case REPLACE_ALL:
            children.clear();
            if (action.display.isPresent()) {
                children.add(action.display.get());
            }
            break;
        case OPEN:
            if (action.display.isPresent()) {
                children.add(action.display.get());
            }
            break;
        case NONE:
            break;
        }
        return action.stopActing;
    }

    @Override
    public ActionResult keyTyped(char eventCharacter, int eventKey) {
        return new NoAction();
    }

    public void saveWindow() {

    }

    public void closeWindow() {

    }
}
