package net.vi.woodengears.common.entity;

public enum LogisticArmBlockCause
{
    NONE,
    /**
     * Out of fuel meaning it didn't took enough or the path has changed and was too big.
     */
    NO_FUEL,
    /**
     * Path has changed and the any of the provider, requester or reservoir cannot be navigated to.
     */
    NO_PATH,
    /**
     * Waiting for another Arm in front of this one.
     */
    TRAFFIC_JAM
}
