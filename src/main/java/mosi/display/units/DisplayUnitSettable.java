package mosi.display.units;

import mosi.display.DisplayUnitFactory;
import mosi.display.units.DisplayUnit.ActionResult;
import mosi.display.units.DisplayUnit.HorizontalAlignment;
import mosi.display.units.DisplayUnit.MouseAction;
import mosi.display.units.DisplayUnit.VerticalAlignment;
import mosi.display.units.DisplayUnit.ActionResult.INTERACTION;
import mosi.display.units.DisplayUnit.ActionResult.SimpleAction;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;

import com.google.common.base.Optional;
import com.google.gson.JsonObject;

public interface DisplayUnitSettable extends DisplayUnit {
    public abstract Coord setOffset(Coord offset);

    public abstract VerticalAlignment setVerticalAlignment(VerticalAlignment alignment);

    public abstract HorizontalAlignment setHorizontalAlignment(HorizontalAlignment alignment);
}
