package net.vi.woodengears.common.test;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.voxelindustry.steamlayer.grid.CableGrid;
import net.voxelindustry.steamlayer.grid.GridManager;
import net.voxelindustry.steamlayer.grid.ITileCable;
import net.voxelindustry.steamlayer.grid.ITileNode;

import java.util.EnumMap;

public class GridTestBuilder
{
    private CableGrid grid;

    private BlockPos   origin;
    private BlockPos   current;
    private ITileCable lastCable;

    public GridTestBuilder(CableGrid grid)
    {
        this.grid = grid;
    }

    public GridTestBuilder origin(BlockPos pos)
    {
        this.origin = pos;
        return this;
    }

    public GridTestBuilder north()
    {
        return facing(EnumFacing.NORTH);
    }

    public GridTestBuilder south()
    {
        return facing(EnumFacing.SOUTH);
    }

    public GridTestBuilder east()
    {
        return facing(EnumFacing.EAST);
    }

    public GridTestBuilder west()
    {
        return facing(EnumFacing.WEST);
    }

    public GridTestBuilder up()
    {
        return facing(EnumFacing.UP);
    }

    public GridTestBuilder down()
    {
        return facing(EnumFacing.DOWN);
    }

    public GridTestBuilder facing(EnumFacing facing)
    {
        this.facingGet(facing);
        return this;
    }

    public ITileCable northGet()
    {
        return facingGet(EnumFacing.NORTH);
    }

    public ITileCable southGet()
    {
        return facingGet(EnumFacing.SOUTH);
    }

    public ITileCable eastGet()
    {
        return facingGet(EnumFacing.EAST);
    }

    public ITileCable westGet()
    {
        return facingGet(EnumFacing.WEST);
    }

    public ITileCable upGet()
    {
        return facingGet(EnumFacing.UP);
    }

    public ITileCable downGet()
    {
        return facingGet(EnumFacing.DOWN);
    }

    public ITileCable facingGet(EnumFacing facing)
    {
        if (lastCable != null)
            this.current = current.offset(facing);
        else
            this.current = origin;

        DummyTileCable cable = new DummyTileCable(current, grid.getIdentifier());

        if (lastCable != null)
        {
            lastCable.connect(facing, cable);
            cable.connect(facing.getOpposite(), lastCable);
        }

        this.grid.addCable(cable);
        this.lastCable = cable;
        return cable;
    }

    public GridTestBuilder current(ITileCable cable)
    {
        this.current = cable.getBlockPos();
        this.lastCable = cable;
        return this;
    }

    public void create()
    {

    }

    public static GridTestBuilder build(CableGrid grid)
    {
        return new GridTestBuilder(grid);
    }

    @Getter
    private static class DummyTileCable implements ITileCable
    {
        private EnumMap<EnumFacing, ITileCable> connectionsMap = new EnumMap<>(EnumFacing.class);
        @Getter
        private BlockPos                        blockPos;
        @Getter
        @Setter
        private int                             grid;

        public DummyTileCable(BlockPos pos, int grid)
        {
            this.blockPos = pos;
            this.grid = grid;
        }

        @Override
        public World getBlockWorld()
        {
            return null;
        }

        @Override
        public CableGrid createGrid(int nextID)
        {
            return null;
        }

        @Override
        public GridManager getGridManager()
        {
            return null;
        }

        @Override
        public boolean canConnect(EnumFacing facing, ITileNode to)
        {
            return true;
        }
    }
}
