package net.voxelindustry.armedlogistics.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.voxelindustry.armedlogistics.common.tile.TileActiveProvider;
import net.voxelindustry.armedlogistics.common.tile.TileProvider;

public class BlockProvider extends BlockTileBase<TileProvider>
{
    public static final DirectionProperty FACING = DirectionProperty.create("facing",
            facing -> facing != Direction.UP);

    private boolean isActive;

    public BlockProvider(Properties properties, boolean isActive)
    {
        super(properties, TileProvider.class);

        setDefaultState(getStateContainer().getBaseState()
                .with(FACING, Direction.NORTH));

        this.isActive = isActive;
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor)
    {
        super.onNeighborChange(state, world, pos, neighbor);

        getWorldTile(world, pos).onAdjacentRefresh();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        Direction placingFacing = context.getFace().getOpposite();

        if (placingFacing == Direction.UP)
            placingFacing = Direction.DOWN;
        return getDefaultState().with(FACING, placingFacing);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader reader)
    {
        if (isActive)
            return new TileActiveProvider();
        return new TileProvider();
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        if (player.isSneaking())
            return false;

        if (world.isRemote)
            return true;

        NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) world.getTileEntity(pos), pos);
        return true;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            TileProvider tile = (TileProvider) world.getTileEntity(pos);
            tile.disconnectGrid();
            tile.dropBuffer();
        }

        super.onReplaced(state, world, pos, newState, isMoving);
    }
}
