package net.opmcorp.woodengears;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.opmcorp.woodengears.common.CommonProxy;
import org.apache.logging.log4j.Logger;

@Mod(modid = WoodenGears.MODID, name = WoodenGears.NAME, version = WoodenGears.VERSION)
public class WoodenGears
{
    public static final String MODID   = "woodengears";
    public static final String NAME    = "Wooden Gears";
    public static final String VERSION = "0.1.0";

    @Mod.Instance(MODID)
    public static WoodenGears instance;

    public static Logger logger;

    @SidedProxy(clientSide = "net.opmcorp.woodengears.client.ClientProxy",
            serverSide = "net.opmcorp.woodengears.common.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }
}
