package net.voxelindustry.armedlogistics.common.serializer;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.voxelindustry.armedlogistics.common.grid.logistic.ColoredShipment;
import net.voxelindustry.armedlogistics.common.grid.logistic.ColoredStack;
import net.voxelindustry.armedlogistics.common.grid.logistic.LogisticShipment;
import net.voxelindustry.steamlayer.network.ByteBufHelper;

public class LogisticShipmentSerializer
{
    public static void itemShipmentToByteBuf(LogisticShipment<ItemStack> shipment, ByteBuf buf)
    {
        buf.writeLong(shipment.getFrom().toLong());
        buf.writeLong(shipment.getTo().toLong());

        ByteBufHelper.writeItemStack(buf, shipment.getContent());
    }

    public static LogisticShipment<ItemStack> itemShipmentFromByteBuf(ByteBuf buf)
    {
        return new LogisticShipment<>(
                BlockPos.fromLong(buf.readLong()),
                BlockPos.fromLong(buf.readLong()),
                ByteBufHelper.readItemStack(buf));
    }

    public static CompoundNBT itemShipmentToNBT(LogisticShipment<ItemStack> shipment)
    {
        CompoundNBT tag = new CompoundNBT();

        tag.putLong("from", shipment.getFrom().toLong());
        tag.putLong("to", shipment.getTo().toLong());

        tag.put("content", shipment.getContent().serializeNBT());

        return tag;
    }

    public static LogisticShipment<ItemStack> itemShipmentFromNBT(CompoundNBT tag)
    {
        return new LogisticShipment<>(
                BlockPos.fromLong(tag.getLong("from")),
                BlockPos.fromLong(tag.getLong("to")),
                ItemStack.read(tag.getCompound("content")));
    }

    public static CompoundNBT coloredItemShipmentToNBT(ColoredShipment<ItemStack> shipment)
    {
        CompoundNBT tag = new CompoundNBT();

        tag.putLong("from", shipment.getFrom().toLong());
        tag.putLong("to", shipment.getTo().toLong());

        tag.put("content", shipment.getRawContent().serializeNBT());
        tag.put("color", shipment.getContent().toNBT(new CompoundNBT()));

        return tag;
    }

    public static ColoredShipment<ItemStack> coloredItemShipmentFromNBT(CompoundNBT tag)
    {
        return new ColoredShipment<>(
                BlockPos.fromLong(tag.getLong("from")),
                BlockPos.fromLong(tag.getLong("to")),
                new ColoredStack(tag.getCompound("color")),
                ItemStack.read(tag.getCompound("content")));
    }
}
