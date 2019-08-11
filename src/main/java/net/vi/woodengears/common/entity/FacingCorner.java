package net.vi.woodengears.common.entity;

import lombok.Getter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static net.vi.woodengears.common.entity.EntityLogisticArm.RAIL_OFFSET;

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

    public static FacingCorner fromFacings(EnumFacing from, EnumFacing to)
    {
        switch (from)
        {
            case NORTH:
                if (to == EnumFacing.EAST)
                    return NORTH_EAST;
                return NORTH_WEST;
            case SOUTH:
                if (to == EnumFacing.EAST)
                    return SOUTH_EAST;
                return SOUTH_WEST;
            case WEST:
                if (to == EnumFacing.NORTH)
                    return WEST_NORTH;
                return WEST_SOUTH;
            case EAST:
                if (to == EnumFacing.NORTH)
                    return EAST_NORTH;
                return EAST_SOUTH;
        }
        return null;
    }

    public static FacingCorner fromBlockPos(BlockPos from, BlockPos to)
    {
        return fromFacings(EnumFacing.getFacingFromVector(from.getX(), from.getY(), from.getZ()),
                EnumFacing.getFacingFromVector(to.getX(), to.getY(), to.getZ()));
    }
}
