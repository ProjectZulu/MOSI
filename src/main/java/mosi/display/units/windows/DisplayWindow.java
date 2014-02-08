package mosi.display.units.windows;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;

import mosi.display.DisplayHelper;
import mosi.display.DisplayUnitFactory;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnitInventoryRule;
import mosi.display.units.DisplayUnit.ActionResult;
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

    /**
     * Windows are child displays that, while depending on the parent display fulfill some isolated role such as a
     * pop-up to select an item. Windows should be able to be closed without closing the parent.
     */
    private ArrayList<DisplayUnit> windows;
    /**
     * Elements are part of the children of the current window, such as a text field and buttons that should exist as
     * long as the parent exists
     */
    protected final ArrayList<DisplayUnit> elements;

    // Temporary list of displays that need to be moved higher in the display list (higher displays get events sooner)
    private final Queue<DisplayUnit> priority;

    public DisplayWindow() {
        this(new Coord(0, 0));
    }

    public DisplayWindow(Coord coord) {
        super(coord);
        this.windows = new ArrayList<DisplayUnit>();
        this.priority = new ArrayDeque<DisplayUnit>();
        this.elements = new ArrayList<DisplayUnit>();
    }

    public final void addWindow(DisplayUnit window) {
        windows.add(0, window);
    }

    public final boolean removeWindow(DisplayUnit window) {
        return windows.remove(window);
    }

    protected final void clearWindows() {
        windows.clear();
    }

    public boolean addElement(DisplayUnit element) {
        return elements.add(element);
    }
    
    public boolean removeElement(DisplayUnit element) {
        return elements.remove(element);
    }

    @Override
    public final String getType() {
        StringBuilder sb = new StringBuilder();
        sb.append(DISPLAY_ID).append(":").append(getSubType()).append("[");
        Iterator<DisplayUnit> iterator = windows.iterator();
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
        while (!priority.isEmpty()) {
            DisplayUnit display = priority.poll();
            windows.remove(display);
            windows.add(0, display);
        }

        for (DisplayUnit window : windows) {
            window.onUpdate(mc, ticks);
        }

        for (DisplayUnit element : elements) {
            element.onUpdate(mc, ticks);
        }
    }

    @Override
    public boolean shouldRender(Minecraft mc) {
        return true;
    }

    @Override
    public final void renderDisplay(Minecraft mc, Coord position) {
        renderSubDisplay(mc, position);
        
        for (int i = elements.size() - 1; i >= 0; i--) {
            DisplayUnit element = elements.get(i);
            element.renderDisplay(mc,
                    DisplayHelper.determineScreenPositionFromDisplay(mc, position, getSize(), element));
        }
        
        /**
         * Reverse iteration we are doing back to front rendering and top of list is considered 'front' i.e. given
         * priority for clicks
         */
        for (int i = windows.size() - 1; i >= 0; i--) {
            DisplayUnit window = windows.get(i);
            window.renderDisplay(mc, DisplayHelper.determineScreenPositionFromDisplay(mc, position, getSize(), window));
        }
    }

    public abstract void renderSubDisplay(Minecraft mc, Coord position);

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
        for (DisplayUnit window : windows) {
            SimpleAction action = window.mousePosition(DisplayHelper.localizeMouseCoords(Minecraft.getMinecraft(),
                    localMouse, this, window));
            if (action.stopActing) {
                return action;
            }
        }

        for (DisplayUnit element : elements) {
            SimpleAction action = element.mousePosition(DisplayHelper.localizeMouseCoords(Minecraft.getMinecraft(),
                    localMouse, this, element));
            if (action.stopActing) {
                return action;
            }
        }

        return ActionResult.NOACTION;
    }

    @Override
    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
        for (DisplayUnit window : windows) {
            if (processWindowActionResult(window.mouseAction(
                    DisplayHelper.localizeMouseCoords(Minecraft.getMinecraft(), localMouse, this, window), action,
                    actionData), window)) {
                return ActionResult.SIMPLEACTION;
            }
        }
        for (DisplayUnit element : elements) {
            if (processElementActionResult(element.mouseAction(
                    DisplayHelper.localizeMouseCoords(Minecraft.getMinecraft(), localMouse, this, element), action,
                    actionData), element)) {
                return ActionResult.SIMPLEACTION;
            }
        }
        return super.mouseAction(localMouse, action, actionData);
    }

    /**
     * @return StopProcessing - true if processing should be stopped
     */
    private boolean processWindowActionResult(ActionResult action, DisplayUnit provider) {
        if (provider != null && provider instanceof DisplayUnitInventoryRule) {
            boolean blah = true;
        }
        switch (action.interaction) {
        case CLOSE:
            if (action.display.isPresent()) {
                windows.remove(action.display);
            }
            break;
        case REPLACE:
            if (action.display.isPresent()) {
                windows.remove(provider);
                windows.add(action.display.get());
            }
            break;
        case REPLACE_ALL:
            windows.clear();
            if (action.display.isPresent()) {
                windows.add(action.display.get());
            }
            break;
        case OPEN:
            if (action.display.isPresent()) {
                windows.add(action.display.get());
            }
            break;
        case NONE:
            if (action.stopActing) {
                // Some interaction occurred in that display, elevate it to receive events sooner
                priority.add(provider);
            }
            break;
        }
        return action.stopActing;
    }

    private boolean processElementActionResult(ActionResult action, DisplayUnit provider) {
        if (provider != null && provider instanceof DisplayUnitInventoryRule) {
            boolean blah = true;
        }
        switch (action.interaction) {
        case CLOSE:
            if (action.display.isPresent()) {
                windows.remove(action.display);
            }
            break;
        case REPLACE:
            throw new IllegalArgumentException("Display ELEMENTS do not support 'REPLACE' Interaction");
        case REPLACE_ALL:
            windows.clear();
            if (action.display.isPresent()) {
                addWindow(action.display.get());
            }
            break;
        case OPEN:
            if (action.display.isPresent()) {
                addWindow(action.display.get());
            }
            break;
        case NONE:
            break;
        }
        return action.stopActing;
    }

    @Override
    public ActionResult keyTyped(char eventCharacter, int eventKey) {
        for (DisplayUnit window : windows) {
            if (processWindowActionResult(window.keyTyped(eventCharacter, eventKey), window)) {
                return ActionResult.SIMPLEACTION;
            }
        }
        for (DisplayUnit element : elements) {
            if (processElementActionResult(element.keyTyped(eventCharacter, eventKey), element)) {
                return ActionResult.SIMPLEACTION;
            }
        }
        return super.keyTyped(eventCharacter, eventKey);
    }

    public void saveWindow() {

    }

    public void closeWindow() {

    }
}
