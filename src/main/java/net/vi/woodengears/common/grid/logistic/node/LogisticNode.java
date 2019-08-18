package net.vi.woodengears.common.grid.logistic.node;

import net.minecraft.util.math.BlockPos;
import net.vi.woodengears.common.grid.logistic.LogisticOrder;
import net.vi.woodengears.common.grid.logistic.LogisticShipment;

import java.util.Collection;

public interface LogisticNode<T>
{
    void wake();

    void sleep();

    boolean isAwake();

    void networkTick();

    BlockPos getRailPos();

    ////////////////////////
    // ORDER AND DELIVERY //
    ////////////////////////

    void addOrder(LogisticOrder<T> order);

    void removeOrder(LogisticOrder<T> order);

    void addShipment(LogisticShipment<T> shipment);

    boolean removeShipment(LogisticShipment<T> shipment);

    Collection<LogisticShipment<T>> getShipments();

    void deliverShipment(LogisticShipment<T> shipment);
}
