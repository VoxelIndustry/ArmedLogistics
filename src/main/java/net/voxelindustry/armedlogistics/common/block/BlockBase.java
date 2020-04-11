package net.voxelindustry.armedlogistics.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.voxelindustry.armedlogistics.ArmedLogistics;

public class BlockBase extends Block
{
    public BlockBase(String name, Material material)
    {
        super(material);
        this.setRegistryName(ArmedLogistics.MODID, name);
        this.setTranslationKey(ArmedLogistics.MODID + "." + name);
        this.setCreativeTab(ArmedLogistics.TAB_ALL);
    }
}
