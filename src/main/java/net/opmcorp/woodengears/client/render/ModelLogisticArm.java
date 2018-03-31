package net.opmcorp.woodengears.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.opmcorp.woodengears.common.entity.EntityLogisticArm;

public class ModelLogisticArm extends ModelBase
{
    ModelRenderer core1;
    ModelRenderer core2;
    ModelRenderer core3;
    ModelRenderer core4;
    ModelRenderer core5;
    ModelRenderer core6;
    ModelRenderer core7;
    ModelRenderer core8;
    ModelRenderer core9;
    ModelRenderer core10;
    ModelRenderer core11;
    ModelRenderer core12;
    ModelRenderer core13;
    ModelRenderer core14;
    ModelRenderer core15;
    ModelRenderer core16;
    ModelRenderer core17;
    ModelRenderer core18;

    public ModelLogisticArm()
    {
        core1 = new ModelRenderer(this, 20, 11);
        core1.setTextureSize(64, 32);
        core1.addBox(-5.5F, -2F, -5.5F, 11, 4, 11);
        core1.setRotationPoint(8F, -6F, 0F);
        core2 = new ModelRenderer(this, 28, 0);
        core2.setTextureSize(64, 32);
        core2.addBox(-4.5F, -1F, -4.5F, 9, 2, 9);
        core2.setRotationPoint(8F, -3F, 0F);
        core3 = new ModelRenderer(this, 0, 0);
        core3.setTextureSize(64, 32);
        core3.addBox(-1.5F, -8F, -1.5F, 3, 16, 3);
        core3.setRotationPoint(5.5F, 6F, 0F);
        core4 = new ModelRenderer(this, 0, 0);
        core4.setTextureSize(64, 32);
        core4.addBox(-1.5F, -8F, -1.5F, 3, 16, 3);
        core4.setRotationPoint(10.5F, 6F, 0F);
        core5 = new ModelRenderer(this, 12, 0);
        core5.setTextureSize(64, 32);
        core5.addBox(-1F, -10F, -1F, 2, 20, 2);
        core5.setRotationPoint(5.5F, 4.5F, 0F);
        core6 = new ModelRenderer(this, 12, 0);
        core6.setTextureSize(64, 32);
        core6.addBox(-1F, -10F, -1F, 2, 20, 2);
        core6.setRotationPoint(10.5F, 4.5F, 0F);
        core7 = new ModelRenderer(this, 20, 0);
        core7.setTextureSize(64, 32);
        core7.addBox(-0.5F, -10.5F, -0.5F, 1, 21, 1);
        core7.setRotationPoint(5.5F, 4.5F, 0F);
        core8 = new ModelRenderer(this, 20, 0);
        core8.setTextureSize(64, 32);
        core8.addBox(-0.5F, -10.5F, -0.5F, 1, 21, 1);
        core8.setRotationPoint(10.5F, 4.5F, 0F);
        core9 = new ModelRenderer(this, 0, 25);
        core9.setTextureSize(64, 32);
        core9.addBox(-4F, -2F, -1.5F, 8, 4, 3);
        core9.setRotationPoint(8F, 17.5F, 0F);
        core10 = new ModelRenderer(this, 23, 29);
        core10.setTextureSize(64, 32);
        core10.addBox(-1F, -0.5F, -1F, 2, 1, 2);
        core10.setRotationPoint(10.5F, 15.5F, 0F);
        core11 = new ModelRenderer(this, 23, 29);
        core11.setTextureSize(64, 32);
        core11.addBox(-1F, -0.5F, -1F, 2, 1, 2);
        core11.setRotationPoint(5.5F, 15.5F, 0F);
        core12 = new ModelRenderer(this, 4, 19);
        core12.setTextureSize(64, 32);
        core12.addBox(-0.5F, -0.5F, -2F, 1, 1, 4);
        core12.setRotationPoint(8F, 19.5F, 0F);
        core13 = new ModelRenderer(this, 0, 20);
        core13.setTextureSize(64, 32);
        core13.addBox(-0.5F, -1.5F, -0.5F, 1, 3, 1);
        core13.setRotationPoint(8F, 21F, -2.5F);
        core14 = new ModelRenderer(this, 14, 22);
        core14.setTextureSize(64, 32);
        core14.addBox(-0.5F, -0.5F, -1F, 1, 1, 2);
        core14.setRotationPoint(8F, 22.85F, -1.94F);
        core15 = new ModelRenderer(this, 0, 20);
        core15.setTextureSize(64, 32);
        core15.addBox(-0.5F, -1.5F, -0.5F, 1, 3, 1);
        core15.setRotationPoint(8F, 21F, 2.5F);
        core16 = new ModelRenderer(this, 14, 22);
        core16.setTextureSize(64, 32);
        core16.addBox(-0.5F, -0.5F, -1F, 1, 1, 2);
        core16.setRotationPoint(8F, 22.85F, 1.94F);
        core17 = new ModelRenderer(this, 50, 27);
        core17.setTextureSize(64, 32);
        core17.addBox(-3F, -2F, -0.5F, 6, 4, 1);
        core17.setRotationPoint(8F, 17.5F, -2F);
        core18 = new ModelRenderer(this, 50, 27);
        core18.setTextureSize(64, 32);
        core18.addBox(-3F, -2F, -0.5F, 6, 4, 1);
        core18.setRotationPoint(8F, 17.5F, 2F);
    }

