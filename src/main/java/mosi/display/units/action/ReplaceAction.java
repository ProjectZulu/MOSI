package mosi.display.units.action;

import java.util.ArrayList;
import java.util.List;

import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnit.ActionResult;

public class ReplaceAction implements ActionResult {
    private ArrayList<DisplayUnit> displaysToOpen;
    private ArrayList<DisplayUnit> displaysToClose;
    private boolean closeAll;

    public ReplaceAction(DisplayUnit displayToOpen, DisplayUnit displayToClose) {
        this(new DisplayUnit[] { displayToOpen }, new DisplayUnit[] { displayToClose });
    }

    public ReplaceAction(DisplayUnit[] displaysToOpen, DisplayUnit[] displaysToClose) {
        this.displaysToOpen = new ArrayList<DisplayUnit>();
        for (DisplayUnit displayToOpen : displaysToOpen) {
            this.displaysToOpen.add(displayToOpen);
        }
        this.displaysToClose = new ArrayList<DisplayUnit>();
        for (DisplayUnit displayToClose : displaysToClose) {
            this.displaysToClose.add(displayToClose);
        }
        this.closeAll = false;
    }

    public ReplaceAction(DisplayUnit displayToOpen, boolean closeAll) {
        this(new DisplayUnit[] { displayToOpen }, closeAll);
    }

    public ReplaceAction(DisplayUnit[] displaysToOpen, boolean closeAll) {
        this.displaysToOpen = new ArrayList<DisplayUnit>();
        for (DisplayUnit displayToOpen : displaysToOpen) {
            this.displaysToOpen.add(displayToOpen);
        }
        this.displaysToClose = new ArrayList<DisplayUnit>();
        this.closeAll = closeAll;
    }

    @Override
    public boolean closeAll() {
        return closeAll;
    }

    @Override
    public boolean shouldStop() {
        return true;
    }

    @Override
    public List<DisplayUnit> screensToClose() {
        return displaysToClose;
    }

    @Override
    public List<DisplayUnit> screensToOpen() {
        return displaysToOpen;
    }
}
