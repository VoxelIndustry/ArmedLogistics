package net.vi.woodengears.common.grid.logistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.math.BlockPos;

@Getter
@AllArgsConstructor
public class LogisticShipment<T>
{
    private BlockPos from;
    private BlockPos to;

    private T content;
}
