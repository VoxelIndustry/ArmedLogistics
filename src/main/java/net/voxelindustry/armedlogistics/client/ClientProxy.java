package net.voxelindustry.armedlogistics.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.client.render.RenderLogisticArm;
import net.voxelindustry.armedlogistics.common.CommonProxy;
import net.voxelindustry.armedlogistics.common.IModelProvider;
import net.voxelindustry.armedlogistics.common.entity.EntityLogisticArm;
import net.voxelindustry.armedlogistics.common.init.WGBlocks;
import net.voxelindustry.armedlogistics.common.init.WGItems;
import net.voxelindustry.armedlogistics.common.item.IItemModelProvider;
import net.voxelindustry.brokkgui.style.StylesheetManager;

import java.util.function.BiConsumer;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent e)
    {
        super.preInit(e);

        OBJLoader.INSTANCE.addDomain(ArmedLogistics.MODID);
        MinecraftForge.EVENT_BUS.register(this);

        WGItems.ITEMS.stream().filter(IItemModelProvider.class::isInstance)
                .filter(item -> ((IItemModelProvider) item).hasSpecialModel())
                .forEach(item -> ((IItemModelProvider) item).registerVariants());

        RenderingRegistry.registerEntityRenderingHandler(EntityLogisticArm.class, RenderLogisticArm::new);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e)
    {
        super.postInit(e);

        StylesheetManager.getInstance().addUserAgent(ArmedLogistics.MODID, "/assets/" + ArmedLogistics.MODID +
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
        WGBlocks.BLOCKS.keySet().stream().filter(IModelProvider.class::isInstance).forEach(block ->
        {
            IModelProvider modelProvider = (IModelProvider) block;

            BiConsumer<Integer, Block> modelRegister = modelProvider.registerItemModels();
            for (int i = 0; i < modelProvider.getItemModelCount(); i++)
                modelRegister.accept(i, block);
        });
    }

    @SubscribeEvent
    public void onModelRegistry(ModelRegistryEvent e)
    {
        for (Item item : WGItems.ITEMS)
        {
            if (item instanceof IItemModelProvider && ((IItemModelProvider) item).hasSpecialModel())
                ((IItemModelProvider) item).registerModels();
            else
                ArmedLogistics.proxy.registerItemRenderer(item, 0);
        }
    }
}
