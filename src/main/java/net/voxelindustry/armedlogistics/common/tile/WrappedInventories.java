package net.voxelindustry.armedlogistics.common.tile;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static net.minecraft.item.ItemStack.EMPTY;

public class WrappedInventories implements IItemHandler
{
    private List<IItemHandler> inventories = new ArrayList<>(6);

    public WrappedInventories()
    {
    }

    public void setWrappeds(List<IItemHandler> inventories)
    {
        this.inventories = inventories;
    }

    public void addWrapped(@Nonnull IItemHandler inv)
    {
        requireNonNull(inv);
        inventories.add(inv);
    }

    public boolean removeWrapped(IItemHandler inv)
    {
        return inventories.remove(inv);
    }

    public void setWrapped(@Nonnull IItemHandler inv, int index)
    {
        requireNonNull(inv);
        inventories.set(index, inv);
    }

    @Override
    public int getSlots()
    {
        if (inventories.isEmpty())
            return 0;

        int slots = 0;
        for (IItemHandler inventory : inventories)
            slots += inventory.getSlots();
        return slots;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot)
    {
        if (inventories.isEmpty())
            return EMPTY;

        int slots = 0;
        for (IItemHandler inventory : inventories)
        {
            if (slots + inventory.getSlots() > slot)
                return inventory.getStackInSlot(slot - slots);
            slots += inventory.getSlots();
        }
        return EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (inventories.isEmpty())
            return stack;

        int slots = 0;
        for (IItemHandler inventory : inventories)
        {
            if (slots + inventory.getSlots() > slot)
                return inventory.insertItem(slot - slots, stack, simulate);
            slots += inventory.getSlots();
        }
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (inventories.isEmpty())
            return EMPTY;

        int slots = 0;
        for (IItemHandler inventory : inventories)
        {
            if (slots + inventory.getSlots() > slot)
                return inventory.extractItem(slot - slots, amount, simulate);
            slots += inventory.getSlots();
        }
        return EMPTY;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        if (inventories.isEmpty())
            return 0;

        int slots = 0;
        for (IItemHandler inventory : inventories)
        {
            if (slots + inventory.getSlots() > slot)
                return inventory.getSlotLimit(slot - slots);
            slots += inventory.getSlots();
        }
        return 0;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        if (inventories.isEmpty())
            return false;

        int slots = 0;
        for (IItemHandler inventory : inventories)
        {
            if (slots + inventory.getSlots() > slot)
                return inventory.isItemValid(slot, stack);
            slots += inventory.getSlots();
        }
        return false;
    }
}
