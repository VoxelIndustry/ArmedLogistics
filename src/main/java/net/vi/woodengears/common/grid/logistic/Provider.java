package net.vi.woodengears.common.grid.logistic;

import java.util.List;
import java.util.function.Predicate;

public interface Provider<T> extends LogisticNode
{
    boolean contains(T value);

    boolean anyMatch(Predicate<T> matcher);

    T firstMatching(Predicate<T> matcher);

    List<T> allMatching(Predicate<T> matcher);

    T extract(T value);

    List<T> getCompressedContents();
}
