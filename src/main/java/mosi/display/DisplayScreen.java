package mosi.display;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import mosi.DisplayUnitRegistry;
import mosi.Log;
import mosi.MOSI;
import mosi.display.inventoryrules.ScrollableSubDisplays;
import mosi.display.resource.SimpleImageResource.GuiIconImageResource;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnit.ActionResult;
import mosi.display.units.DisplayUnit.HorizontalAlignment;
import mosi.display.units.DisplayUnit.HoverAction;
import mosi.display.units.DisplayUnit.HoverTracker;
import mosi.display.units.DisplayUnit.MouseAction;
import mosi.display.units.DisplayUnit.VerticalAlignment;
import mosi.display.units.DisplayUnitItem;
import mosi.display.units.DisplayUnitPotion;
import mosi.display.units.DisplayUnitSortedPanel;
import mosi.display.units.DisplayUnitUnsortedPanel;
import mosi.display.units.action.ReplaceAction;
import mosi.display.units.windows.DisplayUnitButton;
import mosi.display.units.windows.DisplayUnitButton.Clicker;
import mosi.display.units.windows.DisplayUnitTextBoard;
import mosi.display.units.windows.DisplayUnitToggle;
import mosi.display.units.windows.DisplayWindowMenu;
import mosi.display.units.windows.DisplayWindowScrollList;
import mosi.display.units.windows.button.AddScrollClick;
import mosi.display.units.windows.button.CloseClick;
import mosi.display.units.windows.button.MoveScrollElementToggle;
import mosi.display.units.windows.button.RemoveScrollToggle;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Optional;
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
    // Temporary list of displays that need to be removed
    private ArrayList<DisplayUnit> windowsToBeRemoved;

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
        windowsToBeRemoved = new ArrayList<DisplayUnit>();
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

        for (DisplayUnit displayToRemove : windowsToBeRemoved) {
            windows.remove(displayToRemove);
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
        ArrayList<DisplayUnit> displayList = displayRegistry.currentDisplays();
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
    public void func_146274_d() {
        super.func_146274_d();
        int mouseScaledX = Mouse.getEventX() * this.field_146294_l / this.field_146297_k.displayWidth;
        int mouseScaledY = this.field_146295_m - Mouse.getEventY() * this.field_146295_m
                / this.field_146297_k.displayHeight - 1;
        int scrollAmount = Mouse.getEventDWheel();
        if (scrollAmount != 0) {
            Log.log().info("Scroll evetn with [%s]", scrollAmount);
            for (DisplayUnit window : windows) {
                Coord localMouse = DisplayHelper
                        .localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY, window);
                if (processActionResult(window.mouseAction(localMouse, MouseAction.SCROLL, scrollAmount), window)) {
                    return;
                }
            }

            ArrayList<DisplayUnit> displayList = displayRegistry.currentDisplays();
            for (DisplayUnit displayUnit : displayList) {
                Coord localMouse = DisplayHelper.localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY,
                        displayUnit);
                if (processActionResult(displayUnit.mouseAction(localMouse, MouseAction.SCROLL, scrollAmount),
                        displayUnit)) {
                    break;
                }
            }
        }
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

        ArrayList<DisplayUnit> displayList = displayRegistry.currentDisplays();
        for (DisplayUnit displayUnit : displayList) {
            Coord localMouse = DisplayHelper.localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY,
                    displayUnit);
            if (processActionResult(displayUnit.mouseAction(localMouse, MouseAction.CLICK, eventbutton), displayUnit)) {
                return;
            }
        }

        if (eventbutton == 1) {
            DisplayWindowMenu menu = new DisplayWindowMenu(new Coord(2, 2), HorizontalAlignment.CENTER_ABSO,
                    VerticalAlignment.CENTER_ABSO);
            menu.addElement(new DisplayUnitTextBoard(new Coord(0, 2), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, "MOSI Menu").setBackgroundImage(null));
            menu.addElement(new DisplayUnitButton(new Coord(0, 20), new Coord(90, 15), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, new Clicker() {
                        private ArrayList<DisplayUnit> displayList;

                        public Clicker init(ArrayList<DisplayUnit> displayList) {
                            this.displayList = displayList;
                            return this;
                        }

                        @Override
                        public ActionResult onClick() {
                            return ActionResult.SIMPLEACTION;
                        }

                        @Override
                        public ActionResult onRelease() {
                            ScrollableSubDisplays<DisplayUnit> scrollable = new ScrollableSubDisplays<DisplayUnit>(
                                    displayList);
                            DisplayWindowScrollList<DisplayUnit> slider = new DisplayWindowScrollList<DisplayUnit>(
                                    new Coord(90, 00), new Coord(140, 200), 25, VerticalAlignment.TOP_ABSO,
                                    HorizontalAlignment.LEFT_ABSO, scrollable);
                            // Add Element Buttons
                            slider.addElement(new DisplayUnitButton(new Coord(2, 2), new Coord(20, 20),
                                    VerticalAlignment.TOP_ABSO, HorizontalAlignment.LEFT_ABSO,
                                    new AddScrollClick<DisplayUnit, ScrollableSubDisplays<DisplayUnit>>(scrollable) {
                                        @Override
                                        public void performScrollAddition(ScrollableSubDisplays<DisplayUnit> container) {
                                            container.addElement(new DisplayUnitPotion());
                                        }
                                    }).setIconImageResource(new GuiIconImageResource(new Coord(147, 44), new Coord(12,
                                    16))));
                            slider.addElement(new DisplayUnitButton(new Coord(23, 2), new Coord(20, 20),
                                    VerticalAlignment.TOP_ABSO, HorizontalAlignment.LEFT_ABSO,
                                    new AddScrollClick<DisplayUnit, ScrollableSubDisplays<DisplayUnit>>(scrollable) {
                                        @Override
                                        public void performScrollAddition(ScrollableSubDisplays<DisplayUnit> container) {
                                            container.addElement(new DisplayUnitItem());
                                        }
                                    }).setIconImageResource(new GuiIconImageResource(new Coord(165, 44), new Coord(12,
                                    16))));
                            slider.addElement(new DisplayUnitButton(new Coord(44, 2), new Coord(20, 20),
                                    VerticalAlignment.TOP_ABSO, HorizontalAlignment.LEFT_ABSO,
                                    new AddScrollClick<DisplayUnit, ScrollableSubDisplays<DisplayUnit>>(scrollable) {

                                        @Override
                                        public void performScrollAddition(ScrollableSubDisplays<DisplayUnit> container) {
                                            container.addElement(new DisplayUnitSortedPanel());
                                        }
                                    }).setIconImageResource(new GuiIconImageResource(new Coord(111, 66), new Coord(12,
                                    15))));
                            // List interactive Buttons - Remove, MoveUp, MoveDown
                            slider.addElement(new DisplayUnitToggle(new Coord(-2, 2), new Coord(20, 20),
                                    VerticalAlignment.TOP_ABSO, HorizontalAlignment.RIGHT_ABSO,
                                    new RemoveScrollToggle<DisplayUnit>(scrollable))
                                    .setIconImageResource(new GuiIconImageResource(new Coord(201, 44),
                                            new Coord(13, 16))));
                            slider.addElement(new DisplayUnitToggle(new Coord(-23, 2), new Coord(20, 20),
                                    VerticalAlignment.TOP_ABSO, HorizontalAlignment.RIGHT_ABSO,
                                    new MoveScrollElementToggle<DisplayUnit>(scrollable, 1))
                                    .setIconImageResource(new GuiIconImageResource(new Coord(165, 66),
                                            new Coord(12, 15))));
                            slider.addElement(new DisplayUnitToggle(new Coord(-44, 2), new Coord(20, 20),
                                    VerticalAlignment.TOP_ABSO, HorizontalAlignment.RIGHT_ABSO,
                                    new MoveScrollElementToggle<DisplayUnit>(scrollable, -1))
                                    .setIconImageResource(new GuiIconImageResource(new Coord(147, 66),
                                            new Coord(12, 15))));
                            slider.addElement(new DisplayUnitButton(new Coord(0, -2), new Coord(60, 20),
                                    VerticalAlignment.BOTTOM_ABSO, HorizontalAlignment.CENTER_ABSO, new CloseClick(
                                            slider), "Close"));
                            return new ReplaceAction(slider, true);
                        }
                    }.init(displayList), "Display Editor"));
            menu.addElement(new DisplayUnitButton(new Coord(0, 50), new Coord(50, 15), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, new CloseClick(menu), "Close"));
            processActionResult(new ReplaceAction(menu, true), Optional.<DisplayUnit> absent());
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

            ArrayList<DisplayUnit> displayList = displayRegistry.currentDisplays();
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

        ArrayList<DisplayUnit> displayList = displayRegistry.currentDisplays();
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
        ArrayList<DisplayUnit> displayList = displayRegistry.currentDisplays();
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
        // If Hit ESC, GUI screen is closing and we should save
        if (eventKey == 1) {
            displayRegistry.saveToConfig(MOSI.getConfigDirectory());
        }
        super.keyTyped(eventCharacter, eventKey);
    }

    private boolean processActionResult(ActionResult action, DisplayUnit provider) {
        return processActionResult(action, provider != null ? Optional.of(provider) : Optional.<DisplayUnit> absent());
    }

    private boolean processActionResult(ActionResult action, Optional<DisplayUnit> provider) {

        if (action.closeAll()) {
            windowsToBeRemoved.addAll(windows);
        } else {
            List<DisplayUnit> displaysToClose = action.screensToClose();
            for (DisplayUnit displayUnit : displaysToClose) {
                windowsToBeRemoved.add(displayUnit);
            }
        }

        List<DisplayUnit> displaysToOpen = action.screensToOpen();
        for (DisplayUnit displayUnit : displaysToOpen) {
            addWindow(displayUnit);
        }

        if (action.shouldStop() && provider.isPresent()) {
            // Some interaction occurred in that display, elevate it to receive events sooner
            priority.add(provider.get());
        }
        return action.shouldStop();
    }
}
