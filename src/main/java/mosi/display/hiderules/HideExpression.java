package mosi.display.hiderules;

import mosi.Log;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import com.google.gson.JsonObject;

public class HideExpression {
    private Evaluator evaluator;
    private String hideExpression;
    private int ticksUnchanged = 0;
    private boolean shouldHide = false;

    public boolean shouldHide() {
        return shouldHide;
    }

    public HideExpression() {
        setExpression("");
        shouldHide = false;
    }

    public HideExpression setExpression(String expression) {
        if (expression == null) {
            expression = "";
        }
        evaluator = new Evaluator();
        try {
            evaluator.parse(expression);
        } catch (EvaluationException e) {
            throw new IllegalArgumentException("Invalid expression statement");
        }
        this.hideExpression = expression;
        return this;
    }

    public void update(Integer trackedCount, Integer prevTrackedCount, int ticksPerUpdate) {
        ticksUnchanged = trackedCount.equals(prevTrackedCount) ? ticksUnchanged + ticksPerUpdate : 0;
        evaluator.putVariable("count", Integer.toString(trackedCount));
        evaluator.putVariable("unchanged", Integer.toString(ticksUnchanged));

        try {
            shouldHide = "1.0".equals(evaluator.evaluate());
        } catch (EvaluationException e) {
            shouldHide = false;
            Log.log().severe("Failed to evaluate expression %s", evaluator.toString());
            e.printStackTrace();
        }
    }

    public void saveCustomData(JsonObject jsonObject) {

    }

    public void loadCustomData(JsonObject customData) {

    }
}
