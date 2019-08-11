package net.vi.woodengears.common.serializer;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.vi.woodengears.common.grid.logistic.ColoredShipment;
import net.vi.woodengears.common.grid.logistic.ColoredStack;
import net.vi.woodengears.common.grid.logistic.LogisticShipment;

public class LogisticShipmentSerializer
{
    public static void itemShipmentToByteBuf(LogisticShipment<ItemStack> shipment, ByteBuf buf)
    {
        buf.writeLong(shipment.getFrom().toLong());
        buf.writeLong(shipment.getTo().toLong());

        ByteBufUtils.writeItemStack(buf, shipment.getContent());
    }

    public static LogisticShipment<ItemStack> itemShipmentFromByteBuf(ByteBuf buf)
    {
        return new LogisticShipment<>(
                BlockPos.fromLong(buf.readLong()),
                BlockPos.fromLong(buf.readLong()),
                ByteBufUtils.readItemStack(buf));
    }

    public static NBTTagCompound itemShipmentToNBT(LogisticShipment<ItemStack> shipment)
    {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setLong("from", shipment.getFrom().toLong());
        tag.setLong("to", shipment.getTo().toLong());

        tag.setTag("content", shipment.getContent().writeToNBT(new NBTTagCompound()));

        return tag;
    }

    public static LogisticShipment<ItemStack> itemShipmentFromNBT(NBTTagCompound tag)
    {
        return new LogisticShipment<>(
                BlockPos.fromLong(tag.getLong("from")),
                BlockPos.fromLong(tag.getLong("to")),
                new ItemStack(tag.getCompoundTag("content")));
    }

    public static NBTTagCompound coloredItemShipmentToNBT(ColoredShipment<ItemStack> shipment)
    {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setLong("from", shipment.getFrom().toLong());
        tag.setLong("to", shipment.getTo().toLong());

        tag.setTag("content", shipment.getRawContent().writeToNBT(new NBTTagCompound()));
        tag.setTag("color", shipment.getContent().toNBT(new NBTTagCompound()));

        return tag;
    }

    public static ColoredShipment<ItemStack> coloredItemShipmentFromNBT(NBTTagCompound tag)
    {
        return new ColoredShipment<>(
                BlockPos.fromLong(tag.getLong("from")),
                BlockPos.fromLong(tag.getLong("to")),
                new ColoredStack(tag.getCompoundTag("color")),
                new ItemStack(tag.getCompoundTag("content")));
    }
}
