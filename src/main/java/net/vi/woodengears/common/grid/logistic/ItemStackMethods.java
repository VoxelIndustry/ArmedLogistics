package net.vi.woodengears.common.grid.logistic;

import net.minecraft.item.ItemStack;
import net.voxelindustry.steamlayer.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemStackMethods implements LogisticGridFunctions<ItemStack>
{
    private static ItemStackMethods INSTANCE;

    public static ItemStackMethods getInstance()
    {
        if (INSTANCE == null)
            INSTANCE = new ItemStackMethods();
        return INSTANCE;
    }

    private ItemStackMethods()
    {

    }

    public List<ItemStack> accumulateList(List<ItemStack> first, List<ItemStack> second)
    {
        List<ItemStack> newList = new ArrayList<>();

        first.forEach(stack -> newList.add(stack.copy()));

        second.forEach(stack ->
        {
            Optional<ItemStack> found =
                    newList.stream().filter(candidate -> ItemUtils.deepEquals(candidate, stack)).findFirst();
            if (found.isPresent())
                found.get().grow(stack.getCount());
            else
                newList.add(stack);
        });

        return newList;
    }

    @Override
    public int getQuantity(ItemStack type)
    {
        return type.getCount();
    }

    @Override
    public ItemStack changeQuantity(ItemStack type, int quantity)
    {
        ItemStack copy = type.copy();
        copy.setCount(quantity);
        return copy;
    }
}
