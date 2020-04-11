package net.voxelindustry.armedlogistics.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.common.block.BlockArmReservoir;
import net.voxelindustry.armedlogistics.common.block.BlockCable;
import net.voxelindustry.armedlogistics.common.block.BlockProvider;
import net.voxelindustry.armedlogistics.common.block.BlockRequester;
import net.voxelindustry.armedlogistics.common.block.BlockStorage;
import net.voxelindustry.armedlogistics.common.tile.TileActiveProvider;
import net.voxelindustry.armedlogistics.common.tile.TileArmReservoir;
import net.voxelindustry.armedlogistics.common.tile.TileCable;
import net.voxelindustry.armedlogistics.common.tile.TileProvider;
import net.voxelindustry.armedlogistics.common.tile.TileRequester;
import net.voxelindustry.armedlogistics.common.tile.TileStorage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@ObjectHolder(ArmedLogistics.MODID)
public class WGBlocks
{
    public static Map<Block, ItemBlock> BLOCKS;

    @ObjectHolder("blockcable")
    public static BlockCable CABLE;

    @ObjectHolder("provider_active")
    public static BlockProvider PROVIDER;

    @ObjectHolder("provider_passive")
    public static BlockProvider ACTIVE_PROVIDER;

    @ObjectHolder("requester")
    public static BlockRequester REQUESTER;

    @ObjectHolder("storage")
    public static BlockStorage STORAGE;

    @ObjectHolder("armreservoir")
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
        registerTile(TileActiveProvider.class, "activeprovider");
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
        GameRegistry.registerTileEntity(c, new ResourceLocation(ArmedLogistics.MODID, name));
    }
}
