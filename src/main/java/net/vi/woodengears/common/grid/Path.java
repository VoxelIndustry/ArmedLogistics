package net.vi.woodengears.common.grid;

import lombok.Data;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

@Data
public class Path
{
    private BlockPos from;
    private BlockPos to;

    private List<BlockPos> points;
    private boolean        impossible;

    public Path(BlockPos from, BlockPos to)
    {
        this.from = from;
        this.to = to;

        this.points = new ArrayList<>();
    }

    public boolean isEmpty()
    {
        return this.points.isEmpty();
    }
}
