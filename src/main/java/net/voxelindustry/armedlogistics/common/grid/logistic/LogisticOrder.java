package net.voxelindustry.armedlogistics.common.grid.logistic;

import lombok.Getter;
import lombok.Setter;
import net.voxelindustry.armedlogistics.common.grid.logistic.node.LogisticNode;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LogisticOrder<T>
{
    private T                         content;
    @Setter
    private OrderState                state;
    private LogisticNode<T>           source;
    private List<LogisticShipment<T>> shippedParts;

    protected LogisticOrder(T content, LogisticNode<T> source)
    {
        this.content = content;
        this.source = source;

        shippedParts = new ArrayList<>();
    }
}
