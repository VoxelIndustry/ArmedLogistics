package net.opmcorp.woodengears.common.grid;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import net.opmcorp.woodengears.common.tile.TileArmReservoir;
import net.opmcorp.woodengears.common.tile.TileProvider;

import java.util.HashMap;
import java.util.Map;

public class RailGrid extends CableGrid
{
    private ListMultimap<TileArmReservoir, ITileRail> reservoirMap;
    private Map<TileProvider, ITileRail>              providerMap;

    public RailGrid(int identifier)
    {
        super(identifier);

        this.reservoirMap = MultimapBuilder.hashKeys().arrayListValues().build();
        this.providerMap = new HashMap<>();
    }

    @Override
    public CableGrid copy(int identifier)
    {
        return new RailGrid(identifier);
    }

    public void addConnectedReservoir(ITileRail pipe, TileArmReservoir handler)
    {
        if (!this.reservoirMap.containsEntry(handler, pipe))
            this.reservoirMap.put(handler, pipe);
    }

    private void clearConnectedReservoir(ITileRail pipe)
    {
        pipe.getConnectedHandlers().forEach(handler ->
        {
            if (handler instanceof TileArmReservoir)
                this.removeConnectedReservoir(pipe, (TileArmReservoir) handler);
        });
    }

    public void removeConnectedReservoir(ITileRail pipe, TileArmReservoir handler)
    {
        this.reservoirMap.remove(handler, pipe);
    }

    @Override
    public boolean removeCable(ITileNode cable)
    {
        if (super.removeCable(cable))
        {
            this.clearConnectedReservoir((ITileRail) cable);
            return true;
        }
        return false;
    }
}
