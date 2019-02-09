package net.vi.woodengears.common.grid.logistic.node;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.vi.woodengears.common.tile.TileLogicisticNode;

public class BaseItemRequester extends BaseLogisticNode implements Requester<ItemStack>
{
    @Getter(AccessLevel.PROTECTED)
    private InventoryBuffer buffer;

    private TileLogicisticNode tile;

    public BaseItemRequester(TileLogicisticNode tile, InventoryBuffer buffer)
    {
        this.tile = tile;

        this.buffer = buffer;
    }

    @Override
    public BlockPos getRailPos()
    {
        return tile.getRailPos();
    }

    @Override
    public int insert(ItemStack value)
    {
        if (buffer.isFull())
            return 0;

        this.sleep();

        return buffer.add(value).getCount();
    }

    @Override
    public boolean isBufferFull()
    {
        return buffer.isFull();
    }
}
