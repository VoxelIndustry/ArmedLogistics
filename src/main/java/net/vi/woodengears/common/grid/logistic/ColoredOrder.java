package net.vi.woodengears.common.grid.logistic;

import lombok.Getter;
import lombok.Setter;
import net.vi.woodengears.common.grid.logistic.node.Requester;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ColoredOrder<T>
{
    private ColoredStack             ordered;
    @Setter
    private OrderState               state;
    private Requester<T>             destination;
    private List<ColoredShipment<T>> shippedParts;

    protected ColoredOrder(ColoredStack ordered, Requester<T> destination)
    {
        this.ordered = ordered;
        this.destination = destination;

        this.shippedParts = new ArrayList<>();
    }
}
