package net.vi.woodengears.common.entity;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.vi.woodengears.WoodenGears;

@Mod.EventBusSubscriber(modid = WoodenGears.MODID)
public class WGEntity
{
    @SubscribeEvent
    public static void registerEntity(RegistryEvent.Register<EntityEntry> event)
    {
        EntityEntry logistic_arm =
                EntityEntryBuilder.create().entity(EntityLogisticArm.class).name("logistic_arm").id("logistic_arm",
                        0).tracker(64, 1, true).build();

        event.getRegistry().registerAll(logistic_arm);
    }
}