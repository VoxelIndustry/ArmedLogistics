package net.vi.woodengears.common.grid.logistic;

import lombok.Getter;
import net.minecraft.util.math.BlockPos;

public class ColoredShipment<T> extends LogisticShipment<ColoredStack>
{
    @Getter
    private T rawContent;

    public ColoredShipment(BlockPos from, BlockPos to, ColoredStack content, T rawContent)
    {
        super(from, to, content);

        this.rawContent = rawContent;
    }
}
