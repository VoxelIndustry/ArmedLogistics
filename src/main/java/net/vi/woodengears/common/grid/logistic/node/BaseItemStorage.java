package net.vi.woodengears.common.grid.logistic.node;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.vi.woodengears.common.grid.logistic.LogisticNetwork;
import net.vi.woodengears.common.grid.logistic.ProviderType;
import net.vi.woodengears.common.tile.TileLogicisticNode;

import java.util.function.Supplier;

public class BaseItemStorage extends BaseItemProvider implements Storage<ItemStack>
{
    @Getter(AccessLevel.PROTECTED)
    private InventoryBuffer storageBuffer;

    public BaseItemStorage(TileLogicisticNode tile,
                           ProviderType type,
                           IItemHandler handler,
                           Supplier<LogisticNetwork<ItemStack>> networkSupplier,
                           InventoryBuffer providerBuffer,
                           InventoryBuffer storageBuffer)
    {
        super(tile, type, handler, networkSupplier, providerBuffer);

        this.storageBuffer = storageBuffer;
    }

    @Override
    public int insertablePart(ItemStack content)
    {
        if (content.isEmpty())
            return 0;

        wake();

        int insertable = 0;
        for (int slot = 0; slot < getHandler().getSlots(); slot++)
        {
            ItemStack remainder = getHandler().insertItem(slot, content, true);

            if (remainder.getCount() == content.getCount())
                continue;

            insertable += content.getCount() - remainder.getCount();

            if (insertable >= content.getCount())
                return content.getCount();
        }
        return insertable;
    }

    @Override
    public ItemStack insert(ItemStack value)
    {
        emptyBuffer();
        if (storageBuffer.isFull())
            return ItemStack.EMPTY;

        sleep();

        ItemStack added = storageBuffer.add(value);

        emptyBuffer();
        return added;
    }

    public void emptyBuffer()
    {
        for (ItemStack stack : storageBuffer.getStacks())
        {
            if (stack.isEmpty())
                break;

            ItemStack toInsert = stack.copy();
            for (int slot = 0; slot < getHandler().getSlots(); slot++)
            {
                ItemStack remainder = getHandler().insertItem(slot, toInsert, true);

                if (remainder.getCount() == stack.getCount())
                    continue;

                remainder = getHandler().insertItem(slot, toInsert.copy(), false);

                toInsert.shrink(toInsert.getCount() - remainder.getCount());

                if (toInsert.isEmpty())
                    break;
            }

            ItemStack inserted = stack.copy();
            inserted.shrink(toInsert.getCount());
            storageBuffer.remove(inserted);
        }
    }
}
