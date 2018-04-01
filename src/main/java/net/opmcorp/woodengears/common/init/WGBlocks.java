package net.opmcorp.woodengears.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.opmcorp.woodengears.WoodenGears;
import net.opmcorp.woodengears.common.block.BlockArmReservoir;
import net.opmcorp.woodengears.common.block.BlockCable;
import net.opmcorp.woodengears.common.block.BlockProvider;
import net.opmcorp.woodengears.common.tile.TileArmReservoir;
import net.opmcorp.woodengears.common.tile.TileCable;
import net.opmcorp.woodengears.common.tile.TileProvider;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@GameRegistry.ObjectHolder(WoodenGears.MODID)
public class WGBlocks
{
    public static Map<Block, ItemBlock> BLOCKS;

    @GameRegistry.ObjectHolder("blockcable")
    public static BlockCable CABLE;

    @GameRegistry.ObjectHolder("blockarmreservoir")
    public static BlockArmReservoir ARM_RESERVOIR;

    @GameRegistry.ObjectHolder("logic")
    public static Block LOGIC;

    public static void init()
    {
        BLOCKS = new LinkedHashMap<>();

        MinecraftForge.EVENT_BUS.register(new WGBlocks());

        registerBlock(new BlockProvider());
        registerBlock(new BlockCable());
        registerBlock(new BlockArmReservoir());

        registerTile(TileArmReservoir.class, "armreservoir");
        registerTile(TileCable.class, "cable");
        registerTile(TileProvider.class, "provider");
    }

    @SubscribeEvent
    public void onBlockRegister(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(BLOCKS.keySet().toArray(new Block[BLOCKS.size()]));
    }

    private static <T extends Block> void registerBlock(T block)
    {
        registerBlock(block, ItemBlock::new);
    }

    private static <T extends Block> void registerBlock(T block, Function<T, ItemBlock> supplier)
    {
        final ItemBlock supplied = supplier.apply(block);
        supplied.setRegistryName(block.getRegistryName());

        BLOCKS.put(block, supplied);
    }

    private static void registerTile(Class<? extends TileEntity> c, String name)
    {
        GameRegistry.registerTileEntity(c, WoodenGears.MODID + ":" + name);
    }
}
