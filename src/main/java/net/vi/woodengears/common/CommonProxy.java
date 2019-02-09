package net.vi.woodengears.common;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.common.gui.GuiHandler;
import net.vi.woodengears.common.init.WGBlocks;
import net.vi.woodengears.common.init.WGItems;
import net.vi.woodengears.compat.CompatManager;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent e)
    {
        WGBlocks.init();

        WGItems.init();

        NetworkRegistry.INSTANCE.registerGuiHandler(WoodenGears.instance, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new TickHandler());

        CompatManager.preInit(e);
    }

    public void init(FMLInitializationEvent e)
    {
        CompatManager.init(e);

    }

    public void postInit(FMLPostInitializationEvent e)
    {
        CompatManager.postInit(e);
    }

    public void registerItemRenderer(Item item, int meta)
    {

    }
}
