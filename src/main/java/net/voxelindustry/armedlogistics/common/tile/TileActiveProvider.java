package net.voxelindustry.armedlogistics.common.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.voxelindustry.armedlogistics.common.grid.logistic.ProviderType;

public class TileActiveProvider extends TileProvider implements ITickable
{
    @Override
    protected ProviderType getProviderType()
    {
        return ProviderType.ACTIVE_PROVIDER;
    }

    @Override
    public void update()
    {
        if (isClient())
            return;

        if (world.getTotalWorldTime() % 32 != ((pos.getX() ^ pos.getZ()) & 31))
            return;
        randomTick();
    }

    public void randomTick()
    {
        if (getCable() == null || getCable().getGridObject() == null)
            return;

        NonNullList<ItemStack> compressedContents = getProvider().getCompressedContents();

        getProvider().wake();
        if (!compressedContents.isEmpty() && !compressedContents.get(0).isEmpty())
        {
            ItemStack toRemove = compressedContents.get(0);

            if (toRemove.getCount() > toRemove.getMaxStackSize())
            {
                toRemove = toRemove.copy();
                toRemove.setCount(toRemove.getMaxStackSize());
            }
            getCable().getGridObject().getStackNetwork().makeRemovalOrder(getProvider(), toRemove);
        }
        getProvider().sleep();
    }
}
