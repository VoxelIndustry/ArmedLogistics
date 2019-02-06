package net.vi.woodengears.common.grid.logistic.node;

import net.minecraft.util.math.BlockPos;

public interface LogisticNode
{
    void wake();

    void sleep();

    boolean isAwake();

    void networkTick();

    BlockPos getRailPos();
}
