package net.opmcorp.woodengears.common.grid;

import java.util.Collection;

public interface ITileRail extends ITileCable<RailGrid>
{
    Collection<IRailConnectable> getConnectedHandlers();
}
