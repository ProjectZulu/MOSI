package mosi.display;

import mosi.DisplayUnitRegistry;
import mosi.Log;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnit.ActionResult;
import mosi.display.units.DisplayUnit.HorizontalAlignment;
import mosi.display.units.DisplayUnit.MouseAction;
import mosi.display.units.DisplayUnit.VerticalAlignment;
import mosi.display.units.DisplayWindow;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * Screen responsible for interaction with Displays. See displayTicker for DisplayUnit rendering
 */
public class DisplayScreen extends GuiScreen {
    private DisplayUnitRegistry displayRegistry;

    // Menu/Subscreen created by clicking/hotkey, global such to ensure only one menu/s
    private Optional<DisplayWindow> menu;

    // Helper for when parent minecraft field is obfuscarted
    public Minecraft getMinecraft() {
        return field_146297_k;
    }

    public DisplayScreen(DisplayUnitRegistry displayRegistry) {
        super();
        this.displayRegistry = displayRegistry;
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
            Coord localMouse = localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY, displayUnit);
            displayUnit.mousePosition(localMouse);
        }
    }

    @Override
    protected void mouseClicked(int mouseScaledX, int mouseScaledY, int eventbutton) {
        super.mouseClicked(mouseScaledX, mouseScaledY, eventbutton);
        if (menu.isPresent()) {
            Coord localMouse = localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY, menu.get()
                    .getBaseDisplay());
            if (processAction(menu.get().mouseAction(localMouse, MouseAction.CLICK, eventbutton))) {
                return;
            }
        }

        ImmutableList<DisplayUnit> displayList = displayRegistry.currentDisplays();
        for (DisplayUnit displayUnit : displayList) {
            Coord localMouse = localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY, displayUnit);
            if (processAction(displayUnit.mouseAction(localMouse, MouseAction.CLICK, eventbutton))) {
                break;
            }
        }
    }

    /**
     * mouseMovedOrUp:= Called when the mouse is moved or a mouse button is released. Signature: (mouseX, mouseY, which)
     * which==-1 is mouseMove, which==0 or which==1 is mouseUp
     */
    @Override
    protected void func_146286_b(int mouseScaledX, int mouseScaledY, int which) {
        super.func_146286_b(mouseScaledX, mouseScaledY, which);
        if (which == 0 || which == 1) {
            if (menu.isPresent()) {
                Coord localMouse = localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY, menu.get()
                        .getBaseDisplay());
                if (processAction(menu.get().mouseAction(localMouse, MouseAction.RELEASE))) {
                    return;
                }
            }

            ImmutableList<DisplayUnit> displayList = displayRegistry.currentDisplays();
            for (DisplayUnit displayUnit : displayList) {
                Coord localMouse = localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY, displayUnit);
                if (processAction(displayUnit.mouseAction(localMouse, MouseAction.RELEASE))) {
                    return;
                }
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
        if (menu.isPresent()) {
            Coord localMouse = localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY, menu.get()
                    .getBaseDisplay());
            if (processAction(menu.get().mouseAction(localMouse, MouseAction.CLICK_MOVE, lastButtonClicked))) {
                return;
            }
        }
        ImmutableList<DisplayUnit> displayList = displayRegistry.currentDisplays();
        for (DisplayUnit displayUnit : displayList) {
            Coord localMouse = localizeMouseCoords(getMinecraft(), mouseScaledX, mouseScaledY, displayUnit);
            if (processAction(menu.get().mouseAction(localMouse, MouseAction.CLICK_MOVE, lastButtonClicked))) {
                return;
            }
        }
    }

    @Override
    protected void keyTyped(char eventCharacter, int eventKey) {
        ImmutableList<DisplayUnit> displayList = displayRegistry.currentDisplays();
        for (DisplayUnit displayUnit : displayList) {
            if (processAction(displayUnit.keyTyped(eventCharacter, eventKey))) {
                return;
            }
        }
        super.keyTyped(eventCharacter, eventKey);
    }

    protected Coord localizeMouseCoords(Minecraft mc, int mouseScaledX, int mouseScaledY, DisplayUnit displayUnit) {
        return determineScreenPosition(mc, mouseScaledX, mouseScaledY, displayUnit);
    }

    protected boolean processAction(ActionResult action) {
        switch (action.interaction) {
        case CLOSE:
            if (menu.isPresent() && menu.get().equals(action.display)) {
                menu = Optional.absent();
            }
            break;
        case OPEN:
            if (action.display != null) {
                menu.of(action.display);
            }
            break;
        case REPLACE:
            menu = action.display != null ? Optional.of(action.display) : Optional.<DisplayWindow> absent();
            break;
        case NONE:
            break;
        }
        return action.stopActing;
    }

    private Coord determineScreenPosition(Minecraft mc, int mouseScaledX, int mouseScaledY, DisplayUnit display) {
        ScaledResolution scaledResolition = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        Coord displaySize = display.getSize();
        Coord displayOffset = new Coord(mouseScaledX, mouseScaledY);
        VerticalAlignment vertAlign = display.getVerticalAlignment();
        HorizontalAlignment horizonAlign = display.getHorizontalAlignment();
        int horzCoord = getHorizontalCoord(horizonAlign, scaledResolition, displayOffset, displaySize);
        int vertCoord = getVerticalPosition(vertAlign, scaledResolition, displayOffset, displaySize);
        return new Coord(horzCoord, vertCoord);
    }

    private int getHorizontalCoord(HorizontalAlignment vertAlign, ScaledResolution resolution, Coord displayOffset,
            Coord displaySize) {
        int percOffset = (int) (resolution.getScaledWidth() * displayOffset.x / 100f);
        switch (vertAlign) {
        default:
        case LEFT_ABSO:
            return displayOffset.x;
        case LEFT_PERC:
            return percOffset;
        case CENTER_ABSO:
            return (resolution.getScaledWidth() / 2 - displaySize.x / 2) + displayOffset.x;
        case CENTER_PERC:
            return (resolution.getScaledWidth() / 2 - displaySize.x / 2) + percOffset;
        case RIGHT_ABSO:
            return (resolution.getScaledWidth() - displaySize.x) + displayOffset.x;
        case RIGHT_PERC:
            return (resolution.getScaledWidth() - displaySize.x) + percOffset;
        }
    }

    private int getVerticalPosition(VerticalAlignment vertAlign, ScaledResolution resolution, Coord displayOffset,
            Coord displaySize) {
        int percOffset = (int) (resolution.getScaledHeight() * displayOffset.z / 100f);
        switch (vertAlign) {
        default:
        case TOP_ABSO:
            return displayOffset.z;
        case TOP_PECR:
            return percOffset;
        case CENTER_ABSO:
            return (resolution.getScaledHeight() / 2 - displaySize.z / 2) + displayOffset.z;
        case CENTER_PERC:
            return (resolution.getScaledHeight() / 2 - displaySize.z / 2) + percOffset;
        case BOTTOM_ABSO:
            return (resolution.getScaledHeight() - displaySize.z) + displayOffset.z;
        case BOTTOM_PERC:
            return (resolution.getScaledHeight() - displaySize.z) + percOffset;
        }
    }

}
