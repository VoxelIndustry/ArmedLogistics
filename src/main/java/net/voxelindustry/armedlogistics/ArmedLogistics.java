package net.voxelindustry.armedlogistics;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.voxelindustry.armedlogistics.common.CommonProxy;
import net.voxelindustry.armedlogistics.common.CustomCreativeTab;
import net.voxelindustry.brokkgui.BrokkGuiPlatform;
import net.voxelindustry.steamlayer.grid.GridManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ArmedLogistics.MODID, name = ArmedLogistics.NAME, version = ArmedLogistics.VERSION)
public class ArmedLogistics
{
    public static final String MODID   = "armedlogistics";
    public static final String NAME    = "Armed Logistics";
    public static final String VERSION = "0.1.0";

    @Mod.Instance(MODID)
    public static ArmedLogistics instance;

    public static final CreativeTabs TAB_ALL = new CustomCreativeTab(MODID);

    public static Logger logger;

    @SidedProxy(clientSide = "net.voxelindustry.armedlogistics.client.ClientProxy",
            serverSide = "net.voxelindustry.armedlogistics.common.CommonProxy")
    public static CommonProxy proxy;

    private GridManager gridManager;

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

        BrokkGuiPlatform.getInstance().enableRenderDebug(true);
    }

    public GridManager getGridManager()
    {
        if (gridManager == null)
            this.gridManager = GridManager.createGetInstance(ArmedLogistics.MODID);
        return this.gridManager;
    }
}
