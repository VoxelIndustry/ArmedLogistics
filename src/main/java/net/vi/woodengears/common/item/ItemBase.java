package net.vi.woodengears.common.item;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.vi.woodengears.WoodenGears;

import java.util.HashMap;
import java.util.Map;

public class ItemBase extends Item implements IItemModelProvider
{
    private Map<String, ModelResourceLocation> variants;

    public ItemBase(String name)
    {
        this.setRegistryName(WoodenGears.MODID, name);
        this.setTranslationKey(name);
        this.setCreativeTab(WoodenGears.TAB_ALL);

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
                variants.values().toArray(new ModelResourceLocation[variants.size()]));
    }
}
