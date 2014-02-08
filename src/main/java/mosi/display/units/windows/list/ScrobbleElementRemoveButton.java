package mosi.display.units.windows.list;

import mosi.display.units.DisplayUnit.ActionResult;
import mosi.display.units.windows.DisplayUnitButton.Clicker;
import mosi.display.units.windows.DisplayWindowScrollList.Scrollable;
import mosi.display.units.windows.DisplayWindowScrollList.ScrollableElement;

public class ScrobbleElementRemoveButton implements Clicker {
    private ScrollableElement element;
    private Scrollable container;

    public ScrobbleElementRemoveButton(ScrollableElement element, Scrollable container) {
        this.element = element;
        this.container = container;
    }

    @Override
    public ActionResult onClick() {
        return ActionResult.SIMPLEACTION;
    }

    @Override
    public ActionResult onRelease() {
        container.removeElement(element);
        return ActionResult.SIMPLEACTION;
    }
}
