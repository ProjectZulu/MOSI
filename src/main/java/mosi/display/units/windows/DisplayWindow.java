package mosi.display.units.windows;

import java.util.ArrayList;
import java.util.Iterator;

import mosi.display.DisplayHelper;
import mosi.display.DisplayUnitFactory;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnit.ActionResult.SimpleAction;
import mosi.display.units.DisplayUnitMoveable;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;

import com.google.gson.JsonObject;

/**
 * Tree structure for propagating interactive events through nested DisplayUnits that wraps valid DisplayUnits
 * 
 * Not actually strictly speaking a DisplayUnit and should not be added to DisplayTicker/DisplayUnitRegistry
 * 
 * Implicitly assumes Children are 'above' the base display and are a priority for input events
 */
public abstract class DisplayWindow extends DisplayUnitMoveable {
    public static final String DISPLAY_ID = "DisplayUnitWindow";

    private ArrayList<DisplayUnit> children;

    public DisplayWindow() {
        super(new Coord(0, 0));
        this.children = new ArrayList<DisplayUnit>();
    }

    public DisplayWindow(Coord coord) {
        super(coord);
        this.children = new ArrayList<DisplayUnit>();
    }

    public boolean addWindow(DisplayUnit window) {
        return children.add(window);
    }

    public boolean removeWindow(DisplayUnit window) {
        return children.remove(window);
    }

    protected void clearWindows() {
        children.clear();
    }

    @Override
    public final String getType() {
        StringBuilder sb = new StringBuilder();
        sb.append(DISPLAY_ID).append(":").append(getSubType()).append("[");
        Iterator<DisplayUnit> iterator = children.iterator();
        while (iterator.hasNext()) {
            DisplayUnit window = iterator.next();
            sb.append(window.getType());
            if (iterator.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("]");
        return DISPLAY_ID.concat(":").concat(getSubType()).concat("[");
    }

    public abstract String getSubType();

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
    public final void renderDisplay(Minecraft mc, Coord position) {
        for (DisplayUnit window : children) {
            window.renderDisplay(mc, DisplayHelper.determineScreenPositionFromDisplay(mc, position, getSize(), window));
            // window.renderDisplay(mc, DisplayHelper.determineScreenPositionFromDisplay(mc, window));
        }
        renderSubDisplay(mc, position);
    }

    public abstract void renderSubDisplay(Minecraft mc, Coord Position);

    @Override
    public JsonObject saveCustomData(JsonObject jsonObject) {
        throw new UnsupportedOperationException("DisplayWindows do not have memory");
    }

    @Override
    public void loadCustomData(DisplayUnitFactory factory, JsonObject customData) {
        throw new UnsupportedOperationException("DisplayWindows do not have memory");
    }

    @Override
    public SimpleAction mousePosition(Coord localMouse) {
        for (DisplayUnit window : children) {
            SimpleAction action = window.mousePosition(DisplayHelper.localizeMouseCoords(Minecraft.getMinecraft(),
                    localMouse, this, window));
            if (action.stopActing) {
                return action;
            }
        }
        return new SimpleAction(false);
    }

    @Override
    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
        for (DisplayUnit window : children) {
            if (processActionResult(window.mouseAction(
                    DisplayHelper.localizeMouseCoords(Minecraft.getMinecraft(), localMouse, this, window), action,
                    actionData), window)) {
                return new ActionResult(true);
            }
        }
        return super.mouseAction(localMouse, action, actionData);
    }

    /**
     * @return StopProcessing - true if processing should be stopped
     */
    private boolean processActionResult(ActionResult action, DisplayUnit provider) {
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
        return super.keyTyped(eventCharacter, eventKey);
    }

    public void saveWindow() {

    }

    public void closeWindow() {

    }
}
