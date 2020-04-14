package net.voxelindustry.armedlogistics.common.setup;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.client.gui.GuiArmReservoir;
import net.voxelindustry.armedlogistics.client.gui.GuiProvider;
import net.voxelindustry.armedlogistics.client.gui.GuiRequester;
import net.voxelindustry.brokkgui.wrapper.impl.BrokkGuiManager;
import net.voxelindustry.steamlayer.container.BuiltContainer;
import net.voxelindustry.steamlayer.container.SteamLayerContainerFactory;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

@EventBusSubscriber(bus = MOD)
@ObjectHolder(ArmedLogistics.MODID)
public class ALContainers
{
    @ObjectHolder("arm_reservoir")
    public static ContainerType<BuiltContainer> ARM_RESERVOIR;
    @ObjectHolder("provider")
    public static ContainerType<BuiltContainer> PROVIDER;
    @ObjectHolder("requester")
    public static ContainerType<BuiltContainer> REQUESTER;
    @ObjectHolder("storage")
    public static ContainerType<BuiltContainer> STORAGE;

    public static void registerScreens()
    {
        ScreenManager.registerFactory(ARM_RESERVOIR, BrokkGuiManager.getContainerFactory(ArmedLogistics.MODID, GuiArmReservoir::new));
        ScreenManager.registerFactory(PROVIDER, BrokkGuiManager.getContainerFactory(ArmedLogistics.MODID, GuiProvider::new));
        ScreenManager.registerFactory(REQUESTER, BrokkGuiManager.getContainerFactory(ArmedLogistics.MODID, GuiRequester::new));
        ScreenManager.registerFactory(STORAGE, BrokkGuiManager.getContainerFactory(ArmedLogistics.MODID, GuiProvider::new));
    }

    @SubscribeEvent
    public static void onContainerRegister(Register<ContainerType<?>> event)
    {
        event.getRegistry().register(SteamLayerContainerFactory.create().setRegistryName(ArmedLogistics.MODID, "arm_reservoir"));
        event.getRegistry().register(SteamLayerContainerFactory.create().setRegistryName(ArmedLogistics.MODID, "provider"));
        event.getRegistry().register(SteamLayerContainerFactory.create().setRegistryName(ArmedLogistics.MODID, "requester"));
        event.getRegistry().register(SteamLayerContainerFactory.create().setRegistryName(ArmedLogistics.MODID, "storage"));
    }
}

