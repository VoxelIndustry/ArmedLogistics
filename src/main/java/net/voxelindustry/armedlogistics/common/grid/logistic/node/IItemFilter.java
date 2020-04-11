package net.voxelindustry.armedlogistics.common.grid.logistic.node;

import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

@FunctionalInterface
public interface IItemFilter extends Predicate<ItemStack>
{
    IItemFilter ALWAYS_TRUE = stack -> true;

    @Override
    default boolean test(ItemStack stack)
    {
        return this.filter(stack);
    }

    boolean filter(ItemStack stack);
}
