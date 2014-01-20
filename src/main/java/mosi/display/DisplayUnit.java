package mosi.display;

import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;

import com.google.gson.JsonObject;

public interface DisplayUnit {
    /**
     * String type registered to Class object. IMPORTANT: Type is used for Deserialization
     */
    public abstract String getType();

    public abstract Coord getOffset();

    public abstract Coord getSize();

    public enum VerticalAlignment {
        TOP_PECR, TOP_ABSO, BOTTOM_PERC, BOTTOM_ABSO, CENTER_PERC, CENTER_ABSO;
    }

    public abstract VerticalAlignment getVerticalAlignment();

    public enum HorizontalAlignment {
        LEFT_PERC, LEFT_ABSO, RIGHT_PERC, RIGHT_ABSO, CENTER_PERC, CENTER_ABSO;
    }
    public abstract HorizontalAlignment getHorizontalAlignment();

    public void onUpdate(Minecraft mc, int ticks);

    public boolean shouldRender(Minecraft mc);

    public void renderDisplay(Minecraft mc, Coord Position);

    public abstract JsonObject saveCustomData(JsonObject jsonObject);

    public abstract void loadCustomData(DisplayUnitFactory factory, JsonObject customData);
}
