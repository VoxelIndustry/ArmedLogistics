package net.voxelindustry.armedlogistics;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.voxelindustry.armedlogistics.client.ClientProxy;
import net.voxelindustry.armedlogistics.common.ServerProxy;
import net.voxelindustry.armedlogistics.common.TickHandler;
import net.voxelindustry.armedlogistics.common.setup.ALBlocks;
import net.voxelindustry.armedlogistics.common.setup.IProxy;
import net.voxelindustry.armedlogistics.compat.CompatManager;
import net.voxelindustry.brokkgui.BrokkGuiPlatform;
import net.voxelindustry.steamlayer.common.container.CustomCreativeTab;
import net.voxelindustry.steamlayer.grid.GridManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ArmedLogistics.MODID)
public class ArmedLogistics
{
    public static final String MODID   = "armedlogistics";
    public static final String NAME    = "Armed Logistics";
    public static final String VERSION = "0.1.0";

    public static ArmedLogistics instance;

    public static final ItemGroup TAB_ALL = new CustomCreativeTab(MODID, () -> new ItemStack(ALBlocks.PROVIDER));

    public static Logger logger;

    public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    private GridManager gridManager;

    public ArmedLogistics()
    {
        instance = this;
        logger = LogManager.getLogger(ArmedLogistics.class);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent e)
    {
        proxy.setup(e);

        CompatManager.setup(e);

        gridManager = GridManager.createGetInstance(MODID);

        MinecraftForge.EVENT_BUS.register(new TickHandler());

        BrokkGuiPlatform.getInstance().enableRenderDebug(true);
    }

    public GridManager getGridManager()
    {
        if (gridManager == null)
            gridManager = GridManager.createGetInstance(ArmedLogistics.MODID);
        return gridManager;
    }
}
