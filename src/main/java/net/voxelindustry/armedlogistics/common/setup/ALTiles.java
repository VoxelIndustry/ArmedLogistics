package net.voxelindustry.armedlogistics.common.setup;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.tileentity.TileEntityType.Builder;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.common.tile.TileActiveProvider;
import net.voxelindustry.armedlogistics.common.tile.TileArmReservoir;
import net.voxelindustry.armedlogistics.common.tile.TileCable;
import net.voxelindustry.armedlogistics.common.tile.TileProvider;
import net.voxelindustry.armedlogistics.common.tile.TileRequester;
import net.voxelindustry.armedlogistics.common.tile.TileStorage;

@ObjectHolder(ArmedLogistics.MODID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ALTiles
{
    @ObjectHolder("provider")
    public static TileEntityType<TileProvider> PROVIDER;

    @ObjectHolder("active_provider")
    public static TileEntityType<TileActiveProvider> ACTIVE_PROVIDER;

    @ObjectHolder("arm_reservoir")
    public static TileEntityType<TileArmReservoir> ARM_RESERVOIR;

    @ObjectHolder("requester")
    public static TileEntityType<TileRequester> REQUESTER;

    @ObjectHolder("storage")
    public static TileEntityType<TileStorage> STORAGE;

    @ObjectHolder("cable")
    public static TileEntityType<TileCable> CABLE;

    @SubscribeEvent
    public static void onTileRegister(RegistryEvent.Register<TileEntityType<?>> event)
    {
        event.getRegistry().register(Builder
                .create(TileProvider::new, ALBlocks.PROVIDER)
                .build(null)
                .setRegistryName(ArmedLogistics.MODID, "provider"));

        event.getRegistry().register(Builder
                .create(TileActiveProvider::new, ALBlocks.ACTIVE_PROVIDER)
                .build(null)
                .setRegistryName(ArmedLogistics.MODID, "active_provider"));

        event.getRegistry().register(Builder
                .create(TileStorage::new, ALBlocks.STORAGE)
                .build(null)
                .setRegistryName(ArmedLogistics.MODID, "storage"));

        event.getRegistry().register(Builder
                .create(TileRequester::new, ALBlocks.REQUESTER)
                .build(null)
                .setRegistryName(ArmedLogistics.MODID, "requester"));

        event.getRegistry().register(Builder
                .create(TileArmReservoir::new, ALBlocks.ARM_RESERVOIR)
                .build(null)
                .setRegistryName(ArmedLogistics.MODID, "arm_reservoir"));

        event.getRegistry().register(Builder
                .create(TileCable::new, ALBlocks.CABLE)
                .build(null)
                .setRegistryName(ArmedLogistics.MODID, "cable"));
    }
}
