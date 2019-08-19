package net.vi.woodengears.common.grid.logistic.node;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.vi.woodengears.common.grid.logistic.LogisticNetwork;
import net.vi.woodengears.common.grid.logistic.ProviderType;
import net.vi.woodengears.common.tile.TileLogicisticNode;

import java.util.function.Supplier;

public class BaseItemStorage extends BaseItemProvider implements Storage<ItemStack>
{
    public BaseItemStorage(TileLogicisticNode tile,
                           ProviderType type,
                           IItemHandler handler,
                           Supplier<LogisticNetwork<ItemStack>> networkSupplier,
                           InventoryBuffer providerBuffer)
    {
        super(tile, type, handler, networkSupplier, providerBuffer);
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
    public ItemStack insert(ItemStack stack)
    {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        sleep();

        ItemStack toInsert = stack.copy();
        for (int slot = 0; slot < getHandler().getSlots(); slot++)
        {
            ItemStack remainder = getHandler().insertItem(slot, toInsert, true);

            if (remainder.getCount() == stack.getCount())
                continue;

            ItemStack insert = toInsert.copy();
            insert.shrink(remainder.getCount());
            getHandler().insertItem(slot, insert, false);

            toInsert.shrink(insert.getCount());

            if (toInsert.isEmpty())
                break;
        }

        ItemStack inserted = stack.copy();
        stack.shrink(toInsert.getCount());

        return inserted;
    }
}
