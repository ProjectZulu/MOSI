package mosi.display;

import java.util.ArrayList;

import mosi.DisplayUnitRegistry;
import mosi.Log;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnit.ActionResult;
import mosi.display.units.DisplayUnit.MouseAction;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import com.google.common.collect.ImmutableList;

/**
 * Screen responsible for interaction with Displays. See displayTicker for DisplayUnit rendering
 */
public class DisplayScreen extends GuiScreen {
    private DisplayUnitRegistry displayRegistry;

    // Menu/Subscreen created by clicking/hotkey, global such to ensure only one menu/s
    private ArrayList<DisplayUnit> windows;

    // Helper for when parent minecraft field is obfuscarted
    public Minecraft getMinecraft() {
        return field_146297_k;
    }

    // Simple delegate, cause I'm apparently really lazy, probably shouldn't be called often
    public Coord calcScaledScreenSize() {
        ScaledResolution scaledResolition = new ScaledResolution(getMinecraft().gameSettings,
                getMinecraft().displayWidth, getMinecraft().displayHeight);
        return new Coord(scaledResolition.getScaledWidth(), scaledResolition.getScaledHeight());
    }

    public DisplayScreen(DisplayUnitRegistry displayRegistry) {
        super();
        this.displayRegistry = displayRegistry;
        windows = new ArrayList<DisplayUnit>();
    }

