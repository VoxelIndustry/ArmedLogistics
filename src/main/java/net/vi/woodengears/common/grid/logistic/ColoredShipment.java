package net.vi.woodengears.common.grid.logistic;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ColoredShipment extends LogisticShipment<ColoredStack>
{
    @Getter
    private ItemStack internalStack;

    public ColoredShipment(BlockPos from, BlockPos to, ColoredStack content, ItemStack internalStack)
    {
        super(from, to, content);

        this.internalStack = internalStack;
    }
}
