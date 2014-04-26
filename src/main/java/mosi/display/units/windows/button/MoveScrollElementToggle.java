package mosi.display.units.windows.button;

import mosi.display.units.DisplayUnit.ActionResult;
import mosi.display.units.windows.DisplayUnitButton.Clicker;
import mosi.display.units.windows.DisplayUnitToggle.Toggle;
import mosi.display.units.windows.DisplayWindowScrollList.Scrollable;
import mosi.display.units.windows.DisplayWindowScrollList.ScrollableElement;

import com.google.common.base.Optional;

public class MoveScrollElementToggle<T> implements Toggle {

    private Scrollable<T> container;
    private int unitstoMove;

    public MoveScrollElementToggle(Scrollable<T> container, int unitstoMove) {
        this.container = container;
        this.unitstoMove = unitstoMove;
    }

    @Override
    public void toggle() {
        Optional<ScrollableElement<T>> toMove = container.getSelected();
        if (toMove.isPresent()) {
            container.moveElement(toMove.get(), unitstoMove);
        }
    }

    @Override
    public boolean isToggled() {
        return container.getSelected().isPresent();
    }
}
