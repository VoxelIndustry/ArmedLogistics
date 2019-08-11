package net.vi.woodengears.common.grid.logistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;

@Data
@AllArgsConstructor
public class ColoredStack
{
    private EnumDyeColor color;
    private int          quantity;

    public ColoredStack(NBTTagCompound tag)
    {
        this(EnumDyeColor.values()[tag.getInteger("color")], tag.getInteger("quantity"));
    }

    public NBTTagCompound toNBT(NBTTagCompound tag)
    {
        tag.setInteger("color", color.ordinal());
        tag.setInteger("quantity", quantity);
        return tag;
    }
}
