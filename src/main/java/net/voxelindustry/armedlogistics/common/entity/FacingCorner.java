package net.voxelindustry.armedlogistics.common.entity;

import lombok.Getter;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static net.voxelindustry.armedlogistics.common.entity.EntityLogisticArm.RAIL_OFFSET;

public enum FacingCorner
{
    NORTH_EAST(RAIL_OFFSET, 0, 1 - RAIL_OFFSET), //
    NORTH_WEST(RAIL_OFFSET, 0, RAIL_OFFSET), //
    EAST_NORTH(1 - RAIL_OFFSET, 0, RAIL_OFFSET), //
    EAST_SOUTH(RAIL_OFFSET, 0, RAIL_OFFSET), //
    SOUTH_EAST(1 - RAIL_OFFSET, 0, 1 - RAIL_OFFSET), //
    SOUTH_WEST(1 - RAIL_OFFSET, 0, RAIL_OFFSET), //
    WEST_SOUTH(RAIL_OFFSET, 0, 1 - RAIL_OFFSET), //
    WEST_NORTH(1 - RAIL_OFFSET, 0, 1 - RAIL_OFFSET); //

    @Getter
    private Vec3d vector;

    FacingCorner(double x, double y, double z)
    {
        vector = new Vec3d(x, y, z);
    }

    public static FacingCorner fromFacings(Direction from, Direction to)
    {
        switch (from)
        {
            case NORTH:
                if (to == Direction.EAST)
                    return NORTH_EAST;
                return NORTH_WEST;
            case SOUTH:
                if (to == Direction.EAST)
                    return SOUTH_EAST;
                return SOUTH_WEST;
            case WEST:
                if (to == Direction.NORTH)
                    return WEST_NORTH;
                return WEST_SOUTH;
            case EAST:
                if (to == Direction.NORTH)
                    return EAST_NORTH;
                return EAST_SOUTH;
        }
        return null;
    }

    public static FacingCorner fromBlockPos(BlockPos from, BlockPos to)
    {
        return fromFacings(Direction.getFacingFromVector(from.getX(), from.getY(), from.getZ()),
                Direction.getFacingFromVector(to.getX(), to.getY(), to.getZ()));
    }
}
