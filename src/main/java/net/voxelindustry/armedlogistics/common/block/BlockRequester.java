package net.voxelindustry.armedlogistics.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.common.gui.GuiType;
import net.voxelindustry.armedlogistics.common.tile.TileRequester;

public class BlockRequester extends net.voxelindustry.armedlogistics.common.block.BlockTileBase<TileRequester>
{
    public static final PropertyDirection FACING = BlockProvider.FACING;

    public BlockRequester()
    {
        super("requester", Material.PISTON, TileRequester.class);
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
        return new TileRequester();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking())
            return false;

        player.openGui(ArmedLogistics.instance, GuiType.REQUESTER.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock(World w, BlockPos pos, IBlockState state)
    {
        TileRequester tile = (TileRequester) w.getTileEntity(pos);
        tile.disconnectGrid();
        tile.dropBuffer();

        super.breakBlock(w, pos, state);
    }
}
