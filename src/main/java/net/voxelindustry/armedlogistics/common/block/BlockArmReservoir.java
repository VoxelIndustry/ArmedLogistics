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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.voxelindustry.armedlogistics.common.tile.TileArmReservoir;
import net.voxelindustry.gizmos.Gizmos;
import net.voxelindustry.gizmos.handle.TimedGizmoHandle;
import net.voxelindustry.gizmos.widget.WindowGizmo;

import java.time.Duration;

public class BlockArmReservoir extends BlockTileBase<TileArmReservoir>
{
    public static final DirectionProperty FACING = DirectionProperty.create("facing",
            facing -> facing.getAxis().isHorizontal());

    public BlockArmReservoir(Properties properties)
    {
        super(properties, TileArmReservoir.class);

        setDefaultState(getStateContainer().getBaseState()
                .with(FACING, Direction.NORTH));
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
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        if (player.isSneaking())
        {
            if (world.isRemote)
            {
                WindowGizmo window = Gizmos.getCreateByPos(new Vec3d(pos.up()), "RESERVOIR", () ->
                {
                    WindowGizmo created = Gizmos.window(WindowGizmo.Parameters.builder()
                            .pos(new Vec3d(pos.getX(), pos.getY() + 1, pos.getZ()))
                            .textColorRGBA(0xFFFFFFFF)
                            .fixedLineCount(4)
                            .offset(new Vec3d(0.5, -0.5, 0))
                            .build());
                    created.handle(new TimedGizmoHandle(Duration.ofMinutes(1)));
                    created.appendData("This is a test of §e{{count}}§r and §3{{join}}", new TimedGizmoHandle(Duration.ofMinutes(1)), 0, "BANANA");
                    created.appendData("TEST 30sec", new TimedGizmoHandle(Duration.ofSeconds(30)));
                    return created;
                });
                window.getHandle().ifPresent(handle -> ((TimedGizmoHandle) handle).reset());
                window.updateValue("count", 0, 1);
                window.updateValue("join", 0, "!");
            }
            return false;
        }

        if (world.isRemote)
            return true;

        NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) world.getTileEntity(pos), pos);
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader reader)
    {
        return new TileArmReservoir();
    }
}
