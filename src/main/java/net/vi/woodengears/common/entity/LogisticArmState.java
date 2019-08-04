package net.vi.woodengears.common.entity;

public enum LogisticArmState
{
    BLOCKED,
    MOVING_TO_PROVIDER,
    PICKING_FROM_PROVIDER,
    MOVING_TO_REQUESTER,
    GIVING_TO_REQUESTER,
    MOVING_TO_RESERVOIR,
    COMPLETED;

    public boolean isBlocked()
    {
        return this == BLOCKED;
    }

    public boolean isCompleted()
    {
        return this == COMPLETED;
    }

    public boolean isMoving()
    {
        return this == MOVING_TO_PROVIDER || this == MOVING_TO_REQUESTER || this == MOVING_TO_RESERVOIR;
    }
}
