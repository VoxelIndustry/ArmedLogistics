package net.voxelindustry.armedlogistics.client;

import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.client.render.RenderLogisticArm;
import net.voxelindustry.armedlogistics.common.entity.EntityLogisticArm;
import net.voxelindustry.armedlogistics.common.setup.ALContainers;
import net.voxelindustry.armedlogistics.common.setup.IProxy;
import net.voxelindustry.brokkgui.style.StylesheetManager;

public class ClientProxy implements IProxy
{
    @Override
    public void setup(FMLCommonSetupEvent e)
    {
        OBJLoader.INSTANCE.addDomain(ArmedLogistics.MODID);

        ALContainers.registerScreens();

        RenderingRegistry.registerEntityRenderingHandler(EntityLogisticArm.class, RenderLogisticArm::new);

        StylesheetManager.getInstance().addUserAgent(ArmedLogistics.MODID, "/assets/" + ArmedLogistics.MODID +
                "/css/theme.css");
    }
}
