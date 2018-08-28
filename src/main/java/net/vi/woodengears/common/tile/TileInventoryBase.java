package net.vi.woodengears.common.tile;

import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.vi.woodengears.WoodenGears;
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

    public ITextComponent getDisplayName()
    {
        return new TextComponentTranslation(WoodenGears.MODID + ".gui." + this.getName() + ".name");
    }
}
