package mosi.display;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DisplayUnitFactory implements JsonSerializer<ArrayList<DisplayUnit>>,
        JsonDeserializer<ArrayList<DisplayUnit>> {

    private HashMap<String, DisplayCreator> displayTypes;

    public DisplayCreator getDisplayType(String type) {
        return displayTypes.get(type);
    }

    /**
     * Wrapper class around Dummy Display units used for creation to ensure they aren't used for anything else
     * 
     * This allows createFromCustomData to be declared as part of the instance for each DisplayUnit along with
     * saveCustomData while keeping it seperate from reading/writing
     */
    public static class DisplayCreator {
        /* Display Unit this Creator creates */
        private DisplayUnit displayUnit;

        public DisplayCreator(DisplayUnit displayUnit) {
            this.displayUnit = displayUnit;
        }

        public DisplayUnit createFromCustomData(DisplayUnitFactory factory, JsonObject customData) {
            return displayUnit.createFromCustomData(factory, customData);
        }
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
            DisplayCreator displayType = getDisplayType(typeId);
            if (typeId != null && elementValue != null && elementValue.isJsonObject() && displayType != null) {
                DisplayUnit displayUnit = displayType.createFromCustomData(this, elementValue.getAsJsonObject());
                if (displayUnit != null) {
                    displayUnits.add(displayUnit);
                }
            }
        }
        return displayUnits;
    }
}
