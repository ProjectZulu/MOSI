package mosi.display.hiderules;

public interface HideRule {
    public abstract void update(Integer count, Integer prevCount);

    public abstract boolean shouldHide(int count);

    public static enum Operator {
        AND, OR;
    }

    public abstract Operator getOperator();
}
