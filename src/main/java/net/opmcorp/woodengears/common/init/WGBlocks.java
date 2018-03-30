package net.opmcorp.woodengears.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.opmcorp.woodengears.WoodenGears;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@GameRegistry.ObjectHolder(WoodenGears.MODID)
public class WGBlocks
{
    public static Map<Block, ItemBlock> BLOCKS;

    public static void init()
    {
        BLOCKS = new LinkedHashMap<>();

        MinecraftForge.EVENT_BUS.register(new WGBlocks());
    }

    @SubscribeEvent
    public void onBlockRegister(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(BLOCKS.keySet().toArray(new Block[BLOCKS.size()]));
    }

    static <T extends Block> void registerBlock(T block)
    {
        registerBlock(block, ItemBlock::new);
    }

    static <T extends Block> void registerBlock(T block, Function<T, ItemBlock> supplier)
    {
        final ItemBlock supplied = supplier.apply(block);
        supplied.setRegistryName(block.getRegistryName());

        BLOCKS.put(block, supplied);
    }

    static void registerTile(Class<? extends TileEntity> c, String name)
    {
        GameRegistry.registerTileEntity(c, WoodenGears.MODID + ":" + name);
    }
}
