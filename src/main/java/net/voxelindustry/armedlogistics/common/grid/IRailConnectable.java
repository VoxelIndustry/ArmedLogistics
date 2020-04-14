package net.voxelindustry.armedlogistics.common.grid;

import net.minecraft.util.Direction;
import net.voxelindustry.armedlogistics.common.tile.TileCable;

public interface IRailConnectable
{
    default boolean canConnect(TileCable cable, Direction from)
    {
        return true;
    }
}