    /**
     * @param mouseRelative is the mouse position relative to resolution and screen size
     * @param renderPartialTicks How much time has elapsed since the last tick, in ticks [0.0,1.0]
     */
    @Override
    public void drawScreen(int mouseScaledX, int mouseScaledY, float renderPartialTicks) {
        super.drawScreen(mouseScaledX, mouseScaledY, renderPartialTicks);
        ImmutableList<DisplayUnit> displayList = displayRegistry.currentDisplays();
        for (DisplayUnit displayUnit : displayList) {
            Coord localMouse = DisplayHelper.localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY,
                    displayUnit);
            displayUnit.mousePosition(localMouse);
        }
        for (DisplayUnit displayUnit : windows) {
            Coord localMouse = DisplayHelper.localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY,
                    displayUnit);
            displayUnit.mousePosition(localMouse);
        }
        for (DisplayUnit displayUnit : windows) {
            Coord localMouse = DisplayHelper.localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY,
                    displayUnit);
            Coord screenPos = DisplayHelper.determineScreenPositionFromDisplay(getMinecraft(), new Coord(0, 0),
                    calcScaledScreenSize(), displayUnit);
            displayUnit.renderDisplay(getMinecraft(), screenPos);
        }
    }

    @Override
    protected void mouseClicked(int mouseScaledX, int mouseScaledY, int eventbutton) {
        super.mouseClicked(mouseScaledX, mouseScaledY, eventbutton);
        for (DisplayUnit window : windows) {
            Coord localMouse = DisplayHelper.localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY, window);
            if (processAction(window.mouseAction(localMouse, MouseAction.CLICK, eventbutton), window)) {
                return;
            }
        }

        ImmutableList<DisplayUnit> displayList = displayRegistry.currentDisplays();
        for (DisplayUnit displayUnit : displayList) {
            Coord localMouse = DisplayHelper.localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY,
                    displayUnit);
            if (processLimitedAction(displayUnit.mouseAction(localMouse, MouseAction.CLICK, eventbutton), displayUnit)) {
                break;
            }
        }
    }

    /**
     * mouseMovedOrUp:= Called when the mouse is moved or a mouse button is released. Signature: (mouseX, mouseY, which)
     * which==-1 is mouseMove, which==0 or which==1 is mouseUp
     * 
     * NOTE: Release cannot be cancelled, a display that received CLICK must be able to receive RELEASE
     */
    @Override
    protected void func_146286_b(int mouseScaledX, int mouseScaledY, int which) {
        super.func_146286_b(mouseScaledX, mouseScaledY, which);
        if (which == 0 || which == 1) {
            for (DisplayUnit window : windows) {
                Coord localMouse = DisplayHelper
                        .localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY, window);
                processAction(window.mouseAction(localMouse, MouseAction.RELEASE), window);
            }

            ImmutableList<DisplayUnit> displayList = displayRegistry.currentDisplays();
            for (DisplayUnit displayUnit : displayList) {
                Coord localMouse = DisplayHelper.localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY,
                        displayUnit);
                processLimitedAction(displayUnit.mouseAction(localMouse, MouseAction.RELEASE), displayUnit);
            }
        }
    }

    /**
     * mouseClickMove:= Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX,
     * mouseY, lastButtonClicked & timeSinceMouseClick.
     */
    @Override
    protected void func_146273_a(int mouseScaledX, int mouseScaledY, int lastButtonClicked, long timeSinceMouseClick) {
        super.func_146273_a(mouseScaledX, mouseScaledY, lastButtonClicked, timeSinceMouseClick);
        for (DisplayUnit window : windows) {
            Coord localMouse = DisplayHelper.localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY, window);
            if (processAction(window.mouseAction(localMouse, MouseAction.CLICK_MOVE, lastButtonClicked), window)) {
                return;
            }
        }

        ImmutableList<DisplayUnit> displayList = displayRegistry.currentDisplays();
        for (DisplayUnit displayUnit : displayList) {
            Coord localMouse = DisplayHelper.localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY,
                    displayUnit);
            if (processLimitedAction(displayUnit.mouseAction(localMouse, MouseAction.CLICK_MOVE, lastButtonClicked),
                    displayUnit)) {
                return;
            }
        }
    }

    @Override
    protected void keyTyped(char eventCharacter, int eventKey) {
        ImmutableList<DisplayUnit> displayList = displayRegistry.currentDisplays();
        for (DisplayUnit window : windows) {
            if (processAction(window.keyTyped(eventCharacter, eventKey), window)) {
                return;
            }
        }

        for (DisplayUnit displayUnit : displayList) {
            if (processLimitedAction(displayUnit.keyTyped(eventCharacter, eventKey), displayUnit)) {
                return;
            }
        }
        super.keyTyped(eventCharacter, eventKey);
    }

    // protected Coord localizeMouseCoords(Minecraft mc, int mouseScaledX, int mouseScaledY, DisplayUnit displayUnit) {
    // return determineScreenPosition(mc, mouseScaledX, mouseScaledY, displayUnit);
    // }

    /**
     * DisplayUnits are a unique case in the Window hierarchy in that they are not windows and cannot be closed
     * directly. DisplayUnitRegistry. DisplayChanger is to be used to remove/add and should be passed the DisplayWindow
     * upon construction.
     * 
     * @param provider May be null if provider is not DisplayWindow in which case it CANNOT be closed
     * @return StopProcessing - true if processing should be stopped
     */
    private boolean processLimitedAction(ActionResult action, DisplayUnit provider) {
        switch (action.interaction) {
        case CLOSE:
            throw new UnsupportedOperationException("DisplayUnit does not support 'CLOSE' Interaction");
        case REPLACE:
            throw new IllegalArgumentException("DisplayUnit does not support 'REPLACE' Interaction");
        case REPLACE_ALL:
            throw new UnsupportedOperationException("DisplayUnit does not support 'REPLACE_ALL' Interaction");
        case NONE:
        default:
            return processAction(action, null);
        }
    }

    /**
     * @param provider May be null if provider is not DisplayWindow in which case it CANNOT be closed
     * @return StopProcessing - true if processing should be stopped
     */
    private boolean processAction(ActionResult action, DisplayUnit provider) {
        switch (action.interaction) {
        case CLOSE:
            if (action.display.isPresent()) {
                windows.remove(action.display);
            }
            break;
        case REPLACE:
            if (provider != null && action.display.isPresent()) {
                windows.add(action.display.get());
                windows.remove(provider);
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
            break;
        }
        return action.stopActing;
    }

    // private Coord determineScreenPosition(Minecraft mc, int mouseScaledX, int mouseScaledY, DisplayUnit display) {
    // ScaledResolution scaledResolition = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
    // Coord displaySize = display.getSize();
    // Coord scaledMouse = new Coord(mouseScaledX, mouseScaledY);
    // VerticalAlignment vertAlign = display.getVerticalAlignment();
    // HorizontalAlignment horizonAlign = display.getHorizontalAlignment();
    // int horzCoord = getHorizontalCoord(horizonAlign, scaledResolition, scaledMouse, displaySize);
    // int vertCoord = getVerticalPosition(vertAlign, scaledResolition, scaledMouse, displaySize);
    // return new Coord(horzCoord, vertCoord);
    // }
    //
    // private int getHorizontalCoord(HorizontalAlignment vertAlign, ScaledResolution resolution, Coord displayOffset,
    // Coord displaySize) {
    // // Reminder do NOT do integer division for %
    // int percOffset = (int) (displayOffset.x * 100f / resolution.getScaledWidth());
    // switch (vertAlign) {
    // default:
    // case LEFT_ABSO:
    // return displayOffset.x;
    // case LEFT_PERC:
    // return percOffset;
    // case CENTER_ABSO:
    // return displayOffset.x - resolution.getScaledWidth() / 2;
    // case CENTER_PERC:
    // return percOffset - 50;
    // case RIGHT_ABSO:
    // return displayOffset.x - resolution.getScaledWidth();
    // case RIGHT_PERC:
    // return percOffset - 100;
    // }
    // }
    //
    // private int getVerticalPosition(VerticalAlignment vertAlign, ScaledResolution resolution, Coord displayOffset,
    // Coord displaySize) {
    // // Reminder do NOT do integer division for %
    // int percOffset = (int) (displayOffset.z * 100f / resolution.getScaledHeight());
    // switch (vertAlign) {
    // default:
    // case TOP_ABSO:
    // return displayOffset.z;
    // case TOP_PECR:
    // return percOffset;
    // case CENTER_ABSO:
    // return displayOffset.z - resolution.getScaledHeight() / 2;
    // case CENTER_PERC:
    // return percOffset - 50;
    // case BOTTOM_ABSO:
    // return displayOffset.z - resolution.getScaledHeight();
    // case BOTTOM_PERC:
    // return percOffset - 100;
    // }
    // }

}
