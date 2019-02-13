package net.vi.woodengears.common.grid;

import net.vi.woodengears.WoodenGears;
import net.voxelindustry.steamlayer.grid.GridManager;
import net.voxelindustry.steamlayer.grid.ITileCable;

import java.util.Collection;

public interface ITileRail extends ITileCable<RailGrid>
{
    Collection<IRailConnectable> getConnectedHandlers();

    @Override
    default GridManager getGridManager()
    {
        return WoodenGears.instance.getGridManager();
    }
}
