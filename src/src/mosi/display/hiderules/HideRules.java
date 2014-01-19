package mosi.display.hiderules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import mosi.display.DisplayUnitItem.DisplayStats;
import mosi.display.hiderules.HideRule.Operator;

import com.google.gson.JsonObject;

public class HideRules implements Iterable<HideRule> {
    private List<HideRule> rules;

    public HideRules() {
        rules = new ArrayList<HideRule>();
    }

    public HideRules(Collection<HideRule> rules) {
        rules = new ArrayList<HideRule>(rules);
    }

    public boolean addRule(HideRule rule) {
        return rules.add(rule);
    }

    public void removeRule(int index) {
        rules.remove(index);
    }

    public void size() {
        rules.size();
    }

    public void saveCustomData(JsonObject jsonObject) {

    }

    public void loadCustomData(JsonObject customData) {

    }

    public void update(DisplayStats currentDisplay, DisplayStats prevDisplay) {
        for (HideRule rule : rules) {
            rule.update(currentDisplay != null ? currentDisplay.trackedCount : null,
                    prevDisplay != null ? prevDisplay.trackedCount : null);
        }
    }

    public boolean shouldHide(DisplayStats currentDisplay) {
        boolean shouldHide = false;
        Boolean prevOutcome = null;
        for (int i = 0; i < rules.size(); i++) {
            HideRule rule = rules.get(i);
            if (prevOutcome != null) {
                if (rule.getOperator() == Operator.OR && prevOutcome == true) {
                    shouldHide = true;
                }
                if (rule.getOperator() == Operator.AND && prevOutcome == false) {
                    prevOutcome = false;
                } else {
                    prevOutcome = rule.shouldHide(currentDisplay.trackedCount);
                    // If Last rule set shouldHide evaluation
                    if (i == rules.size() - 1 && prevOutcome) {
                        shouldHide = true;
                    }
                }
            } else {
                prevOutcome = rule.shouldHide(currentDisplay.trackedCount);
                // If Last rule set shouldHide evaluation
                if (i == rules.size() - 1 && prevOutcome) {
                    shouldHide = true;
                }
            }
        }
        return shouldHide;
    }

    @Override
    public Iterator<HideRule> iterator() {
        return rules.iterator();
    }
}
