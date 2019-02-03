package net.vi.woodengears.common.block;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
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
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.common.gui.GuiType;
import net.vi.woodengears.common.tile.TileProvider;

public class BlockProvider extends BlockTileBase<TileProvider>
{
    public static final PropertyDirection FACING = BlockDirectional.FACING;

    public BlockProvider()
    {
        super("provider", Material.PISTON, TileProvider.class);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
    {
        super.onNeighborChange(world, pos, neighbor);

        this.getWorldTile(world, pos).checkInventory();
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, facing.getOpposite());
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[]{FACING});
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 7));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | (state.getValue(FACING)).getIndex();

        return i;
    }

    @Override
    public TileEntity createNewTileEntity(World w, int meta)
    {
        return new TileProvider();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking())
            return false;

        player.openGui(WoodenGears.instance, GuiType.PROVIDER.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock(World w, BlockPos pos, IBlockState state)
    {
        ((TileProvider) w.getTileEntity(pos)).disconnectGrid();

        super.breakBlock(w, pos, state);
    }
}