    public void render(EntityLogisticArm entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        core1.rotateAngleX = 0F;
        core1.rotateAngleY = 0F;
        core1.rotateAngleZ = 0F;
        core1.renderWithRotation(par7);

        core2.rotateAngleX = 0F;
        core2.rotateAngleY = 0F;
        core2.rotateAngleZ = 0F;
        core2.renderWithRotation(par7);

        core3.rotateAngleX = 0F;
        core3.rotateAngleY = 0F;
        core3.rotateAngleZ = 0F;
        core3.renderWithRotation(par7);

        core4.rotateAngleX = 0F;
        core4.rotateAngleY = 0F;
        core4.rotateAngleZ = 0F;
        core4.renderWithRotation(par7);

        core5.rotateAngleX = 0F;
        core5.rotateAngleY = 0F;
        core5.rotateAngleZ = 0F;
        core5.renderWithRotation(par7);

        core6.rotateAngleX = 0F;
        core6.rotateAngleY = 0F;
        core6.rotateAngleZ = 0F;
        core6.renderWithRotation(par7);

        core7.rotateAngleX = 0F;
        core7.rotateAngleY = 0F;
        core7.rotateAngleZ = 0F;
        core7.renderWithRotation(par7);

        core8.rotateAngleX = 0F;
        core8.rotateAngleY = 0F;
        core8.rotateAngleZ = 0F;
        core8.renderWithRotation(par7);

        core9.rotateAngleX = 0F;
        core9.rotateAngleY = 0F;
        core9.rotateAngleZ = 0F;
        core9.renderWithRotation(par7);

        core10.rotateAngleX = 0F;
        core10.rotateAngleY = 0F;
        core10.rotateAngleZ = 0F;
        core10.renderWithRotation(par7);

        core11.rotateAngleX = 0F;
        core11.rotateAngleY = 0F;
        core11.rotateAngleZ = 0F;
        core11.renderWithRotation(par7);

        core12.rotateAngleX = 0F;
        core12.rotateAngleY = 3.141593F;
        core12.rotateAngleZ = 0F;
        core12.renderWithRotation(par7);

        core13.rotateAngleX = 0F;
        core13.rotateAngleY = 1.748455E-07F;
        core13.rotateAngleZ = 0F;
        core13.renderWithRotation(par7);

        core14.rotateAngleX = 0.7853982F;
        core14.rotateAngleY = -3.141593F;
        core14.rotateAngleZ = -3.141593F;
        core14.renderWithRotation(par7);

        core15.rotateAngleX = 0F;
        core15.rotateAngleY = -3.141593F;
        core15.rotateAngleZ = 0F;
        core15.renderWithRotation(par7);

        core16.rotateAngleX = 0.7853982F;
        core16.rotateAngleY = 1.748455E-07F;
        core16.rotateAngleZ = -3.141593F;
        core16.renderWithRotation(par7);

        core17.rotateAngleX = 0F;
        core17.rotateAngleY = 0F;
        core17.rotateAngleZ = 0F;
        core17.renderWithRotation(par7);

        core18.rotateAngleX = 1.57853E-24F;
        core18.rotateAngleY = 3.141593F;
        core18.rotateAngleZ = -3.141593F;
        core18.renderWithRotation(par7);
    }
}
