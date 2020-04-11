package net.voxelindustry.armedlogistics.common.item;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.voxelindustry.armedlogistics.ArmedLogistics;

import java.util.HashMap;
import java.util.Map;

public class ItemBase extends Item implements IItemModelProvider
{
    private Map<String, ModelResourceLocation> variants;

    public ItemBase(String name)
    {
        this.setRegistryName(ArmedLogistics.MODID, name);
        this.setTranslationKey(ArmedLogistics.MODID + "." + name);
        this.setCreativeTab(ArmedLogistics.TAB_ALL);

        this.variants = new HashMap<>();
    }

    protected void addVariant(String name, ModelResourceLocation model)
    {
        this.variants.put(name, model);
    }

    protected ModelResourceLocation getVariantModel(String name)
    {
        return this.variants.get(name);
    }

    @Override
    public void registerVariants()
    {
        ModelBakery.registerItemVariants(this,
                variants.values().toArray(new ModelResourceLocation[0]));
    }
}
