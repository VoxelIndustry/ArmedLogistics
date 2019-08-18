package net.vi.woodengears.common.grid.logistic.node;

import net.minecraft.item.EnumDyeColor;
import net.vi.woodengears.common.grid.logistic.ColoredStack;

import java.util.List;

public interface ColoredProvider<T> extends Provider<T>, ColoredLogisticNode<T>
{
    @Override
    default boolean isColored()
    {
        return true;
    }

    boolean hasColor(EnumDyeColor color);

    List<T> getValuesFromColor(EnumDyeColor color);

    boolean contains(ColoredStack coloredStack);

    int containedPart(ColoredStack coloredStack);

    List<T> extract(ColoredStack coloredStack);
}
