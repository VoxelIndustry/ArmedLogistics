package net.vi.woodengears.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CustomCreativeTab extends CreativeTabs
{
    public CustomCreativeTab(final String label)
    {
        super(label);
    }

    @Override
    public ItemStack createIcon()
    {
        return new ItemStack(Items.BREAD);
    }
}
