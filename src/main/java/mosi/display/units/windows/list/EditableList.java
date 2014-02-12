package mosi.display.units.windows.list;

public interface EditableList<T> extends Iterable<T> {
    public abstract void add(T element);

    public abstract void remove(T element);
}
