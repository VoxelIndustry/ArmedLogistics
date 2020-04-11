package net.voxelindustry.armedlogistics.common.grid;

import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.steamlayer.grid.GridManager;
import net.voxelindustry.steamlayer.grid.ITileCable;

import java.util.Collection;

public interface ITileRail extends ITileCable<RailGrid>
{
    Collection<IRailConnectable> getConnectedHandlers();

    @Override
    default GridManager getGridManager()
    {
        return ArmedLogistics.instance.getGridManager();
    }
}
