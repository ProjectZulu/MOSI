package mosi.display.units;

import mosi.Log;
import mosi.display.DisplayHelper;
import mosi.utilities.Coord;

import org.lwjgl.input.Keyboard;

public abstract class DisplayUnitMoveable implements DisplayUnit {
    protected Coord offset;
    protected transient boolean clickedOn = false;
    // Mouse location was on mouseClick. Click + Drag -> Offset = OriginalOffset + (MousePos - mousePosOnClick)
    protected transient Coord mousePosOnClick;
    // Original location were on mouseClick. Click + Drag -> Offset = OriginalOffset + (MousePos - mousePosOnClick)
    protected transient Coord offsetPosOnClick;

    public DisplayUnitMoveable(Coord offset) {
        this.offset = offset;
    }

    @Override
    public final Coord getOffset() {
        return offset;
    }

    @Override
    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
        switch (action) {
        case CLICK:
            // actionData[0] == EventButton, 0 == Left-Click, 1 == Right-Click
            if (actionData[0] == 0 && DisplayHelper.isCursorOverDisplay(localMouse, this)) {
                clickedOn = true;
                mousePosOnClick = localMouse;
                offsetPosOnClick = offset;
                return new ActionResult(true);
            }
            break;
        case CLICK_MOVE:
            if (clickedOn) {
                offset = offsetPosOnClick.add(localMouse.subt(mousePosOnClick));
                return new ActionResult(true);
            }
            break;
        case RELEASE:
            clickedOn = false;
            return ActionResult.NOACTION;
        }
        return ActionResult.NOACTION;
    }

    @Override
    public ActionResult keyTyped(char eventCharacter, int eventKey) {
        if (clickedOn) {
            Log.log().info("Char %s  -- Key %s", eventCharacter, eventKey);
            if (Keyboard.KEY_LEFT == eventKey) {
                offset = offset.add(-1, 0);
                mousePosOnClick = mousePosOnClick.subt(-1, 0);
            } else if (Keyboard.KEY_RIGHT == eventKey) {
                offset = offset.add(+1, 0);
                mousePosOnClick = mousePosOnClick.subt(+1, 0);
            } else if (Keyboard.KEY_DOWN == eventKey) {
                offset = offset.add(0, +1);
                mousePosOnClick = mousePosOnClick.subt(0, +1);
            } else if (Keyboard.KEY_UP == eventKey) {
                offset = offset.add(0, -1);
                mousePosOnClick = mousePosOnClick.subt(0, -1);
            }
        }
        return ActionResult.NOACTION;
    }
}
