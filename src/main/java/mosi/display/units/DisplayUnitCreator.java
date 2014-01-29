package mosi.display.units;

import net.minecraft.client.Minecraft;
import mosi.DisplayUnitRegistry.DisplayChanger;
import mosi.display.units.windows.DisplayWindow;
import mosi.utilities.Coord;

//TODO: This is currntly more a reminder than an implementation
public class DisplayUnitCreator extends DisplayWindow {
    private DisplayChanger displayChanger;

    public DisplayUnitCreator(DisplayChanger displayChanger) {
        this.displayChanger = displayChanger;
    }

    @Override
    public Coord getSize() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VerticalAlignment getVerticalAlignment() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HorizontalAlignment getHorizontalAlignment() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getSubType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void renderSubDisplay(Minecraft mc, Coord Position) {
        // TODO Auto-generated method stub

    }
}
