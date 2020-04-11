package net.voxelindustry.armedlogistics.common.grid.logistic;

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

    @Override
    public List<ItemStack> accumulateList(List<ItemStack> first, List<ItemStack> second)
    {
        List<ItemStack> newList = new ArrayList<>();

        first.forEach(stack -> newList.add(stack.copy()));

        second.forEach(stack ->
                this.pushStackToList(newList, stack, stack));

        return newList;
    }

    public void pushStackToList(List<ItemStack> stacks, ItemStack stack, ItemStack toAdd)
    {
        Optional<ItemStack> alreadyExtracted =
                stacks.stream().filter(candidate -> ItemUtils.deepEquals(candidate, stack)).findFirst();
        if (alreadyExtracted.isPresent())
            alreadyExtracted.get().grow(toAdd.getCount());
        else
            stacks.add(toAdd);
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
