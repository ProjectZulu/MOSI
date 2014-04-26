package mosi.display.units.windows.button;

import mosi.display.units.DisplayUnit.ActionResult;
import mosi.display.units.windows.DisplayUnitButton.Clicker;
import mosi.display.units.windows.DisplayUnitToggle.Toggle;
import mosi.display.units.windows.DisplayWindowScrollList.Scrollable;
import mosi.display.units.windows.DisplayWindowScrollList.ScrollableElement;

public abstract class AddScrollClick<T, K extends Scrollable<T>> implements Clicker {

    private K container;

    public AddScrollClick(K container) {
        this.container = container;
    }

    @Override
    public ActionResult onClick() {
        return ActionResult.SIMPLEACTION;
    }

    @Override
    public ActionResult onRelease() {
        performScrollAddition(container);
        return ActionResult.SIMPLEACTION;
    }

    public abstract void performScrollAddition(K container);
}
