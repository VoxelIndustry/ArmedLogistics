package net.voxelindustry.armedlogistics.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.voxelindustry.armedlogistics.common.tile.TileCable;

import javax.annotation.Nullable;

public class BlockRail extends BlockTileBase<TileCable>
{
    protected static final AxisAlignedBB AABB_NONE  = new AxisAlignedBB(0.125, 1, 0.125, 0.875, 0.75, 0.875);
    protected static final AxisAlignedBB AABB_EAST  = new AxisAlignedBB(0.875, 1, 0.125, 1.00D, 0.75, 0.875);
    protected static final AxisAlignedBB AABB_WEST  = new AxisAlignedBB(0, 1, 0.125, 0.125, 0.75, 0.125);
    protected static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.875, 1, 0.125, 0.875, 0.75, 1.00D);
    protected static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.125, 1, 0.00D, 0.125, 0.75, 0.125);

    public static final BooleanProperty NORTH     = SixWayBlock.NORTH;
    public static final BooleanProperty EAST      = SixWayBlock.EAST;
    public static final BooleanProperty SOUTH     = SixWayBlock.SOUTH;
    public static final BooleanProperty WEST      = SixWayBlock.WEST;
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    public BlockRail(Properties properties)
    {
        super(properties, TileCable.class);

        setDefaultState(stateContainer.getBaseState()
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(CONNECTED, false));
    }

    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
            ((TileCable) world.getTileEntity(pos)).disconnectItself();

        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos)
    {
        if (world.getBlockState(pos.offset(Direction.UP)).func_224755_d(world, pos, Direction.DOWN))
            return true;
        return false;
    }

    private boolean canConnect(BlockState state, Direction direction)
    {
        return state.getBlock() instanceof BlockRail || state.getBlock() instanceof BlockArmReservoir;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader reader)
    {
        return new TileCable();
    }

    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder)
    {
        builder.add(NORTH, EAST, WEST, SOUTH, CONNECTED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState[] state = {getDefaultState()};

        if (!isValidPosition(state[0], context.getWorld(), context.getPos()))
            return null;

        Plane.HORIZONTAL.iterator().forEachRemaining(direction ->
        {
            if (canConnect(context.getWorld().getBlockState(context.getPos().offset(direction)), direction))
                state[0] = state[0].with(facingToProperty(direction), true);
        });
        return state[0];
    }

    @Override
    public BlockState updatePostPlacement(BlockState state,
                                          Direction facing,
                                          BlockState facingState,
                                          IWorld world,
                                          BlockPos currentPos,
                                          BlockPos facingPos)
    {
        BooleanProperty property = facingToProperty(facing);

        if (property == null)
            return state;

        if (!world.isRemote())
            ((TileCable) world.getTileEntity(currentPos)).scanHandlers(facingPos);

        return state.with(property, canConnect(facingState, facing));
    }

    private BooleanProperty facingToProperty(Direction direction)
    {
        switch (direction)
        {
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
            case EAST:
                return EAST;
        }
        return null;
    }
}