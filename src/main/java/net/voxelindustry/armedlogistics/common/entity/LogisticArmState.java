package net.voxelindustry.armedlogistics.common.entity;

public enum LogisticArmState
{
    BLOCKED,
    MOVING_TO_PROVIDER,
    PICKING,
    MOVING_TO_DESTINATION,
    GIVING,
    MOVING_RESERVOIR,
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
        return this == MOVING_TO_PROVIDER || this == MOVING_TO_DESTINATION || this == MOVING_RESERVOIR;
    }
}
