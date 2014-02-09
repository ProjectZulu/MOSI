package mosi.display.units;

import mosi.utilities.Coord;

public interface DisplayUnitSettable extends DisplayUnit {
    public abstract Coord setOffset(Coord offset);

    public abstract VerticalAlignment setVerticalAlignment(VerticalAlignment alignment);

    public abstract HorizontalAlignment setHorizontalAlignment(HorizontalAlignment alignment);
}
