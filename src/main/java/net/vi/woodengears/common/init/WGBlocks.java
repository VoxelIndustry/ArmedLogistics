package net.vi.woodengears.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.common.block.*;
import net.vi.woodengears.common.tile.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@GameRegistry.ObjectHolder(WoodenGears.MODID)
public class WGBlocks
{
    public static Map<Block, ItemBlock> BLOCKS;

    @GameRegistry.ObjectHolder("blockcable")
    public static BlockCable CABLE;

    @GameRegistry.ObjectHolder("armreservoir")
    public static BlockArmReservoir ARM_RESERVOIR;

    public static void init()
    {
        BLOCKS = new LinkedHashMap<>();

        MinecraftForge.EVENT_BUS.register(new WGBlocks());

        registerBlock(new BlockProvider(true));
        registerBlock(new BlockProvider(false));
        registerBlock(new BlockRequester());
        registerBlock(new BlockStorage());
        registerBlock(new BlockCable());
        registerBlock(new BlockArmReservoir());

        registerTile(TileArmReservoir.class, "armreservoir");
        registerTile(TileCable.class, "cable");
        registerTile(TileProvider.class, "provider");
        registerTile(TileRequester.class, "requester");
        registerTile(TileStorage.class, "storage");
    }

    @SubscribeEvent
    public void onBlockRegister(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(BLOCKS.keySet().toArray(new Block[0]));
    }

    private static <T extends Block> void registerBlock(T block)
    {
        registerBlock(block, ItemBlock::new);
    }

    private static <T extends Block> void registerBlock(T block, Function<T, ItemBlock> supplier)
    {
        ItemBlock supplied = supplier.apply(block);
        supplied.setRegistryName(block.getRegistryName());

        BLOCKS.put(block, supplied);
    }

    private static void registerTile(Class<? extends TileEntity> c, String name)
    {
        GameRegistry.registerTileEntity(c, new ResourceLocation(WoodenGears.MODID, name));
    }
}
