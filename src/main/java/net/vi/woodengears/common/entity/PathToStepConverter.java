package net.vi.woodengears.common.entity;

import net.minecraft.util.math.BlockPos;
import net.vi.woodengears.common.grid.Path;

public class PathToStepConverter
{
    public static boolean isNextRailCorner(int index, Path path)
    {
        BlockPos previous = path.getPoints().get(index - 1);
        BlockPos next = path.getPoints().get(index + 1);

        return previous.getX() != next.getX() && previous.getZ() != next.getZ();
    }

    public static boolean isPathStraight(Path path)
    {
        if (path.getFrom().getX() != path.getTo().getX() && path.getFrom().getZ() != path.getTo().getZ())
            return false;

        return path.getPoints().stream().allMatch(point -> point.getX() == path.getFrom().getX() || point.getZ() == path.getFrom().getZ());
    }
}
