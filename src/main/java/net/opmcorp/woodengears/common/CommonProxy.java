package net.opmcorp.woodengears.common;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.opmcorp.woodengears.common.init.WGBlocks;
import net.opmcorp.woodengears.common.init.WGItems;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent e)
    {
        WGBlocks.init();

        WGItems.init();
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
