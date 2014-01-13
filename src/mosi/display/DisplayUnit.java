package mosi.display;

import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;

import org.lwjgl.util.Point;

import com.google.gson.JsonObject;

public interface DisplayUnit {
    /**
     * String type registered to Class object. IMPORTANT: Type is used for Deserialization
     */
    public abstract String getType();

    public abstract Coord getPosition();

    public void onUpdate(Minecraft mc, int ticks);

    public boolean shouldRender(Minecraft mc);

    public void renderDisplay(Minecraft mc, Point Position);

    public abstract JsonObject saveCustomData(JsonObject jsonObject);

    public abstract void loadCustomData(DisplayUnitFactory factory, JsonObject customData);
}
