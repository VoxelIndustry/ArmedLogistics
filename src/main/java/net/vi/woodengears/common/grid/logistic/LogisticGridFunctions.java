package net.vi.woodengears.common.grid.logistic;

import java.util.List;

public interface LogisticGridFunctions<T>
{
    List<T> accumulateList(List<T> first, List<T> second);

    int getQuantity(T type);

    T changeQuantity(T type, int quantity);
}
