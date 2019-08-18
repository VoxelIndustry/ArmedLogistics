package net.vi.woodengears.common.tile;

import net.minecraft.util.ITickable;

public class TileActiveProvider extends TileProvider implements ITickable
{
    @Override
    public void update()
    {
        if (isClient())
            return;
    }
}
