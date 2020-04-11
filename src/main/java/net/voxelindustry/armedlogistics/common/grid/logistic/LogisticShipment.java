package net.voxelindustry.armedlogistics.common.grid.logistic;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

@Getter
public class LogisticShipment<T>
{
    private BlockPos from;
    private BlockPos to;

    @Setter
    private UUID armID;

    private T content;

    public LogisticShipment(BlockPos from, BlockPos to, T content)
    {
        this.from = from;
        this.to = to;
        this.content = content;
    }
}
