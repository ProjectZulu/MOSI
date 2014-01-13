package mosi.display;

import net.minecraft.client.Minecraft;

import org.lwjgl.util.Point;

import com.google.gson.JsonObject;

public interface DisplayUnit {
    /**
     * String type registered to Class object. IMPORTANT: Type is used for Deserialization
     */
    public abstract String getType();

    public abstract Point getPosition();

    public abstract JsonObject saveCustomData(JsonObject jsonObject);

    /**
     * Create a NEW INSTANCE of this Class from exported customData
     */
    public abstract DisplayUnit createFromCustomData(DisplayUnitFactory factory, JsonObject customData);

    public void onUpdate(Minecraft mc, int ticks);

    public boolean shouldRender(Minecraft mc);

    public void renderDisplay(Minecraft mc, Point Position);
}
