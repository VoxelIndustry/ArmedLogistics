package net.voxelindustry.armedlogistics.common.tile;

import lombok.Getter;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.steamlayer.inventory.InventoryHandler;
import net.voxelindustry.steamlayer.tile.TileBase;

@Getter
public abstract class TileInventoryBase extends TileBase
{
    private final String           name;
    private final InventoryHandler inventory;

    public TileInventoryBase(String name, int size)
    {
        this.name = name;
        this.inventory = new InventoryHandler(size);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setTag("inv", this.inventory.serializeNBT());

        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.inventory.deserializeNBT(tag.getCompoundTag("inv"));
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new TextComponentTranslation(ArmedLogistics.MODID + ".gui." + this.getName() + ".name");
    }

    public void dropInventory()
    {
        for (int slot = 0; slot < this.inventory.getSlots(); slot++)
        {
            if (!canDropSlot(slot))
                continue;
            InventoryHelper.spawnItemStack(this.world, this.getPos().getX(), this.getPos().getY(),
                    this.getPos().getZ(), inventory.getStackInSlot(slot));
        }
    }

    public boolean canDropSlot(int slot)
    {
        return true;
    }
}
