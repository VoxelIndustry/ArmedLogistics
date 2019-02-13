package net.vi.woodengears.common;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.vi.woodengears.WoodenGears;

public class TickHandler
{
    @SubscribeEvent
    public void serverTick(final TickEvent.ServerTickEvent e)
    {
        WoodenGears.instance.getGridManager().tickGrids();
    }
}

