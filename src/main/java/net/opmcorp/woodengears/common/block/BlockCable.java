package net.opmcorp.woodengears.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCable extends BlockBase
{
    protected static final AxisAlignedBB AABB_NONE  = new AxisAlignedBB(0.31D, 0D, 0.31D, 0.69D, 0.35D, 0.69D);
    protected static final AxisAlignedBB AABB_EAST  = new AxisAlignedBB(0.69D, 0D, 0.31D, 1.00D, 0.35D, 0.69D);
    protected static final AxisAlignedBB AABB_WEST  = new AxisAlignedBB(0.00D, 0D, 0.31D, 0.31D, 0.35D, 0.31D);
    protected static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.69D, 0D, 0.31D, 0.69D, 0.35D, 1.00D);
    protected static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.31D, 0D, 0.00D, 0.31D, 0.35D, 0.31D);

    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST  = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST  = PropertyBool.create("west");

    public BlockCable()
    {
        super("blockcable", Material.WOOD);

        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(NORTH, false)
                .withProperty(EAST, false)
                .withProperty(SOUTH, false)
                .withProperty(WEST, false));
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos neighbor)
    {
        if (neighbor.offset(EnumFacing.DOWN).equals(pos) && world.getBlockState(pos.offset(EnumFacing.UP))
                .getBlockFaceShape(world, pos, EnumFacing.DOWN) != BlockFaceShape.SOLID)
            world.destroyBlock(pos, true);

        super.neighborChanged(state, world, pos, block, neighbor);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, NORTH, EAST, WEST, SOUTH);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.withProperty(NORTH, canConnect(world, pos, EnumFacing.NORTH))
                .withProperty(EAST, canConnect(world, pos, EnumFacing.EAST))
                .withProperty(SOUTH, canConnect(world, pos, EnumFacing.SOUTH))
                .withProperty(WEST, canConnect(world, pos, EnumFacing.WEST));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        AxisAlignedBB res = AABB_NONE;

        IBlockState actualState = this.getActualState(state, source, pos);

        if (actualState.getValue(EAST))
            res = res.union(AABB_EAST);
        if (actualState.getValue(WEST))
            res = res.union(AABB_WEST);
        if (actualState.getValue(NORTH))
            res = res.union(AABB_NORTH);
        if (actualState.getValue(SOUTH))
            res = res.union(AABB_SOUTH);

        return res;
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side)
    {
        if (side != EnumFacing.DOWN)
            return false;
        return world.getBlockState(pos.offset(EnumFacing.UP))
                .getBlockFaceShape(world, pos, EnumFacing.DOWN) == BlockFaceShape.SOLID;
    }

    private boolean canConnect(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
        return world.getBlockState(pos.offset(facing)).getBlock() instanceof BlockCable;
    }
}
