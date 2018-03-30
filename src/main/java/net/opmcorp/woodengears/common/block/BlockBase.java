package net.opmcorp.woodengears.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.opmcorp.woodengears.WoodenGears;

public class BlockBase extends Block
{
    public BlockBase(String name, Material material)
    {
        super(material);
        this.setRegistryName(WoodenGears.MODID, name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(WoodenGears.TAB_ALL);
    }
}
