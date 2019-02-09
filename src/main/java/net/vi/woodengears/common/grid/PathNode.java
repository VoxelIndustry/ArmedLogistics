package net.vi.woodengears.common.grid;

import lombok.Data;
import net.minecraft.util.math.BlockPos;

@Data
public class PathNode
{
    private PathNode previous;
    private int cost;
    private BlockPos pos;
    private ITileNode tile;

    public PathNode(PathNode previous, BlockPos pos, ITileNode tile)
    {
        this.previous = previous;
        this.pos = pos;
        this.tile = tile;

        this.cost = -1;
    }

    public int getDepth()
    {
        if (this.getPrevious() != null)
            return getPrevious().getDepth() + 1;
        return 0;
    }
}
