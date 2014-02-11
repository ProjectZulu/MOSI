package mosi.display.inventoryrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.JsonObject;

import mosi.display.DisplayRemoteDisplay;
import mosi.display.DisplayUnitFactory;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnitSettable;
import mosi.display.units.windows.DisplayWindowScrollList.Scrollable;
import mosi.display.units.windows.DisplayWindowScrollList.ScrollableElement;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;

public class ScrollableSubDisplays implements Scrollable {
    private List<ScrollableRemoteDisplay> displays;

    private static class ScrollableRemoteDisplay extends DisplayRemoteDisplay implements ScrollableElement {
        public static final String DISPLAY_ID = "SubDisplay";
        private boolean scrollVisibility = false;

        public ScrollableRemoteDisplay(DisplayUnit subDisplay) {
            super(subDisplay);
        }

        @Override
        public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
            if (action == MouseAction.CLICK && actionData[0] == 0) {
                // Convert LeftClicks into RightClicks, as dragging is not supported by LeftClick is always drag
                return super.mouseAction(localMouse, action, new int[] { 1 });
            } else {
                return super.mouseAction(localMouse, action, actionData);
            }
        }

        @Override
        public void setScrollVisibity(boolean visibility) {
            scrollVisibility = visibility;
        }

        @Override
        public boolean isVisibleInScroll() {
            return scrollVisibility;
        }
    }

    public ScrollableSubDisplays(List<? extends DisplayUnit> displays) {
        this.displays = new ArrayList<ScrollableRemoteDisplay>();
        for (DisplayUnit displayUnit : displays) {
            this.displays.add(new ScrollableRemoteDisplay(displayUnit));
        }
    }

    @Override
    public Collection<? extends ScrollableElement> getElements() {
        return displays;
    }

    @Override
    public boolean removeElement(ScrollableElement element) {
        return false;
    }
}