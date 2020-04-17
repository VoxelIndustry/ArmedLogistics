package net.voxelindustry.armedlogistics.common.block;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.voxelindustry.armedlogistics.common.tile.TileCable;

import javax.annotation.Nullable;

public class BlockRail extends BlockTileBase<TileCable>
{
    private static final double MIN_WIDTH = 0.1875;
    private static final double MAX_WIDTH = 0.8125;
    private static final double HEIGHT    = 0.8125;

    public static final BooleanProperty NORTH     = SixWayBlock.NORTH;
    public static final BooleanProperty EAST      = SixWayBlock.EAST;
    public static final BooleanProperty SOUTH     = SixWayBlock.SOUTH;
    public static final BooleanProperty WEST      = SixWayBlock.WEST;
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    private final Object2IntMap<BlockState> indexByState = new Object2IntOpenHashMap<>();
    private final Int2ObjectMap<VoxelShape> shapeByIndex = new Int2ObjectOpenHashMap<>();

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
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return shapeByIndex.computeIfAbsent(getIndex(state), BlockRail::computeShapeFromConnectionIndex);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return getShape(state, world, pos, context);
    }

    protected int getIndex(BlockState state)
    {
        return indexByState.computeIntIfAbsent(state, (keyState) ->
        {
            int computedIndex = 0;

            if (keyState.get(NORTH))
                computedIndex |= 1 << Direction.NORTH.getHorizontalIndex();

            if (keyState.get(EAST))
                computedIndex |= 1 << Direction.EAST.getHorizontalIndex();

            if (keyState.get(SOUTH))
                computedIndex |= 1 << Direction.SOUTH.getHorizontalIndex();

            if (keyState.get(WEST))
                computedIndex |= 1 << Direction.WEST.getHorizontalIndex();

            return computedIndex;
        });
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

    private static VoxelShape computeShapeFromConnectionIndex(int index)
    {
        VoxelShape none = VoxelShapes.create(MIN_WIDTH, 1, MIN_WIDTH, MAX_WIDTH, HEIGHT, MAX_WIDTH);
        if (((index >> Direction.NORTH.getHorizontalIndex()) & 1) == 1)
        {
            none = VoxelShapes.or(none, VoxelShapes.create(MIN_WIDTH, 1, 0.00D, MAX_WIDTH, HEIGHT, MIN_WIDTH));
        }
        if (((index >> Direction.SOUTH.getHorizontalIndex()) & 1) == 1)
        {
            none = VoxelShapes.or(none, VoxelShapes.create(MIN_WIDTH, 1, MAX_WIDTH, MAX_WIDTH, HEIGHT, 1.00D));
        }
        if (((index >> Direction.EAST.getHorizontalIndex()) & 1) == 1)
        {
            none = VoxelShapes.or(none, VoxelShapes.create(MAX_WIDTH, 1, MIN_WIDTH, 1.00D, HEIGHT, MAX_WIDTH));
        }
        if (((index >> Direction.WEST.getHorizontalIndex()) & 1) == 1)
        {
            none = VoxelShapes.or(none, VoxelShapes.create(0, 1, MIN_WIDTH, MIN_WIDTH, HEIGHT, MAX_WIDTH));
        }

        return none;
    }
}