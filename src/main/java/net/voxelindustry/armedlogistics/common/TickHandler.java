package net.voxelindustry.armedlogistics.common;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.voxelindustry.armedlogistics.ArmedLogistics;

public class TickHandler
{
    @SubscribeEvent
    public void serverTick(final TickEvent.ServerTickEvent e)
    {
        ArmedLogistics.instance.getGridManager().tickGrids();
    }
}

