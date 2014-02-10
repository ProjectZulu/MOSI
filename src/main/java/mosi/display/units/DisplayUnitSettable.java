package mosi.display.units;

import mosi.utilities.Coord;

public interface DisplayUnitSettable extends DisplayUnit {
    public abstract void setOffset(Coord offset);

    public abstract void setVerticalAlignment(VerticalAlignment alignment);

    public abstract void setHorizontalAlignment(HorizontalAlignment alignment);
}
