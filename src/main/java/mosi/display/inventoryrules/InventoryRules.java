package mosi.display.inventoryrules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonObject;

/**
 * Convenience object Object containing list of rules that handles saving/to from a JSONObject
 */
public class InventoryRules implements Iterable<InventoryRule> {
    private List<InventoryRule> rules;

    public InventoryRules() {
        rules = new ArrayList<InventoryRule>();
    }

    public boolean addRule(InventoryRule rule) {
        return rules.add(rule);
    }

    public void removeRule(int index) {
        rules.remove(index);
    }

    public void size() {
        rules.size();
    }

    public InventoryRules(List<InventoryRule> rules) {
        rules = new ArrayList<InventoryRule>(rules);
    }

    public void saveCustomData(JsonObject jsonObject) {

    }

    public void loadCustomData(JsonObject customData) {

    }

    @Override
    public Iterator<InventoryRule> iterator() {
        return rules.iterator();
    }
}
