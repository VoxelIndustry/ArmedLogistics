package net.voxelindustry.armedlogistics.common.setup;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.Builder;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.common.entity.EntityLogisticArm;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ALEntity
{

    @ObjectHolder(ArmedLogistics.MODID + ":logistic_arm")
    public static EntityType<Entity> LOGISTIC_ARM;

    @SubscribeEvent
    public static void registerEntity(RegistryEvent.Register<EntityType<?>> event)
    {
        EntityType<Entity> logistic_arm = Builder.create(EntityLogisticArm::new, EntityClassification.MISC)
                .size(0.3525F, 1.0F)
                .setTrackingRange(64)
                .setUpdateInterval(1)
                .setShouldReceiveVelocityUpdates(true)
                .immuneToFire()
                .build("logistic_arm");
        logistic_arm.setRegistryName(ArmedLogistics.MODID, "logistic_arm");

        event.getRegistry().registerAll(logistic_arm);
    }
}