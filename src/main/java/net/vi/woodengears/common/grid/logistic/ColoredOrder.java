package net.vi.woodengears.common.grid.logistic;

import lombok.Getter;
import lombok.Setter;
import net.vi.woodengears.common.grid.logistic.node.LogisticNode;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ColoredOrder<T>
{
    private ColoredStack             content;
    @Setter
    private OrderState               state;
    private LogisticNode<T>          source;
    private List<ColoredShipment<T>> shippedParts;

    protected ColoredOrder(ColoredStack content, LogisticNode<T> source)
    {
        this.content = content;
        this.source = source;

        shippedParts = new ArrayList<>();
    }
}
