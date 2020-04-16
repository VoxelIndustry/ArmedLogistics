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
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.voxelindustry.armedlogistics.common.tile.TileRequester;

public class BlockRequester extends BlockTileBase<TileRequester>
{
    public static final DirectionProperty FACING = BlockProvider.FACING;

    public BlockRequester(Properties properties)
    {
        super(properties, TileRequester.class);
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
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
    {
        getWorldTile(world, currentPos).onAdjacentRefresh();

        return super.updatePostPlacement(stateIn, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader reader)
    {
        return new TileRequester();
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
            TileRequester tile = (TileRequester) world.getTileEntity(pos);
            tile.disconnectGrid();
            tile.dropBuffer();
        }

        super.onReplaced(state, world, pos, newState, isMoving);
    }
}
