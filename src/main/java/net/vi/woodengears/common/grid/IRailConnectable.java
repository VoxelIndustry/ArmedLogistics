package net.vi.woodengears.common.grid;

import net.minecraft.util.EnumFacing;
import net.vi.woodengears.common.tile.TileCable;

public interface IRailConnectable
{
    default boolean canConnect(TileCable cable, EnumFacing from)
    {
        return true;
    }
}
