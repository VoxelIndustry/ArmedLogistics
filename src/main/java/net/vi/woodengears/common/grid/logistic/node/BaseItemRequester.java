package net.vi.woodengears.common.grid.logistic.node;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.vi.woodengears.common.tile.TileLogicisticNode;

public class BaseItemRequester extends BaseLogisticNode implements Requester<ItemStack>
{
    private TileLogicisticNode tile;

    public BaseItemRequester(TileLogicisticNode tile)
    {
        this.tile = tile;
    }

    @Override
    public BlockPos getRailPos()
    {
        return tile.getRailPos();
    }
}
