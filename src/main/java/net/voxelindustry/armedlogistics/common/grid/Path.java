package net.voxelindustry.armedlogistics.common.grid;

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

        points = new ArrayList<>();
    }

    public boolean isEmpty()
    {
        return points.isEmpty();
    }

    public Path copy()
    {
        Path path = new Path(from, to);
        path.getPoints().addAll(getPoints());
        path.setImpossible(isImpossible());

        return path;
    }
}
