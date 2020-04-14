package net.voxelindustry.armedlogistics.common.grid.logistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;

@Data
@AllArgsConstructor
public class ColoredStack
{
    private DyeColor color;
    private int      quantity;

    public ColoredStack(CompoundNBT tag)
    {
        this(DyeColor.values()[tag.getInt("color")], tag.getInt("quantity"));
    }

    public CompoundNBT toNBT(CompoundNBT tag)
    {
        tag.putInt("color", color.ordinal());
        tag.putInt("quantity", quantity);
        return tag;
    }
}
