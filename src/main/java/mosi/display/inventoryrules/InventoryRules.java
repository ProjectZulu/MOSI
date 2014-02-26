package mosi.display.inventoryrules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Convenience object Object containing list of rules that handles saving/to from a JSONObject
 */
public class InventoryRules implements Iterable<InventoryRule> {
    private final static String RULES_KEY = "INVENTORYRULES";
    private final static String TYPE_KEY = "TYPE";
    private final static String HAND_KEY = "HAND";
    private final static String ID_KEY = "ItemIdMatch";
    private final static String META_KEY = "ItemMetaMatch";
    private final static String SLOT_KEY = "ItemSlotMatch";

    private List<InventoryRule> rules;

    public InventoryRules() {
        rules = new ArrayList<InventoryRule>();
    }

    public List<InventoryRule> get() {
        return rules;
    }

    public void size() {
        rules.size();
    }

    public InventoryRules(List<InventoryRule> rules) {
        rules = new ArrayList<InventoryRule>(rules);
    }

    public void saveCustomData(JsonObject jsonObject) {
        JsonArray rulesArray = new JsonArray();
        for (InventoryRule rule : rules) {
            JsonObject ruleObject = new JsonObject();
            if (rule instanceof ItemHandMatch) {
                ItemHandMatch realRule = (ItemHandMatch) rule;
                ruleObject.addProperty(TYPE_KEY, HAND_KEY);
            } else if (rule instanceof ItemIdMatch) {
                ItemIdMatch realRule = (ItemIdMatch) rule;
                ruleObject.addProperty(TYPE_KEY, ID_KEY);
                ruleObject.addProperty("ITEMID", realRule.itemId);
                ruleObject.addProperty("MULTIPLEMATCHES", realRule.multipleMatches);
            } else if (rule instanceof ItemMetaMatch) {
                ItemMetaMatch realRule = (ItemMetaMatch) rule;
                ruleObject.addProperty(TYPE_KEY, META_KEY);
                ruleObject.addProperty("ITEMID", realRule.itemId);
                ruleObject.addProperty("DAMAGEMIN", realRule.getMinItemDamage());
                ruleObject.addProperty("DAMAGEMAX", realRule.getMaxItemDamage());
                ruleObject.addProperty("MULTIPLEMATCHES", realRule.multipleMatches);
            } else if (rule instanceof ItemSlotMatch) {
                ItemSlotMatch realRule = (ItemSlotMatch) rule;
                ruleObject.addProperty(TYPE_KEY, SLOT_KEY);
                ruleObject.addProperty("SLOTID", realRule.getSlotId());
                ruleObject.addProperty("ISARMORSLOT", realRule.armorSlot);
            } else {
                throw new IllegalStateException("Unknown InventoryRule detected. Support must be added for saving.");
            }
            rulesArray.add(ruleObject);
        }
        jsonObject.add(RULES_KEY, rulesArray);
    }

    public void loadCustomData(JsonObject customData) {
        List<InventoryRule> rules = new ArrayList<InventoryRule>();
        JsonArray array = customData.get(RULES_KEY).getAsJsonArray();
        for (JsonElement jsonElement : array) {
            JsonObject ruleObject = jsonElement.getAsJsonObject();
            String ruleType = ruleObject.get(TYPE_KEY).getAsString();
            if (HAND_KEY.equals(ruleType)) {
                rules.add(new ItemHandMatch());
            } else if (ID_KEY.equals(ruleType)) {
                String itemId = ruleObject.get("ITEMID").getAsString();
                boolean multipleMatches = ruleObject.get("MULTIPLEMATCHES").getAsBoolean();
                rules.add(new ItemIdMatch(itemId, multipleMatches));
            } else if (META_KEY.equals(ruleType)) {
                String itemId = ruleObject.get("ITEMID").getAsString();
                boolean multipleMatches = ruleObject.get("MULTIPLEMATCHES").getAsBoolean();
                int minItemDamage = ruleObject.get("DAMAGEMIN").getAsInt();
                int maxItemDamage = ruleObject.get("DAMAGEMAX").getAsInt();
                rules.add(new ItemMetaMatch(itemId, minItemDamage, maxItemDamage, multipleMatches));
            } else if (SLOT_KEY.equals(ruleType)) {
                boolean armorSlot = ruleObject.get("ISARMORSLOT").getAsBoolean();
                int slotId = ruleObject.get("SLOTID").getAsInt();
                rules.add(new ItemSlotMatch(slotId, armorSlot));
            } else {
                if (ruleType == null) {
                    throw new IllegalStateException("InventoryRule type absent when loading.");
                } else {
                    throw new IllegalStateException("Unknown Rule type on loading. Type " + ruleType + " is not known");
                }
            }

        }
    }

    @Override
    public Iterator<InventoryRule> iterator() {
        return rules.iterator();
    }

    public void add(InventoryRule element) {
        rules.add(element);
    }

    public void remove(InventoryRule element) {
        rules.remove(element);
    }
}
