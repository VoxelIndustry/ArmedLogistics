package net.vi.woodengears.common.test;

import net.minecraft.util.math.BlockPos;
import net.vi.woodengears.common.grid.logistic.node.BaseItemRequester;

public class TestItemRequester extends BaseItemRequester
{
    private BlockPos pos;

    public TestItemRequester()
    {
        this(BlockPos.ORIGIN);
    }

    public TestItemRequester(BlockPos pos)
    {
        super(null);

        this.pos = pos;
    }

    @Override
    public BlockPos getRailPos()
    {
        return this.pos;
    }
}
