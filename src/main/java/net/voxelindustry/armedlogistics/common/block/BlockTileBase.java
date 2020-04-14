package net.voxelindustry.armedlogistics.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.voxelindustry.steamlayer.tile.TileBase;

public abstract class BlockTileBase<T extends TileBase> extends Block
{
    private Class<T> tileClass;

    public BlockTileBase(Properties properties, Class<T> tileClass)
    {
        super(properties);
        this.tileClass = tileClass;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity instanceof IInventory)
            {
                InventoryHelper.dropInventoryItems(world, pos, (IInventory) tileentity);
                world.updateComparatorOutputLevel(pos, this);
            }

            super.onReplaced(state, world, pos, newState, isMoving);
        }
    }

    public T getWorldTile(IWorldReader world, BlockPos pos)
    {
        return tileClass.cast(this.getRawWorldTile(world, pos));
    }

    public TileEntity getRawWorldTile(IWorldReader world, BlockPos pos)
    {
        if (world instanceof ChunkRenderCache)
            return ((ChunkRenderCache) world).getTileEntity(pos, Chunk.CreateEntityType.CHECK);
        else
            return world.getTileEntity(pos);
    }

    public boolean checkWorldTile(IWorldReader world, BlockPos pos)
    {
        if (world instanceof ChunkRenderCache)
            return tileClass.isInstance(((ChunkRenderCache) world).getTileEntity(pos, Chunk.CreateEntityType.CHECK));
        else
            return tileClass.isInstance(world.getTileEntity(pos));
    }

    public Class<T> getTileClass()
    {
        return tileClass;
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }
}
