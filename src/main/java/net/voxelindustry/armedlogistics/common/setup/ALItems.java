package net.voxelindustry.armedlogistics.common.setup;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;
import net.voxelindustry.armedlogistics.ArmedLogistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.voxelindustry.armedlogistics.ArmedLogistics.MODID;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ALItems
{
    public static final Item.Properties ITEM_PROPS = new Item.Properties().group(ArmedLogistics.TAB_ALL);
    public static       List<Item>      ITEMS = new ArrayList<>();

    @ObjectHolder(MODID+":logisticarm")
    public static Item LOGISTIC_ARM;

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(ITEMS.toArray(new Item[0]));

        registerItem(event, new Item(new Item.Properties().group(ArmedLogistics.TAB_ALL).maxStackSize(4)), "logisticarm");
    }

    static void registerItem(Register<Item> event, Item item, String name)
    {
        item.setRegistryName(MODID, name);
        event.getRegistry().register(item);
    }

    public static void registerItemBlock(Block block)
    {
        ITEMS.add(new BlockItem(block, ITEM_PROPS).setRegistryName(Objects.requireNonNull(block.getRegistryName())));
    }
}
