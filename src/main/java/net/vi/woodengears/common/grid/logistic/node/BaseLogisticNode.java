package net.vi.woodengears.common.grid.logistic.node;

import lombok.Getter;

public abstract class BaseLogisticNode implements LogisticNode
{
    @Getter
    private boolean isAwake;

    @Override
    public void wake()
    {
        this.isAwake = true;
    }

    @Override
    public void sleep()
    {
        this.isAwake = false;
    }

    @Override
    public void networkTick()
    {
        if(this.isAwake())
            this.sleep();
    }
}