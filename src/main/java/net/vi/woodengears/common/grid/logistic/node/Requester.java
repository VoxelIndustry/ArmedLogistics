package net.vi.woodengears.common.grid.logistic.node;

import net.vi.woodengears.common.grid.logistic.LogisticOrder;

import java.util.List;

public interface Requester<T> extends LogisticNode<T>
{
    int insert(T value);

    void addRequest(T value);

    RequesterMode getMode();

    List<LogisticOrder<T>> getCurrentOrders();
}
