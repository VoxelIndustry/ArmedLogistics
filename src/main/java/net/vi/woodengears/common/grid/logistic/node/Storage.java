package net.vi.woodengears.common.grid.logistic.node;

public interface Storage<T> extends Provider<T>
{
    int insertablePart(T content);

    T insert(T value);
}
