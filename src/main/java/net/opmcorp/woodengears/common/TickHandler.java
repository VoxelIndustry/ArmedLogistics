package net.opmcorp.woodengears.common;

import com.google.common.collect.Queues;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.opmcorp.woodengears.common.grid.GridManager;
import net.opmcorp.woodengears.common.tile.ILoadable;

import java.util.Queue;

public class TickHandler
{
    public static final Queue<ILoadable> loadables = Queues.newArrayDeque();

    @SubscribeEvent
    public void serverTick(final TickEvent.ServerTickEvent e)
    {
        while (TickHandler.loadables.peek() != null)
            TickHandler.loadables.poll().load();

        GridManager.getInstance().tickGrids();
    }
}

