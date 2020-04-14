package net.voxelindustry.armedlogistics.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.voxelindustry.armedlogistics.ArmedLogistics;

@Mod.EventBusSubscriber(modid = ArmedLogistics.MODID)
public class WGEntity
{

    public static EntityType<Entity> LOGISTIC_ARM;

    @SubscribeEvent
    public static void registerEntity(RegistryEvent.Register<EntityType<?>> event)
    {
        WGEntity.LOGISTIC_ARM = EntityType.Builder.create(EntityLogisticArm::new, EntityClassification.MISC)
                .size(0.3525F, 1.0F)
                .setTrackingRange(64)
                .setUpdateInterval(1)
                .setShouldReceiveVelocityUpdates(true)
                .immuneToFire()
                .build("logistic_arm");

        event.getRegistry().registerAll(LOGISTIC_ARM);
    }
}