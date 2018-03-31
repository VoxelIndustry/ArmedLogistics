package net.opmcorp.woodengears.common.init;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.opmcorp.woodengears.WoodenGears;
import net.opmcorp.woodengears.common.item.ItemBase;
import net.opmcorp.woodengears.common.item.ItemLogisticArm;

import java.util.ArrayList;
import java.util.List;

@GameRegistry.ObjectHolder(WoodenGears.MODID)
public class WGItems
{
    public static List<Item> ITEMS;

    @GameRegistry.ObjectHolder("item_logistic_arm")
    public static Item logistic_arm;

    public static void init()
    {
        ITEMS = new ArrayList<>();
        ITEMS.addAll(WGBlocks.BLOCKS.values());

        MinecraftForge.EVENT_BUS.register(new WGItems());

        registerItem(new ItemLogisticArm());
    }

    @SubscribeEvent
    public void onItemRegister(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(ITEMS.toArray(new Item[ITEMS.size()]));
    }

    static void registerItem(ItemBase item)
    {
        ITEMS.add(item);
    }
}
