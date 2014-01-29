package mosi.display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnit.HorizontalAlignment;
import mosi.display.units.DisplayUnit.VerticalAlignment;
import mosi.utilities.Coord;

public class DisplayHelper {
    public static boolean isCursorOverDisplay(Coord mousePos, DisplayUnit display) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledResolition = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        Coord displaySizePerc = display.getSize().multf(100f)
                .divf(scaledResolition.getScaledWidth(), scaledResolition.getScaledHeight());
        if (!isWithinXBounds(display.getHorizontalAlignment(), mousePos.x, display.getOffset().x, display.getSize().x,
                displaySizePerc.x)) {
            return false;
        }

        if (!isWithinYBounds(display.getVerticalAlignment(), mousePos.z, display.getOffset().z, display.getSize().z,
                displaySizePerc.z)) {
            return false;
        }

        return true;
    }

    private static boolean isWithinXBounds(HorizontalAlignment alignment, int mousePos, int dispOffset, int dispSize,
            int dispSizePerc) {
        switch (alignment) {
        case LEFT_ABSO:
            return isWithinBounds(mousePos, dispOffset, dispOffset + dispSize);
        case LEFT_PERC:
            return isWithinBounds(mousePos, dispOffset, dispOffset + dispSizePerc);
        case CENTER_ABSO:
            return isWithinBounds(mousePos, dispOffset - dispSize / 2, dispOffset + dispSize / 2);
        case CENTER_PERC:
            return isWithinBounds(mousePos, dispOffset - dispSizePerc / 2, dispOffset + dispSizePerc / 2);
        case RIGHT_ABSO:
            return isWithinBounds(mousePos, dispOffset - dispSize, dispOffset);
        case RIGHT_PERC:
            return isWithinBounds(mousePos, dispOffset - dispSizePerc, dispOffset);
        }
        throw new IllegalArgumentException("This should not happen, alignment invalid case " + alignment.toString());
    }

    private static boolean isWithinYBounds(VerticalAlignment alignment, int mousePos, int dispOffset, int dispSize,
            int dispSizePerc) {
        switch (alignment) {
        case TOP_ABSO:
            return isWithinBounds(mousePos, dispOffset, dispOffset + dispSize);
        case TOP_PECR:
            return isWithinBounds(mousePos, dispOffset, dispOffset + dispSizePerc);
        case CENTER_ABSO:
            return isWithinBounds(mousePos, dispOffset - dispSize / 2, dispOffset + dispSize / 2);
        case CENTER_PERC:
            return isWithinBounds(mousePos, dispOffset - dispSizePerc / 2, dispOffset + dispSizePerc / 2);
        case BOTTOM_ABSO:
            return isWithinBounds(mousePos, dispOffset - dispSize, dispOffset);
        case BOTTOM_PERC:
            return isWithinBounds(mousePos, dispOffset - dispSizePerc, dispOffset);
        }
        throw new IllegalArgumentException("This should not happen, alignment invalid case " + alignment.toString());
    }

    private static boolean isWithinBounds(int value, int min, int max) {
        return value >= min && value <= max;
    }
}
