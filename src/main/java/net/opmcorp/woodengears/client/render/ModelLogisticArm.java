package net.opmcorp.woodengears.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLogisticArm extends ModelBase
{
    private ModelRenderer mainPedestal;
    private ModelRenderer secondPedestal;

    private ModelRenderer bodyPiston1;
    private ModelRenderer bodyPiston2;

    private ModelRenderer piston1;
    private ModelRenderer piston2;

    private ModelRenderer rodPiston1;
    private ModelRenderer rodPiston2;

    private ModelRenderer mainSupportPliers;

    private ModelRenderer headPiston1;
    private ModelRenderer headPiston2;

    private ModelRenderer mainPliers;
    private ModelRenderer topPhalanxPliers1;
    private ModelRenderer bottomPhalanxPliers1;
    private ModelRenderer topPhalanxPliers2;
    private ModelRenderer bottomPhlanxPliers2;

    private ModelRenderer secondSupportPliers1;
    private ModelRenderer secondSupportPliers2;

    public ModelLogisticArm()
    {
        mainPedestal = new ModelRenderer(this, 20, 11);
        mainPedestal.setTextureSize(64, 32);
        mainPedestal.addBox(-5.5F, -2F, -5.5F, 11, 4, 11);
        mainPedestal.setRotationPoint(8F, -6F, 0F);
        secondPedestal = new ModelRenderer(this, 28, 0);
        secondPedestal.setTextureSize(64, 32);
        secondPedestal.addBox(-4.5F, -1F, -4.5F, 9, 2, 9);
        secondPedestal.setRotationPoint(8F, -3F, 0F);
        bodyPiston1 = new ModelRenderer(this, 0, 0);
        bodyPiston1.setTextureSize(64, 32);
        bodyPiston1.addBox(-1.5F, -8F, -1.5F, 3, 16, 3);
        bodyPiston1.setRotationPoint(5.5F, 6F, 0F);
        bodyPiston2 = new ModelRenderer(this, 0, 0);
        bodyPiston2.setTextureSize(64, 32);
        bodyPiston2.addBox(-1.5F, -8F, -1.5F, 3, 16, 3);
        bodyPiston2.setRotationPoint(10.5F, 6F, 0F);
        piston1 = new ModelRenderer(this, 12, 0);
        piston1.setTextureSize(64, 32);
        piston1.addBox(-1F, -10F, -1F, 2, 20, 2);
        piston1.setRotationPoint(5.5F, 4.5F, 0F);
        piston2 = new ModelRenderer(this, 12, 0);
        piston2.setTextureSize(64, 32);
        piston2.addBox(-1F, -10F, -1F, 2, 20, 2);
        piston2.setRotationPoint(10.5F, 4.5F, 0F);
        rodPiston1 = new ModelRenderer(this, 20, 0);
        rodPiston1.setTextureSize(64, 32);
        rodPiston1.addBox(-0.5F, -10.5F, -0.5F, 1, 21, 1);
        rodPiston1.setRotationPoint(5.5F, 4.5F, 0F);
        rodPiston2 = new ModelRenderer(this, 20, 0);
        rodPiston2.setTextureSize(64, 32);
        rodPiston2.addBox(-0.5F, -10.5F, -0.5F, 1, 21, 1);
        rodPiston2.setRotationPoint(10.5F, 4.5F, 0F);
        mainSupportPliers = new ModelRenderer(this, 0, 25);
        mainSupportPliers.setTextureSize(64, 32);
        mainSupportPliers.addBox(-4F, -2F, -1.5F, 8, 4, 3);
        mainSupportPliers.setRotationPoint(8F, 17.5F, 0F);
        headPiston1 = new ModelRenderer(this, 23, 29);
        headPiston1.setTextureSize(64, 32);
        headPiston1.addBox(-1F, -0.5F, -1F, 2, 1, 2);
        headPiston1.setRotationPoint(10.5F, 15.5F, 0F);
        headPiston2 = new ModelRenderer(this, 23, 29);
        headPiston2.setTextureSize(64, 32);
        headPiston2.addBox(-1F, -0.5F, -1F, 2, 1, 2);
        headPiston2.setRotationPoint(5.5F, 15.5F, 0F);
        mainPliers = new ModelRenderer(this, 4, 19);
        mainPliers.setTextureSize(64, 32);
        mainPliers.addBox(-0.5F, -0.5F, -2F, 1, 1, 4);
        mainPliers.setRotationPoint(8F, 19.5F, 0F);
        topPhalanxPliers1 = new ModelRenderer(this, 0, 20);
        topPhalanxPliers1.setTextureSize(64, 32);
        topPhalanxPliers1.addBox(-0.5F, -1.5F, -0.5F, 1, 3, 1);
        topPhalanxPliers1.setRotationPoint(8F, 21F, -2.5F);
        bottomPhalanxPliers1 = new ModelRenderer(this, 14, 22);
        bottomPhalanxPliers1.setTextureSize(64, 32);
        bottomPhalanxPliers1.addBox(-0.5F, -0.5F, -1F, 1, 1, 2);
        bottomPhalanxPliers1.setRotationPoint(8F, 22.85F, -1.94F);
        topPhalanxPliers2 = new ModelRenderer(this, 0, 20);
        topPhalanxPliers2.setTextureSize(64, 32);
        topPhalanxPliers2.addBox(-0.5F, -1.5F, -0.5F, 1, 3, 1);
        topPhalanxPliers2.setRotationPoint(8F, 21F, 2.5F);
        bottomPhlanxPliers2 = new ModelRenderer(this, 14, 22);
        bottomPhlanxPliers2.setTextureSize(64, 32);
        bottomPhlanxPliers2.addBox(-0.5F, -0.5F, -1F, 1, 1, 2);
        bottomPhlanxPliers2.setRotationPoint(8F, 22.85F, 1.94F);
        secondSupportPliers1 = new ModelRenderer(this, 50, 27);
        secondSupportPliers1.setTextureSize(64, 32);
        secondSupportPliers1.addBox(-3F, -2F, -0.5F, 6, 4, 1);
        secondSupportPliers1.setRotationPoint(8F, 17.5F, -2F);
        secondSupportPliers2 = new ModelRenderer(this, 50, 27);
        secondSupportPliers2.setTextureSize(64, 32);
        secondSupportPliers2.addBox(-3F, -2F, -0.5F, 6, 4, 1);
        secondSupportPliers2.setRotationPoint(8F, 17.5F, 2F);
    }

    public void renderPedestal(float scaleFactor)
    {
        mainPedestal.rotateAngleX = 0F;
        mainPedestal.rotateAngleY = 0F;
        mainPedestal.rotateAngleZ = 0F;
        mainPedestal.renderWithRotation(scaleFactor);

        secondPedestal.rotateAngleX = mainPedestal.rotateAngleX;
        secondPedestal.rotateAngleY = mainPedestal.rotateAngleY;
        secondPedestal.rotateAngleZ = mainPedestal.rotateAngleZ;
        secondPedestal.renderWithRotation(scaleFactor);
    }

    public void renderFirstPiston(float scaleFactor)
    {
        bodyPiston1.rotateAngleX = secondPedestal.rotateAngleX;
        bodyPiston1.rotateAngleY = secondPedestal.rotateAngleY;
        bodyPiston1.rotateAngleZ = secondPedestal.rotateAngleZ;
        bodyPiston1.renderWithRotation(scaleFactor);

        bodyPiston2.rotateAngleX = bodyPiston1.rotateAngleX;
        bodyPiston2.rotateAngleY = bodyPiston1.rotateAngleY;
        bodyPiston2.rotateAngleZ = bodyPiston1.rotateAngleZ;
        bodyPiston2.renderWithRotation(scaleFactor);
    }

    public void renderSecondPiston(float scaleFactor)
    {
        piston1.rotateAngleX = bodyPiston2.rotateAngleX;
        piston1.rotateAngleY = bodyPiston2.rotateAngleY;
        piston1.rotateAngleZ = bodyPiston2.rotateAngleZ;
        piston1.renderWithRotation(scaleFactor);

        piston2.rotateAngleX = bodyPiston2.rotateAngleX;
        piston2.rotateAngleY = bodyPiston2.rotateAngleY;
        piston2.rotateAngleZ = bodyPiston2.rotateAngleZ;
        piston2.renderWithRotation(scaleFactor);
    }

    public void renderHead(float scaleFactor)
    {
        rodPiston1.rotateAngleX = piston1.rotateAngleX;
        rodPiston1.rotateAngleY = piston1.rotateAngleY;
        rodPiston1.rotateAngleZ = piston1.rotateAngleZ;
        rodPiston1.renderWithRotation(scaleFactor);

        rodPiston2.rotateAngleX = bodyPiston2.rotateAngleX;
        rodPiston2.rotateAngleY = bodyPiston2.rotateAngleY;
        rodPiston2.rotateAngleZ = bodyPiston2.rotateAngleZ;
        rodPiston2.renderWithRotation(scaleFactor);

        mainSupportPliers.rotateAngleX = 0F;
        mainSupportPliers.rotateAngleY = 0F;
        mainSupportPliers.rotateAngleZ = 0F;
        mainSupportPliers.renderWithRotation(scaleFactor);

        headPiston1.rotateAngleX = rodPiston1.rotateAngleX;
        headPiston1.rotateAngleY = rodPiston1.rotateAngleY;
        headPiston1.rotateAngleZ = rodPiston1.rotateAngleZ;
        headPiston1.renderWithRotation(scaleFactor);

        headPiston2.rotateAngleX = rodPiston2.rotateAngleX;
        headPiston2.rotateAngleY = rodPiston2.rotateAngleY;
        headPiston2.rotateAngleZ = rodPiston2.rotateAngleZ;
        headPiston2.renderWithRotation(scaleFactor);

        mainPliers.rotateAngleX = 0F;
        mainPliers.rotateAngleY = 3.141593F;
        mainPliers.rotateAngleZ = 0F;
        mainPliers.renderWithRotation(scaleFactor);

        topPhalanxPliers1.rotateAngleX = 0F;
        topPhalanxPliers1.rotateAngleY = 1.748455E-07F;
        topPhalanxPliers1.rotateAngleZ = 0F;
        topPhalanxPliers1.renderWithRotation(scaleFactor);

        bottomPhalanxPliers1.rotateAngleX = 0.7853982F;
        bottomPhalanxPliers1.rotateAngleY = -3.141593F;
        bottomPhalanxPliers1.rotateAngleZ = -3.141593F;
        bottomPhalanxPliers1.renderWithRotation(scaleFactor);

        topPhalanxPliers2.rotateAngleX = 0F;
        topPhalanxPliers2.rotateAngleY = -3.141593F;
        topPhalanxPliers2.rotateAngleZ = 0F;
        topPhalanxPliers2.renderWithRotation(scaleFactor);

        bottomPhlanxPliers2.rotateAngleX = 0.7853982F;
        bottomPhlanxPliers2.rotateAngleY = 1.748455E-07F;
        bottomPhlanxPliers2.rotateAngleZ = -3.141593F;
        bottomPhlanxPliers2.renderWithRotation(scaleFactor);

        secondSupportPliers1.rotateAngleX = 0F;
        secondSupportPliers1.rotateAngleY = 0F;
        secondSupportPliers1.rotateAngleZ = 0F;
        secondSupportPliers1.renderWithRotation(scaleFactor);

        secondSupportPliers2.rotateAngleX = 1.57853E-24F;
        secondSupportPliers2.rotateAngleY = 3.141593F;
        secondSupportPliers2.rotateAngleZ = -3.141593F;
        secondSupportPliers2.renderWithRotation(scaleFactor);
    }
}