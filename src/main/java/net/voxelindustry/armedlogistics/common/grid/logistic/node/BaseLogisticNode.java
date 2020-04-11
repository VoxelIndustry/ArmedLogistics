package net.voxelindustry.armedlogistics.common.grid.logistic.node;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.voxelindustry.armedlogistics.common.grid.logistic.LogisticNetwork;

import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
public abstract class BaseLogisticNode<T> implements LogisticNode<T>
{
    private final Supplier<LogisticNetwork<T>> networkSupplier;

    @Getter
    private boolean isAwake;

    @Override
    public void wake()
    {
        isAwake = true;
    }

    @Override
    public void sleep()
    {
        isAwake = false;
    }

    @Override
    public void networkTick()
    {
        if (isAwake())
            sleep();
    }
}
