package mosi.display;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import mosi.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DisplayUnitFactory implements JsonSerializer<ArrayList<DisplayUnit>>,
        JsonDeserializer<ArrayList<DisplayUnit>> {

    private HashMap<String, Class<? extends DisplayUnit>> displayTypes;

    public Class<? extends DisplayUnit> getDisplayType(String type) {
        return displayTypes.get(type);
    }

    public DisplayUnit createDisplay(String type, JsonObject jsonObject) {
        Class<? extends DisplayUnit> displayType = getDisplayType(type);
        if (displayType != null) {
            try {
                DisplayUnit displayTicker = displayType.newInstance();
                return displayTicker;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            Log.log().warning("Type [%s] is not defined. Cannot create display.", type);
        }
        return null;
    }

    public DisplayUnitFactory() {
        // TODO Add display types
    }

    @Override
    public JsonElement serialize(ArrayList<DisplayUnit> object, Type type, JsonSerializationContext context) {
        JsonObject endObject = new JsonObject();
        for (DisplayUnit displayUnit : object) {
            JsonObject displayObject = new JsonObject();
            displayUnit.saveCustomData(displayObject);
            endObject.add(displayUnit.getType(), displayObject);
        }
        return endObject;
    }

    @Override
    public ArrayList<DisplayUnit> deserialize(JsonElement element, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        ArrayList<DisplayUnit> displayUnits = new ArrayList<DisplayUnit>();
        JsonObject object = element.getAsJsonObject();
        for (Entry<String, JsonElement> entry : object.entrySet()) {
            String typeId = entry.getKey();
            JsonElement elementValue = entry.getValue();
            if (typeId != null && elementValue != null && elementValue.isJsonObject()) {
                DisplayUnit displayUnit = createDisplay(typeId, elementValue.getAsJsonObject());
                if (displayUnit != null) {
                    displayUnits.add(displayUnit);
                }
            }
        }
        return displayUnits;
    }
}
