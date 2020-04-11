package net.voxelindustry.armedlogistics.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.common.gui.GuiType;
import net.voxelindustry.armedlogistics.common.tile.TileArmReservoir;
import net.voxelindustry.gizmos.Gizmos;
import net.voxelindustry.gizmos.handle.TimedGizmoHandle;
import net.voxelindustry.gizmos.widget.WindowGizmo;

import javax.annotation.Nullable;
import java.time.Duration;

public class BlockArmReservoir extends net.voxelindustry.armedlogistics.common.block.BlockTileBase<TileArmReservoir>
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing",
            facing -> facing.getAxis().isHorizontal());

    public BlockArmReservoir()
    {
        super("armreservoir", Material.WOOD, TileArmReservoir.class);

        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer)
    {
        EnumFacing cable = null;
        int count = 0;

        for (EnumFacing side : EnumFacing.HORIZONTALS)
        {
            if (world.getBlockState(pos.offset(side)).getBlock() instanceof net.voxelindustry.armedlogistics.common.block.BlockCable)
            {
                cable = side;
                count++;
            }
        }

        if (count == 1)
            return getDefaultState().withProperty(FACING, cable);
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror)
    {
        return state.withRotation(mirror.toRotation(state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
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

        player.openGui(ArmedLogistics.instance, GuiType.ARM_RESERVOIR.ordinal(),
                world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileArmReservoir();
    }
}
