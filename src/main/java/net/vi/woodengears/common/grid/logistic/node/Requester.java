package net.vi.woodengears.common.grid.logistic.node;

public interface Requester<T> extends LogisticNode
{
    int insert(T value);

    boolean isBufferFull();
}
