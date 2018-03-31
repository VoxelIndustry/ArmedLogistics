package net.opmcorp.woodengears.common;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.opmcorp.woodengears.WoodenGears;
import net.opmcorp.woodengears.common.gui.GuiHandler;
import net.opmcorp.woodengears.common.init.WGBlocks;
import net.opmcorp.woodengears.common.init.WGItems;
import net.opmcorp.woodengears.common.network.ContainerUpdatePacket;
import net.opmcorp.woodengears.common.network.WGPacketHandler;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent e)
    {
        WGPacketHandler.INSTANCE.registerMessage(
                ContainerUpdatePacket.ContainerUpdatePacketHandler.class,
                ContainerUpdatePacket.class,
                0, Side.CLIENT);

        WGBlocks.init();

        WGItems.init();

        NetworkRegistry.INSTANCE.registerGuiHandler(WoodenGears.instance, new GuiHandler());
    }

    public void init(FMLInitializationEvent e)
    {

    }

    public void postInit(FMLPostInitializationEvent e)
    {

    }

    public void registerItemRenderer(Item item, int meta)
    {

    }
}
