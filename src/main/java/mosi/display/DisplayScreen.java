package mosi.display;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import mosi.DisplayUnitRegistry;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnit.ActionResult;
import mosi.display.units.DisplayUnit.HoverAction;
import mosi.display.units.DisplayUnit.HoverTracker;
import mosi.display.units.DisplayUnit.MouseAction;
import mosi.display.units.DisplayUnitInventoryRule;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;

/**
 * Screen responsible for interaction with Displays. See displayTicker for DisplayUnit rendering
 */
public class DisplayScreen extends GuiScreen {
    private DisplayUnitRegistry displayRegistry;
    private int ticks = 0;

    // Menu/Subscreen created by clicking/hotkey, global such to ensure only one menu/s
    private ArrayList<DisplayUnit> windows;
    // Temporary list of displays that need to be moved higher in the display list (higher displays get events sooner)
    private Queue<DisplayUnit> priority;

    public void addWindow(DisplayUnit displayUnit) {
        windows.add(displayUnit);
    }

    public void removeWindow(DisplayUnit displayUnit) {
        windows.remove(displayUnit);
    }

    protected final void clearWindows() {
        windows.clear();
    }

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
        priority = new ArrayDeque<DisplayUnit>();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        while (!priority.isEmpty()) {
            DisplayUnit display = priority.poll();
            if (windows.remove(display)) {
                windows.add(0, display);
            }
        }

        for (DisplayUnit display : windows) {
            display.onUpdate(getMinecraft(), ticks);
        }
        ticks++;
    }

    /**
     * @param mouseRelative is the mouse position relative to resolution and screen size
     * @param renderPartialTicks How much time has elapsed since the last tick, in ticks [0.0,1.0]
     */
    @Override
    public void drawScreen(int mouseScaledX, int mouseScaledY, float renderPartialTicks) {
        super.drawScreen(mouseScaledX, mouseScaledY, renderPartialTicks);

        ImmutableList<DisplayUnit> displayList = displayRegistry.currentDisplays();
        HoverTracker hoverChecker = new HoverTracker();
        for (DisplayUnit window : windows) {
            Coord childCoords = DisplayHelper.localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY, window);
            HoverAction childHover = HoverAction.OUTSIDE;
            if (DisplayHelper.isCursorOverDisplay(childCoords, window)) {
                childHover = !hoverChecker.isHoverFound() ? HoverAction.HOVER : HoverAction.BLOCKED;
            }
            window.mousePosition(childCoords, childHover, hoverChecker);
        }
        for (DisplayUnit displayUnit : displayList) {
            Coord childCoords = DisplayHelper.localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY,
                    displayUnit);
            HoverAction childHover = HoverAction.OUTSIDE;
            if (DisplayHelper.isCursorOverDisplay(childCoords, displayUnit)) {
                childHover = !hoverChecker.isHoverFound() ? HoverAction.HOVER : HoverAction.BLOCKED;
            }
            displayUnit.mousePosition(childCoords, childHover, hoverChecker);
        }
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        /**
         * Reverse iteration we are doing back to front rendering and top of list is considered 'front' i.e. given
         * priority for clicks
         */
        for (int i = windows.size() - 1; i >= 0; i--) {
            DisplayUnit displayUnit = windows.get(i);
            Coord localMouse = DisplayHelper.localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY,
                    displayUnit);
            Coord screenPos = DisplayHelper.determineScreenPositionFromDisplay(getMinecraft(), new Coord(0, 0),
                    calcScaledScreenSize(), displayUnit);
            displayUnit.renderDisplay(getMinecraft(), screenPos);
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    @Override
    protected void mouseClicked(int mouseScaledX, int mouseScaledY, int eventbutton) {
        super.mouseClicked(mouseScaledX, mouseScaledY, eventbutton);
        for (DisplayUnit window : windows) {
            Coord localMouse = DisplayHelper.localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY, window);
            if (processActionResult(window.mouseAction(localMouse, MouseAction.CLICK, eventbutton), window)) {
                return;
            }
        }

        ImmutableList<DisplayUnit> displayList = displayRegistry.currentDisplays();
        for (DisplayUnit displayUnit : displayList) {
            Coord localMouse = DisplayHelper.localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY,
                    displayUnit);
            if (processActionResult(displayUnit.mouseAction(localMouse, MouseAction.CLICK, eventbutton), displayUnit)) {
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
                processActionResult(window.mouseAction(localMouse, MouseAction.RELEASE), window);
            }

            ImmutableList<DisplayUnit> displayList = displayRegistry.currentDisplays();
            for (DisplayUnit displayUnit : displayList) {
                Coord localMouse = DisplayHelper.localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY,
                        displayUnit);
                processActionResult(displayUnit.mouseAction(localMouse, MouseAction.RELEASE), displayUnit);
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
            if (processActionResult(window.mouseAction(localMouse, MouseAction.CLICK_MOVE, lastButtonClicked), window)) {
                return;
            }
        }

        ImmutableList<DisplayUnit> displayList = displayRegistry.currentDisplays();
        for (DisplayUnit displayUnit : displayList) {
            Coord localMouse = DisplayHelper.localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY,
                    displayUnit);
            if (processActionResult(displayUnit.mouseAction(localMouse, MouseAction.CLICK_MOVE, lastButtonClicked),
                    displayUnit)) {
                return;
            }
        }
    }

    @Override
    protected void keyTyped(char eventCharacter, int eventKey) {
        ImmutableList<DisplayUnit> displayList = displayRegistry.currentDisplays();
        for (DisplayUnit window : windows) {
            if (processActionResult(window.keyTyped(eventCharacter, eventKey), window)) {
                return;
            }
        }

        for (DisplayUnit displayUnit : displayList) {
            if (processActionResult(displayUnit.keyTyped(eventCharacter, eventKey), displayUnit)) {
                return;
            }
        }
        super.keyTyped(eventCharacter, eventKey);
    }

    private boolean processActionResult(ActionResult action, DisplayUnit provider) {
        if (provider != null && provider instanceof DisplayUnitInventoryRule) {
            boolean blah = true;
        }

        if (action.closeAll()) {
            clearWindows();
        } else {
            List<DisplayUnit> displaysToClose = action.screensToClose();
            for (DisplayUnit displayUnit : displaysToClose) {
                removeWindow(displayUnit);
            }
        }

        List<DisplayUnit> displaysToOpen = action.screensToOpen();
        for (DisplayUnit displayUnit : displaysToOpen) {
            addWindow(displayUnit);
        }

        if (action.shouldStop()) {
            // Some interaction occurred in that display, elevate it to receive events sooner
            priority.add(provider);
        }
        return action.shouldStop();
    }
}
