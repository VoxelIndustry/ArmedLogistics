package net.voxelindustry.armedlogistics.common.test;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.Direction;
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
        origin = pos;
        return this;
    }

    public GridTestBuilder north()
    {
        return facing(Direction.NORTH);
    }

    public GridTestBuilder south()
    {
        return facing(Direction.SOUTH);
    }

    public GridTestBuilder east()
    {
        return facing(Direction.EAST);
    }

    public GridTestBuilder west()
    {
        return facing(Direction.WEST);
    }

    public GridTestBuilder up()
    {
        return facing(Direction.UP);
    }

    public GridTestBuilder down()
    {
        return facing(Direction.DOWN);
    }

    public GridTestBuilder facing(Direction facing)
    {
        facingGet(facing);
        return this;
    }

    public ITileCable northGet()
    {
        return facingGet(Direction.NORTH);
    }

    public ITileCable southGet()
    {
        return facingGet(Direction.SOUTH);
    }

    public ITileCable eastGet()
    {
        return facingGet(Direction.EAST);
    }

    public ITileCable westGet()
    {
        return facingGet(Direction.WEST);
    }

    public ITileCable upGet()
    {
        return facingGet(Direction.UP);
    }

    public ITileCable downGet()
    {
        return facingGet(Direction.DOWN);
    }

    public ITileCable facingGet(Direction facing)
    {
        if (lastCable != null)
            current = current.offset(facing);
        else
            current = origin;

        DummyTileCable cable = new DummyTileCable(current, grid.getIdentifier());

        if (lastCable != null)
        {
            lastCable.connect(facing, cable);
            cable.connect(facing.getOpposite(), lastCable);
        }

        grid.addCable(cable);
        lastCable = cable;
        return cable;
    }

    public GridTestBuilder current(ITileCable cable)
    {
        current = cable.getBlockPos();
        lastCable = cable;
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
        private EnumMap<Direction, ITileCable> connectionsMap = new EnumMap<>(Direction.class);
        @Getter
        private BlockPos                        blockPos;
        @Getter
        @Setter
        private int                             grid;

        public DummyTileCable(BlockPos pos, int grid)
        {
            blockPos = pos;
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
        public boolean canConnect(Direction facing, ITileNode to)
        {
            return true;
        }
    }
}
