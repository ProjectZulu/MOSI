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

    //TODO: Change to event thats called during postinit or so, ensures Types cannot be added/removed while running
    public boolean addDisplayType(Class<? extends DisplayUnit> displayType) {
        if (displayType == null) {
            throw new IllegalArgumentException("Cannot register null DisplayType");
        }

        DisplayUnit displayUnit;
        try {
            displayUnit = displayType.newInstance();
        } catch (InstantiationException e) {
            Log.log().warning("Type [%s] cannot be registered because it cannot be instantiated.", displayType);
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
            Log.log().warning("Type [%s] cannot be registered because it cannot be instantiated.", displayType);
            e.printStackTrace();
            return false;
        }

        if (!displayTypes.containsKey(displayUnit.getType())) {

            return true;
        } else {
            Log.log().warning("Type [%s] cannot be registered because TypeId [%s] is already taken.", displayType,
                    displayUnit.getType());
            return false;
        }
    }

    public DisplayUnit createDisplay(String type, JsonObject jsonObject) {
        Class<? extends DisplayUnit> displayType = getDisplayType(type);
        DisplayUnit displayTicker = null;
        if (displayType != null) {
            try {
                displayTicker = displayType.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            Log.log().warning("Type [%s] is not defined. Cannot create display.", type);
        }
        return displayTicker;
    }

    public DisplayUnitFactory() {
        displayTypes = new HashMap<String, Class<? extends DisplayUnit>>();
        // TODO Add display types
        addDisplayType(DisplayUnitItem.class);
        addDisplayType(DisplayUnitPotion.class);
        addDisplayType(DisplayUnitUnsortedPanel.class);
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
