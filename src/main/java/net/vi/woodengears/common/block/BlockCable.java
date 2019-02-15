package net.vi.woodengears.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.vi.woodengears.common.tile.TileCable;

import javax.annotation.Nullable;

public class BlockCable extends BlockTileBase<TileCable>
{
    protected static final AxisAlignedBB AABB_NONE  = new AxisAlignedBB(0.125, 1, 0.125, 0.875, 0.75, 0.875);
    protected static final AxisAlignedBB AABB_EAST  = new AxisAlignedBB(0.875, 1, 0.125, 1.00D, 0.75, 0.875);
    protected static final AxisAlignedBB AABB_WEST  = new AxisAlignedBB(0, 1, 0.125, 0.125, 0.75, 0.125);
    protected static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.875, 1, 0.125, 0.875, 0.75, 1.00D);
    protected static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.125, 1, 0.00D, 0.125, 0.75, 0.125);

    public BlockCable()
    {
        super("blockcable", Material.WOOD, TileCable.class);

        this.setDefaultState(this.blockState.getBaseState());
    }

    @Override
    public boolean isOpaqueCube(final IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(final IBlockState state)
    {
        return false;
    }

    @Override
    public boolean causesSuffocation(final IBlockState state)
    {
        return false;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileCable)
        {
            return ((IExtendedBlockState) state).withProperty(Properties.AnimationProperty,
                    ((TileCable) tile).getVisibilityState());
        }
        return state;
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{Properties.AnimationProperty});
    }

    @Override
    public void breakBlock(World w, BlockPos pos, IBlockState state)
    {
        ((TileCable) w.getTileEntity(pos)).disconnectItself();

        super.breakBlock(w, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos neighbor)
    {
        if (neighbor.offset(EnumFacing.DOWN).equals(pos) && world.getBlockState(pos.offset(EnumFacing.UP))
                .getBlockFaceShape(world, pos, EnumFacing.DOWN) != BlockFaceShape.SOLID)
            world.destroyBlock(pos, true);

        if (!world.isRemote)
            ((TileCable) world.getTileEntity(pos)).scanHandlers(neighbor);

        super.neighborChanged(state, world, pos, block, neighbor);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        TileCable tile = (TileCable) source.getTileEntity(pos);
        AxisAlignedBB res = AABB_NONE;
        if (tile != null)
        {
            if (tile.isConnected(EnumFacing.EAST))
                res = res.union(AABB_EAST);
            if (tile.isConnected(EnumFacing.WEST))
                res = res.union(AABB_WEST);
            if (tile.isConnected(EnumFacing.NORTH))
                res = res.union(AABB_NORTH);
            if (tile.isConnected(EnumFacing.SOUTH))
                res = res.union(AABB_SOUTH);
        }
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

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileCable();
    }
}
