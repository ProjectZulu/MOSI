package mosi.display.inventoryrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mosi.display.DisplayRemoteDisplay;
import mosi.display.units.DisplayUnit;
import mosi.display.units.windows.DisplayWindowScrollList.Scrollable;
import mosi.display.units.windows.DisplayWindowScrollList.ScrollableElement;
import mosi.display.units.windows.list.EditableList;
import mosi.utilities.Coord;

public class ScrollableSubDisplays implements Scrollable<DisplayUnit> {
    private EditableList<DisplayUnit> source;
    private List<ScrollableElement<DisplayUnit>> displays;

    private static class ScrollableRemoteDisplay extends DisplayRemoteDisplay implements ScrollableElement<DisplayUnit> {
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

        @Override
        public DisplayUnit getSource() {
            return remoteDisplay;
        }
    }

    public ScrollableSubDisplays(List<? extends DisplayUnit> displays) {
        this.displays = new ArrayList<ScrollableElement<DisplayUnit>>();
        for (DisplayUnit displayUnit : displays) {
            this.displays.add(new ScrollableRemoteDisplay(displayUnit));
        }
    }

    public ScrollableSubDisplays(EditableList<DisplayUnit> displaySource) {
        this.source = displaySource;
        this.displays = new ArrayList<ScrollableElement<DisplayUnit>>();
        for (DisplayUnit displayUnit : this.source) {
            this.displays.add(new ScrollableRemoteDisplay(displayUnit));
        }
    }

    @Override
    public Collection<? extends ScrollableElement<DisplayUnit>> getElements() {
        return displays;
    }

    @Override
    public boolean removeElement(ScrollableElement<DisplayUnit> element) {
        source.remove(element.getSource());
        return displays.remove(element);
    }

    @Override
    public boolean addElement(ScrollableElement<DisplayUnit> element) {
        source.add(element.getSource());
        return displays.add(element);
    }
}