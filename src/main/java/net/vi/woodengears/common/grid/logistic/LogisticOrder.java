package net.vi.woodengears.common.grid.logistic;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LogisticOrder<T>
{
    private T                         ordered;
    @Setter
    private OrderState                state;
    private BlockPos                  destination;
    private List<LogisticShipment<T>> shippedParts;

    protected LogisticOrder(T ordered, BlockPos destination)
    {
        this.ordered = ordered;
        this.destination = destination;

        this.shippedParts = new ArrayList<>();
    }
}
