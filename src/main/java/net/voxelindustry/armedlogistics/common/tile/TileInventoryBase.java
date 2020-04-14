package net.voxelindustry.armedlogistics.common.tile;

import lombok.Getter;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.voxelindustry.steamlayer.inventory.InventoryHandler;
import net.voxelindustry.steamlayer.tile.TileBase;

@Getter
public abstract class TileInventoryBase extends TileBase
{
    private final InventoryHandler inventory;

    public TileInventoryBase(TileEntityType<? extends TileInventoryBase> type, String name, int size)
    {
        super(type);

        inventory = new InventoryHandler(size);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag)
    {
        super.write(tag);

        tag.put("inv", inventory.serializeNBT());

        return tag;
    }

    @Override
    public void read(CompoundNBT tag)
    {
        super.read(tag);

        inventory.deserializeNBT(tag.getCompound("inv"));
    }

    public void dropInventory()
    {
        for (int slot = 0; slot < inventory.getSlots(); slot++)
        {
            if (!canDropSlot(slot))
                continue;
            InventoryHelper.spawnItemStack(world, getPos().getX(), getPos().getY(),
                    getPos().getZ(), inventory.getStackInSlot(slot));
        }
    }

    public boolean canDropSlot(int slot)
    {
        return true;
    }
}
