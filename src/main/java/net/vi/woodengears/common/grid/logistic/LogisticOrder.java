package net.vi.woodengears.common.grid.logistic;

import lombok.Getter;
import lombok.Setter;
import net.vi.woodengears.common.grid.logistic.node.Requester;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LogisticOrder<T>
{
    private T                         ordered;
    @Setter
    private OrderState                state;
    private Requester<T>              destination;
    private List<LogisticShipment<T>> shippedParts;

    protected LogisticOrder(T ordered, Requester<T> destination)
    {
        this.ordered = ordered;
        this.destination = destination;

        this.shippedParts = new ArrayList<>();
    }
}
