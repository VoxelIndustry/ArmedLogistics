package net.vi.woodengears.common.block;

import net.minecraft.block.BlockDirectional;
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
import net.minecraft.world.World;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.common.gui.GuiType;
import net.vi.woodengears.common.tile.TileArmReservoir;

import javax.annotation.Nullable;

public class BlockArmReservoir extends BlockTileBase<TileArmReservoir>
{
    private static final PropertyDirection FACING = BlockDirectional.FACING;

    public BlockArmReservoir()
    {
        super("blockarmreservoir", Material.WOOD, TileArmReservoir.class);

        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer)
    {
        EnumFacing cable = null;
        int count = 0;

        for (EnumFacing side : EnumFacing.VALUES)
        {
            if (world.getBlockState(pos.offset(side)).getBlock() instanceof BlockCable)
            {
                cable = side;
                count++;
            }
        }

        if (count == 1)
            return this.getDefaultState().withProperty(FACING, cable);
        return this.getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
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

    public IBlockState withMirror(IBlockState state, Mirror mirror)
    {
        return state.withRotation(mirror.toRotation(state.getValue(FACING)));
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking())
            return false;

        player.openGui(WoodenGears.instance, GuiType.ARM_RESERVOIR.ordinal(),
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
