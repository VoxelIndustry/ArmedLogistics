package net.opmcorp.woodengears.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.opmcorp.woodengears.common.entity.EntityLogisticArm;

import javax.annotation.Nullable;

public class RenderLogisticArm extends Render<EntityLogisticArm>
{
    public RenderLogisticArm(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityLogisticArm entity)
    {
        return new ResourceLocation("textures/entity/minecart"); // TODO Change texture
    }
}