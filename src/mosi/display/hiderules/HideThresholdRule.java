package mosi.display.hiderules;

import mosi.Log;


public class HideThresholdRule implements HideRule {

    private Operator operator;
    private boolean isInverted;
    private int countThreshold;
    private boolean beAbove;

    public HideThresholdRule(int countThreshold, boolean beAbove, boolean isInverted, Operator operator) {
        this.operator = operator;
        this.countThreshold = countThreshold;
        this.beAbove = beAbove;
        this.isInverted = isInverted;
    }

    @Override
    public void update(Integer count, Integer prevCount) {
    }

    @Override
    public boolean shouldHide(int count) {
        Log.log().info("Hmm %s / %s while %s", count, countThreshold, isInverted);
        boolean shoudlHide = beAbove ? count >= countThreshold : count < countThreshold;
        return !isInverted ? shoudlHide : !shoudlHide;
    }

    @Override
    public Operator getOperator() {
        return operator;
    }
}
