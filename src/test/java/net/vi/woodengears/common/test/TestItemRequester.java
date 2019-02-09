package net.vi.woodengears.common.test;

import net.minecraft.util.math.BlockPos;
import net.vi.woodengears.common.grid.logistic.node.BaseItemRequester;
import net.vi.woodengears.common.grid.logistic.node.InventoryBuffer;

public class TestItemRequester extends BaseItemRequester
{
    private BlockPos pos;

    public TestItemRequester()
    {
        this(BlockPos.ORIGIN);
    }

    public TestItemRequester(BlockPos pos)
    {
        super(null, new InventoryBuffer(2, 128));

        this.pos = pos;
    }

    @Override
    public BlockPos getRailPos()
    {
        return this.pos;
    }
}
