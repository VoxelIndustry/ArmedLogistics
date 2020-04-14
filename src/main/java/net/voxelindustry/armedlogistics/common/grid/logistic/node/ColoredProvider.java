package net.voxelindustry.armedlogistics.common.grid.logistic.node;

import net.minecraft.item.DyeColor;
import net.voxelindustry.armedlogistics.common.grid.logistic.ColoredStack;

import java.util.List;

public interface ColoredProvider<T> extends Provider<T>, ColoredLogisticNode<T>
{
    @Override
    default boolean isColored()
    {
        return true;
    }

    boolean hasColor(DyeColor color);

    List<T> getValuesFromColor(DyeColor color);

    boolean contains(ColoredStack coloredStack);

    int containedPart(ColoredStack coloredStack);

    List<T> extract(ColoredStack coloredStack);
}
