package net.vi.woodengears.common.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.items.ItemStackHandler;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.common.tile.TileInventoryBase;
import net.voxelindustry.steamlayer.tile.TileBase;

public abstract class BlockTileBase<T extends TileBase> extends BlockContainer
{
    private Class<T> tileClass;

    public BlockTileBase(String name, Material material, Class<T> tileClass)
    {
        super(material);
        this.setRegistryName(WoodenGears.MODID, name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(WoodenGears.TAB_ALL);

        this.tileClass = tileClass;
    }

    @Override
    public void breakBlock(final World world, final BlockPos pos, final IBlockState state)
    {
        final TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileInventoryBase)
        {
            ((TileInventoryBase) tile).dropInventory();
            world.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(world, pos, state);
    }

    public T getWorldTile(IBlockAccess world, BlockPos pos)
    {
        return tileClass.cast(this.getRawWorldTile(world, pos));
    }

    public TileEntity getRawWorldTile(IBlockAccess world, BlockPos pos)
    {
        if (world instanceof ChunkCache)
            return ((ChunkCache) world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
        else
            return world.getTileEntity(pos);
    }

    public boolean checkWorldTile(IBlockAccess world, BlockPos pos)
    {
        if (world instanceof ChunkCache)
            return tileClass.isInstance(((ChunkCache) world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK));
        else
            return tileClass.isInstance(world.getTileEntity(pos));
    }

    public Class<T> getTileClass()
    {
        return tileClass;
    }
}
