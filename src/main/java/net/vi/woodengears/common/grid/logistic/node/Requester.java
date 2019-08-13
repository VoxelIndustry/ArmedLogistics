package net.vi.woodengears.common.grid.logistic.node;

import net.vi.woodengears.common.grid.logistic.ColoredShipment;
import net.vi.woodengears.common.grid.logistic.LogisticOrder;
import net.vi.woodengears.common.grid.logistic.LogisticShipment;

import java.util.Collection;
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

    void addShipment(LogisticShipment<T> shipment);

    boolean removeShipment(LogisticShipment<T> shipment);

    Collection<LogisticShipment<T>> getShipments();

    void deliverShipment(LogisticShipment<T> shipment);

    void addColoredShipment(ColoredShipment<T> shipment);

    boolean removeColoredShipment(ColoredShipment<T> shipment);

    Collection<ColoredShipment<T>> getColoredShipments();

    void deliverShipment(ColoredShipment<T> shipment);
}
