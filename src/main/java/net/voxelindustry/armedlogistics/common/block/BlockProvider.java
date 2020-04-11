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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.common.gui.GuiType;
import net.voxelindustry.armedlogistics.common.tile.TileActiveProvider;
import net.voxelindustry.armedlogistics.common.tile.TileProvider;

public class BlockProvider extends net.voxelindustry.armedlogistics.common.block.BlockTileBase<TileProvider>
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing",
            facing -> facing != EnumFacing.UP);

    private boolean isActive;

    public BlockProvider(boolean isActive)
    {
        super("provider_" + (isActive ? "active" : "passive"), Material.PISTON, TileProvider.class);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));

        this.isActive = isActive;
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
    {
        super.onNeighborChange(world, pos, neighbor);

        getWorldTile(world, pos).onAdjacentRefresh();
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer)
    {
        EnumFacing placingFacing = facing.getOpposite();

        if (placingFacing == EnumFacing.UP)
            placingFacing = EnumFacing.DOWN;
        return getDefaultState().withProperty(FACING, placingFacing);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 7));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | (state.getValue(FACING)).getIndex();

        return i;
    }

    @Override
    public TileEntity createNewTileEntity(World w, int meta)
    {
        if (isActive)
            return new TileActiveProvider();
        return new TileProvider();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking())
            return false;

        player.openGui(ArmedLogistics.instance, GuiType.PROVIDER.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock(World w, BlockPos pos, IBlockState state)
    {
        TileProvider tile = (TileProvider) w.getTileEntity(pos);
        tile.disconnectGrid();
        tile.dropBuffer();

        super.breakBlock(w, pos, state);
    }
}
