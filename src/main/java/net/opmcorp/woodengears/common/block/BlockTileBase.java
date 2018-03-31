package net.opmcorp.woodengears.common.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.opmcorp.woodengears.WoodenGears;

public abstract class BlockTileBase extends BlockContainer
{
    public BlockTileBase(String name, Material material)
    {
        super(material);
        this.setRegistryName(WoodenGears.MODID, name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(WoodenGears.TAB_ALL);
    }

    @Override
    public void breakBlock(final World world, final BlockPos pos, final IBlockState state)
    {
        final TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof IInventory)
        {
            InventoryHelper.dropInventoryItems(world, pos, (IInventory) tile);
            world.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(world, pos, state);
    }

    public TileEntity getRawWorldTile(IBlockAccess world, BlockPos pos)
    {
        if (world instanceof ChunkCache)
            return ((ChunkCache) world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
        else
            return world.getTileEntity(pos);
    }
}
