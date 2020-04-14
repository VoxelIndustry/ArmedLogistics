package net.voxelindustry.armedlogistics.common;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.voxelindustry.armedlogistics.ArmedLogistics;

public class TickHandler
{
    @SubscribeEvent
    public void serverTick(final TickEvent.ServerTickEvent e)
    {
        ArmedLogistics.instance.getGridManager().tickGrids();
    }
}

