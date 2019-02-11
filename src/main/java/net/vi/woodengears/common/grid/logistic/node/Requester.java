package net.vi.woodengears.common.grid.logistic.node;

import net.vi.woodengears.common.grid.logistic.LogisticOrder;

import java.util.List;

public interface Requester<T> extends LogisticNode
{
    int insert(T value);

    boolean isBufferFull();

    void addRequest(T value);

    RequesterMode getMode();

    List<LogisticOrder<T>> getCurrentOrders();

    void addOrder(LogisticOrder<T> order);

    void removeOrder(LogisticOrder<T> order);
}
