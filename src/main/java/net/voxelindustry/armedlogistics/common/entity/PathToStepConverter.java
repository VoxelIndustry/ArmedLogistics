package net.voxelindustry.armedlogistics.common.entity;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.voxelindustry.armedlogistics.common.grid.Path;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static net.voxelindustry.armedlogistics.common.entity.EntityLogisticArm.RAIL_OFFSET;

public class PathToStepConverter
{
    public static boolean isNextRailCorner(int index, Path path)
    {
        BlockPos previous = path.getPoints().get(index - 1);
        BlockPos next = path.getPoints().get(index + 1);

        return previous.getX() != next.getX() && previous.getZ() != next.getZ();
    }

    public static boolean  isPathStraight(Path path)
    {
        if (path.getFrom().getX() != path.getTo().getX() && path.getFrom().getZ() != path.getTo().getZ())
            return false;

        return path.getPoints().stream().allMatch(point -> point.getX() == path.getFrom().getX() || point.getZ() == path.getFrom().getZ());
    }

    public static List<Vec3d> createStepsFromPath(Path path)
    {
        if (path.getPoints().isEmpty())
            return emptyList();

        int index = 1;
        boolean straightPath = isPathStraight(path);

        Direction endFacing = Direction.getFacingFromVector(
                path.getTo().getX() - path.getPoints().get(path.getPoints().size() - 2).getX(),
                0,
                path.getTo().getZ() - path.getPoints().get(path.getPoints().size() - 2).getZ()).getOpposite();

        List<Vec3d> steps = new ArrayList<>();

        while (!straightPath && index < path.getPoints().size() - 1)
        {
            if (isNextRailCorner(index, path))
            {
                BlockPos current = path.getPoints().get(index);
                BlockPos previous = path.getPoints().get(index - 1).subtract(current);
                BlockPos next = path.getPoints().get(index + 1).subtract(current);

                Vec3d pos = new Vec3d(current);

                FacingCorner facingCorner = FacingCorner.fromBlockPos(previous, next);

                Vec3d add;
                if (facingCorner == FacingCorner.WEST_NORTH)
                    add = pos.add(new Vec3d(1 - RAIL_OFFSET, 0, 1 - RAIL_OFFSET));
                else
                    add = pos.add(facingCorner.getVector());
                steps.add(add);
            }
            index++;
        }

        Vec3d to = new Vec3d(path.getTo()).add(0.5, 0, 0.5);
        steps.add(to.add(FacingLane.fromFacing(endFacing.getOpposite()).getVector()));
        steps.add(to);
        steps.add(to.add(FacingLane.fromFacing(endFacing).getVector()));

        return steps;
    }
}
