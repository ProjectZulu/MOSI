package mosi.display.inventoryrules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mosi.display.units.DisplayUnitItem.DisplayStats;
import mosi.display.units.DisplayUnitItem.TrackMode;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import com.google.gson.JsonObject;

/**
 * Convenience object Object containing list of rules that handles saving/to from a JSONObject
 */
public class InventoryRules implements Iterable<InventoryRule> {
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

    }

    public void loadCustomData(JsonObject customData) {

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
