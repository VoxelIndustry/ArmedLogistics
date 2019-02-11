package net.vi.woodengears.common.grid.logistic.node;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.vi.woodengears.common.grid.logistic.ProviderType;
import net.vi.woodengears.common.tile.TileLogicisticNode;
import net.voxelindustry.steamlayer.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class BaseItemProvider extends BaseLogisticNode implements Provider<ItemStack>
{
    @Getter(AccessLevel.PROTECTED)
    private IItemHandler           handler;
    @Getter(AccessLevel.PROTECTED)
    private InventoryBuffer        buffer;
    private NonNullList<ItemStack> handlerMirror;
    private NonNullList<ItemStack> compressedStacks;

    @Getter
    private ProviderType       providerType;
    private TileLogicisticNode tile;

    public BaseItemProvider(TileLogicisticNode tile, ProviderType type, IItemHandler handler,
                            InventoryBuffer buffer)
    {
        this.tile = tile;
        this.handler = handler;
        this.buffer = buffer;

        this.providerType = type;

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

        if (handlerMirror.size() != handler.getSlots())
        {
            isDirty = true;
            handlerMirror = NonNullList.withSize(handler.getSlots(), ItemStack.EMPTY);
        }

        for (int i = 0; i < handler.getSlots(); i++)
        {
            ItemStack stack = handler.getStackInSlot(i);

            if (!ItemUtils.deepEqualsWithAmount(stack, handlerMirror.get(i)))
            {
                isDirty = true;
                if (stack.isEmpty())
                    handlerMirror.set(i, ItemStack.EMPTY);
                else
                    handlerMirror.set(i, stack.copy());
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
                ItemStack extractedStack = stack.copy();
                extractedStack.setCount(value.getCount() - extracted);

                extractedStack = handler.extractItem(i, extractedStack.getCount(), true);

                extracted += extractedStack.getCount();

                handler.extractItem(i, extractedStack.getCount(), false);

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
}
