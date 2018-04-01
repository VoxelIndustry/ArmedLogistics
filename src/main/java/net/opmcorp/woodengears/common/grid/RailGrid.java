package net.opmcorp.woodengears.common.grid;

public class RailGrid extends CableGrid
{
    public RailGrid(int identifier)
    {
        super(identifier);
    }

    @Override
    public CableGrid copy(int identifier)
    {
        return new RailGrid(identifier);
    }
}
