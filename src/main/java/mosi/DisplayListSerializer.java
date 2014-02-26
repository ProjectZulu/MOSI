package mosi;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map.Entry;

import mosi.display.DisplayUnitFactory;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnitItem;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DisplayListSerializer implements JsonDeserializer<ArrayList<DisplayUnit>>,
        JsonSerializer<ArrayList<DisplayUnit>> {
    private final String VERSION = "1.0";
    private final String VERSION_KEY = "CONFIG_VERSION";
    private final String DISPLAYS_KEY = "DISPLAYS";
    private final String DISPLAY_TYPE_KEY = "TYPE";

    private DisplayUnitFactory displayFactory;

    public DisplayListSerializer(DisplayUnitFactory displayFactory) {
        this.displayFactory = displayFactory;
    }

    public static File getFile(File configDirectory) {
        return new File(configDirectory, DefaultProps.MOD_DIR + "DisplaySettings.cfg");
    }

    @Override
    public JsonElement serialize(ArrayList<DisplayUnit> displays, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject endObject = new JsonObject();
        endObject.addProperty(VERSION_KEY, VERSION);
        JsonArray displaysObject = new JsonArray();
        for (DisplayUnit display : displays) {
            JsonObject displayObject = new JsonObject();
            displayObject.addProperty(DISPLAY_TYPE_KEY, display.getType());
            display.saveCustomData(displayObject);
            displaysObject.add(displayObject);
        }
        endObject.add(DISPLAYS_KEY, displaysObject);
        return endObject;
    }

    @Override
    public ArrayList<DisplayUnit> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        ArrayList<DisplayUnit> displays = new ArrayList<DisplayUnit>();
        JsonObject endObject = json.getAsJsonObject();
        String currentVersion = readVersion(endObject);

        JsonArray displaysObject = endObject.get(DISPLAYS_KEY).getAsJsonArray();
        for (JsonElement entry : displaysObject) {
            JsonObject displayObject = entry.getAsJsonObject();
            DisplayUnit display = displayFactory.createDisplay(displayObject.get(DISPLAY_TYPE_KEY).getAsString(),
                    displayObject);
            if (display != null) {
                displays.add(display);
            }
        }
        return displays;
    }

    private String readVersion(JsonObject object) {
        JsonElement element = object.get(VERSION_KEY);
        if (element != null && element.isJsonPrimitive()) {
            return element.getAsString();
        }
        Log.log().warning("Could not read version state, assuming current %s", VERSION);
        return VERSION;
    }
}
