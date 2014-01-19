package mosi.display.hiderules;


public class HideUnchangedRule implements HideRule {

    private Operator operator;
    private boolean isInverted;
    private int ticksToHideUnchanged;
    private int ticksUnchanged = 0;

    public HideUnchangedRule(int ticksToHideUnchanged, boolean isInverted, Operator operator) {
        this.operator = operator;
        this.isInverted = isInverted;
        this.ticksToHideUnchanged = ticksToHideUnchanged;
    }

    @Override
    public void update(Integer count, Integer prevCount) {
        if (count != null && prevCount != null && count.equals(prevCount)) {
            ticksUnchanged++;
        } else {
            ticksUnchanged = 0;
        }
    }

    @Override
    public boolean shouldHide(int count) {
        return !isInverted ? ticksUnchanged >= ticksToHideUnchanged : ticksUnchanged <= ticksToHideUnchanged;
    }

    @Override
    public Operator getOperator() {
        return operator;
    }
}
