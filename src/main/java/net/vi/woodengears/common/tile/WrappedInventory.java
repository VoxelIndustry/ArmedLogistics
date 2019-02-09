package net.vi.woodengears.common.tile;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class WrappedInventory implements IItemHandler
{
    private IItemHandler internalInv;

    public WrappedInventory()
    {

    }

    public void setWrapped(IItemHandler inv)
    {
        this.internalInv = inv;
    }

    @Override
    public int getSlots()
    {
        if (this.internalInv == null)
            return 0;
        return this.internalInv.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot)
    {
        if (this.internalInv == null)
            return ItemStack.EMPTY;
        return this.internalInv.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (this.internalInv == null)
            return stack;
        return this.internalInv.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (this.internalInv == null)
            return ItemStack.EMPTY;
        return this.internalInv.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        if (this.internalInv == null)
            return 0;
        return this.internalInv.getSlotLimit(slot);
    }
}
