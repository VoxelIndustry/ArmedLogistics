package net.vi.woodengears.common.grid.logistic;

import lombok.Getter;

public class BaseLogisticNode implements LogisticNode
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
