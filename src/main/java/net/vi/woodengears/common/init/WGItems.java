package net.vi.woodengears.common.init;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.common.item.ItemBase;

import java.util.ArrayList;
import java.util.List;

@GameRegistry.ObjectHolder(WoodenGears.MODID)
public class WGItems
{
    public static List<Item> ITEMS;

    @GameRegistry.ObjectHolder("logisticarm")
    public static Item LOGISTIC_ARM;

    public static void init()
    {
        ITEMS = new ArrayList<>();
        ITEMS.addAll(WGBlocks.BLOCKS.values());

        MinecraftForge.EVENT_BUS.register(new WGItems());

        registerItem(new ItemBase("logisticarm").setMaxStackSize(4));
    }

    @SubscribeEvent
    public void onItemRegister(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(ITEMS.toArray(new Item[0]));
    }

    static void registerItem(Item item)
    {
        ITEMS.add(item);
    }
}
