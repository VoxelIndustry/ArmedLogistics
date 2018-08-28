package net.vi.woodengears.common.grid;

import java.util.Collection;

public interface ITileRail extends ITileCable<RailGrid>
{
    Collection<IRailConnectable> getConnectedHandlers();
}
