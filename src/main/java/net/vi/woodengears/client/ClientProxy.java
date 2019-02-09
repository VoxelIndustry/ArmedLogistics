package net.vi.woodengears.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.client.render.RenderLogisticArm;
import net.vi.woodengears.common.CommonProxy;
import net.vi.woodengears.common.IModelProvider;
import net.vi.woodengears.common.entity.EntityLogisticArm;
import net.vi.woodengears.common.init.WGBlocks;
import net.vi.woodengears.common.init.WGItems;
import net.vi.woodengears.common.item.IItemModelProvider;
import net.voxelindustry.brokkgui.style.StylesheetManager;

import java.util.function.BiConsumer;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent e)
    {
        super.preInit(e);

        OBJLoader.INSTANCE.addDomain(WoodenGears.MODID);
        MinecraftForge.EVENT_BUS.register(this);

        WGItems.ITEMS.stream().filter(IItemModelProvider.class::isInstance)
                .forEach(item -> ((IItemModelProvider) item).registerVariants());

        RenderingRegistry.registerEntityRenderingHandler(EntityLogisticArm.class, RenderLogisticArm::new);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e)
    {
        StylesheetManager.getInstance().addUserAgent(WoodenGears.MODID, "/assets/" + WoodenGears.MODID +
                "/css/theme.css");
    }

    @Override
    public void registerItemRenderer(Item item, int meta)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent e)
    {
        for (Item item : WGItems.ITEMS)
        {
            if (item instanceof IItemModelProvider && ((IItemModelProvider) item).hasSpecialModel())
                ((IItemModelProvider) item).registerModels();
            else
                WoodenGears.proxy.registerItemRenderer(item, 0);
        }

        WGBlocks.BLOCKS.keySet().stream().filter(IModelProvider.class::isInstance).forEach(block ->
        {
            IModelProvider modelProvider = (IModelProvider) block;

            BiConsumer<Integer, Block> modelRegister = modelProvider.registerItemModels();
            for (int i = 0; i < modelProvider.getItemModelCount(); i++)
                modelRegister.accept(i, block);
        });
    }
}
