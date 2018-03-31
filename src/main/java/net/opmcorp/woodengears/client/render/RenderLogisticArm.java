package net.opmcorp.woodengears.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.opmcorp.woodengears.WoodenGears;
import net.opmcorp.woodengears.common.entity.EntityLogisticArm;

import javax.annotation.Nullable;

public class RenderLogisticArm extends Render<EntityLogisticArm>
{
    protected ModelLogisticArm modelLogisticArm = new ModelLogisticArm();

    public RenderLogisticArm(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Override
    public void doRender(EntityLogisticArm logisticArm, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x + 0.25F, (float)y + 0.75F, (float)z);

        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(0.5F, 0.5F, 0.5F);

        this.bindEntityTexture(logisticArm);
        this.modelLogisticArm.render(logisticArm, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

        GlStateManager.popMatrix();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityLogisticArm entity)
    {
        return new ResourceLocation(WoodenGears.MODID,"textures/models/logistic_arm.png");
    }
}