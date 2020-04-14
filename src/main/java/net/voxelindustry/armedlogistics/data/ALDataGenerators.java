package net.voxelindustry.armedlogistics.data;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.voxelindustry.armedlogistics.ArmedLogistics;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ALDataGenerators
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        event.getGenerator().addProvider(new ALModelProvider(event.getGenerator(), ArmedLogistics.MODID, event.getExistingFileHelper()));
    }
}
