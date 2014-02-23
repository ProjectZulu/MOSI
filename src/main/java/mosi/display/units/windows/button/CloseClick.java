package mosi.display.units.windows.button;

import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnit.ActionResult;
import mosi.display.units.action.CloseAction;
import mosi.display.units.windows.DisplayUnitButton.Clicker;

/**
 * Clicker implementation that closes provided display on Click/Release
 */
public class CloseClick implements Clicker {
    private DisplayUnit displayToClose;
    private boolean closeOnClick;

    public CloseClick(DisplayUnit displayToClose) {
        this.displayToClose = displayToClose;
        this.closeOnClick = false;
    }

    public CloseClick(DisplayUnit displayToClose, boolean closeOnClick) {
        this.displayToClose = displayToClose;
        this.closeOnClick = closeOnClick;
    }

    @Override
    public ActionResult onClick() {
        return closeOnClick ? new CloseAction(displayToClose).setSelfAsParentAction() : ActionResult.SIMPLEACTION;
    }

    @Override
    public ActionResult onRelease() {
        return closeOnClick ? ActionResult.NOACTION : new CloseAction(displayToClose).setSelfAsParentAction();
    }
}
