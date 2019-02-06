package net.vi.woodengears.common.grid.logistic.node;

import java.util.List;
import java.util.function.Predicate;

public interface Provider<T> extends LogisticNode
{
    /**
     * Query the provider with the specified value and return the quantity currently available.
     * Will be 0 if none is found or the value is invalid.
     * Will never return more than the quantity specified in the value.
     *
     * @param value
     * @return
     */
    int containedPart(T value);

    boolean contains(T value);

    boolean anyMatch(Predicate<T> matcher);

    T firstMatching(Predicate<T> matcher);

    List<T> allMatching(Predicate<T> matcher);

    /**
     * Remove the value from the provider and place it in a buffer for later retrieval by the grid.
     * A provider do not expose the buffer values for contains or matchers checks.
     * @param value
     * @return the value extracted. The quantity may be less than asked.
     */
    T extract(T value);

    T fromBuffer(T value);

    boolean isBufferFull();

    List<T> getCompressedContents();

    boolean isColored();
}
