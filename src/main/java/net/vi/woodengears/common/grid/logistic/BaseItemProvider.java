package net.vi.woodengears.common.grid.logistic;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.vi.woodengears.common.tile.TileLogicisticNode;
import net.voxelindustry.steamlayer.inventory.InventoryHandler;
import net.voxelindustry.steamlayer.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class BaseItemProvider extends BaseLogisticNode implements Provider<ItemStack>
{
    private InventoryHandler       handler;
    private NonNullList<ItemStack> handlerMirror;
    private NonNullList<ItemStack> compressedStacks;

    private TileLogicisticNode tile;

    public BaseItemProvider(TileLogicisticNode tile, InventoryHandler handler)
    {
        this.tile = tile;
        this.handler = handler;

        handlerMirror = NonNullList.withSize(handler.getSlots(), ItemStack.EMPTY);
        compressedStacks = NonNullList.create();
    }

    @Override
    public BlockPos getRailPos()
    {
        return tile.getRailPos();
    }

    @Override
    public void wake()
    {
        if (this.isAwake())
            return;

        boolean isDirty = false;

        for (int i = 0; i < handler.getSlots(); i++)
        {
            ItemStack stack = handler.getStackInSlot(i);

            if (!ItemUtils.deepEqualsWithAmount(stack, handlerMirror.get(i)))
            {
                isDirty = true;
                handlerMirror.set(i, stack);
            }
        }

        if (isDirty)
        {
            this.compressedStacks.clear();

            for (int i = 0; i < handler.getSlots(); i++)
            {
                ItemStack stack = handler.getStackInSlot(i);

                Optional<ItemStack> found =
                        this.compressedStacks.stream().filter(candidate -> ItemUtils.deepEquals(candidate, stack)).findFirst();
                if (found.isPresent())
                    found.get().grow(stack.getCount());
                else
                    this.compressedStacks.add(stack.copy());
            }
        }

        super.wake();
    }

    @Override
    public boolean contains(ItemStack value)
    {
        if (value.isEmpty())
            return false;

        this.wake();

        for (ItemStack stack : this.compressedStacks)
        {
            if (stack.isEmpty())
                continue;
            if (ItemUtils.deepEquals(stack, value) && stack.getCount() >= value.getCount())
                return true;
        }
        return false;
    }

    @Override
    public boolean anyMatch(Predicate<ItemStack> matcher)
    {
        this.wake();

        for (ItemStack stack : this.compressedStacks)
        {
            if (matcher.test(stack))
                return true;
        }
        return false;
    }

    @Override
    public ItemStack firstMatching(Predicate<ItemStack> matcher)
    {
        this.wake();

        for (ItemStack stack : this.compressedStacks)
        {
            if (matcher.test(stack))
                return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public List<ItemStack> allMatching(Predicate<ItemStack> matcher)
    {
        this.wake();

        ArrayList<ItemStack> stacks = new ArrayList<>(handler.getSlots());

        for (ItemStack stack : this.compressedStacks)
        {
            if (matcher.test(stack))
                stacks.add(stack);
        }
        stacks.trimToSize();
        return stacks;
    }

    @Override
    public ItemStack extract(ItemStack value)
    {
        ItemStack stack =
                this.firstMatching(candidate -> ItemUtils.deepEquals(candidate, value) && candidate.getCount() >= value.getCount());

        if (stack.isEmpty())
            return ItemStack.EMPTY;

        this.sleep();

        int extracted = 0;

        for (int i = 0; i < this.handler.getSlots(); i++)
        {
            ItemStack current = this.handler.getStackInSlot(i);

            if (ItemUtils.deepEquals(current, stack))
            {
                int toExtract = Math.min(stack.getCount() - extracted, current.getCount());

                current.shrink(toExtract);
                extracted += toExtract;

                handler.setStackInSlot(i, current);

                if (extracted == stack.getCount())
                    break;
            }
        }

        return stack;
    }

    @Override
    public NonNullList<ItemStack> getCompressedContents()
    {
        return this.compressedStacks;
    }
}
