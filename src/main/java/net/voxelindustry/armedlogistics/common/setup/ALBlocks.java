package net.voxelindustry.armedlogistics.common.setup;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;
import net.voxelindustry.armedlogistics.common.block.BlockArmReservoir;
import net.voxelindustry.armedlogistics.common.block.BlockProvider;
import net.voxelindustry.armedlogistics.common.block.BlockRail;
import net.voxelindustry.armedlogistics.common.block.BlockRequester;
import net.voxelindustry.armedlogistics.common.block.BlockStorage;

import static net.voxelindustry.armedlogistics.ArmedLogistics.MODID;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ALBlocks
{
    @ObjectHolder(MODID + ":rail")
    public static BlockRail CABLE;

    @ObjectHolder(MODID + ":provider_active")
    public static BlockProvider PROVIDER;

    @ObjectHolder(MODID + ":provider_passive")
    public static BlockProvider ACTIVE_PROVIDER;

    @ObjectHolder(MODID + ":requester")
    public static BlockRequester REQUESTER;

    @ObjectHolder(MODID + ":storage")
    public static BlockStorage STORAGE;

    @ObjectHolder(MODID + ":armreservoir")
    public static BlockArmReservoir ARM_RESERVOIR;

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event)
    {
        registerBlock(event, new BlockProvider(Block.Properties.create(Material.IRON, MaterialColor.IRON), true), "provider_active");
        registerBlock(event, new BlockProvider(Block.Properties.create(Material.IRON, MaterialColor.IRON), false), "provider_passive");

        registerBlock(event, new BlockRequester(Block.Properties.create(Material.IRON, MaterialColor.IRON)), "requester");
        registerBlock(event, new BlockStorage(Block.Properties.create(Material.IRON, MaterialColor.IRON)), "storage");
        registerBlock(event, new BlockRail(Block.Properties.create(Material.IRON, MaterialColor.IRON)), "rail");
        registerBlock(event, new BlockArmReservoir(Block.Properties.create(Material.IRON, MaterialColor.IRON)), "armreservoir");
    }

    private static <T extends Block> void registerBlock(Register<Block> event, T block, String name)
    {
        block.setRegistryName(MODID, name);
        event.getRegistry().register(block);
        ALItems.registerItemBlock(block);
    }
}
