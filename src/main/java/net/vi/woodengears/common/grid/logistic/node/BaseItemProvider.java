package net.vi.woodengears.common.grid.logistic.node;

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
    private InventoryBuffer        buffer;
    private NonNullList<ItemStack> handlerMirror;
    private NonNullList<ItemStack> compressedStacks;

    private TileLogicisticNode tile;

    public BaseItemProvider(TileLogicisticNode tile, InventoryHandler handler, InventoryBuffer buffer)
    {
        this.tile = tile;
        this.handler = handler;
        this.buffer = buffer;

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
    public int containedPart(ItemStack value)
    {
        if (value.isEmpty())
            return 0;

        this.wake();

        int quantity = 0;
        for (ItemStack stack : this.compressedStacks)
        {
            if (stack.isEmpty())
                continue;
            if (ItemUtils.deepEquals(stack, value))
            {
                quantity += stack.getCount();

                if (quantity >= value.getCount())
                    return value.getCount();
            }
        }

        return quantity;
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
        if (buffer.isFull())
            return ItemStack.EMPTY;

        if (this.containedPart(value) == 0)
            return ItemStack.EMPTY;

        this.sleep();

        int extracted = 0;

        for (int i = 0; i < this.handler.getSlots(); i++)
        {
            ItemStack stack = this.handler.getStackInSlot(i);

            if (ItemUtils.deepEquals(stack, value))
            {
                int toExtract = Math.min(value.getCount() - extracted, stack.getCount());

                stack.shrink(toExtract);
                extracted += toExtract;

                handler.setStackInSlot(i, stack);

                if (extracted == value.getCount())
                    break;
            }
        }

        ItemStack copy = value.copy();
        copy.setCount(extracted);
        return buffer.add(copy);
    }

    @Override
    public ItemStack fromBuffer(ItemStack value)
    {
        buffer.remove(value);
        return value;
    }

    @Override
    public boolean isBufferFull()
    {
        return buffer.isFull();
    }

    @Override
    public NonNullList<ItemStack> getCompressedContents()
    {
        return this.compressedStacks;
    }

    @Override
    public boolean isColored()
    {
        return false;
    }

    protected InventoryHandler getHandler()
    {
        return this.handler;
    }

    protected InventoryBuffer getBuffer()
    {
        return this.buffer;
    }
}
