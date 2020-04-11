package net.voxelindustry.armedlogistics.common.grid;

import net.minecraft.util.EnumFacing;
import net.voxelindustry.armedlogistics.common.tile.TileCable;

public interface IRailConnectable
{
    default boolean canConnect(TileCable cable, EnumFacing from)
    {
        return true;
    }
}
