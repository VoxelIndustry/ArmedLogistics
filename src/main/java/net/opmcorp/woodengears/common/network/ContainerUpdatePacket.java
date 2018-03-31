package net.opmcorp.woodengears.common.network;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.opmcorp.woodengears.common.container.BuiltContainer;

@AllArgsConstructor
@NoArgsConstructor
public class ContainerUpdatePacket implements IMessage
{
    private int            windowID;
    private int            syncableID;
    private NBTTagCompound data;

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(windowID);
        buf.writeInt(syncableID);
        ByteBufUtils.writeTag(buf, data);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        windowID = buf.readInt();
        syncableID = buf.readInt();
        data = ByteBufUtils.readTag(buf);
    }

    public static class ContainerUpdatePacketHandler implements IMessageHandler<ContainerUpdatePacket, IMessage>
    {
        public ContainerUpdatePacketHandler()
        {

        }

        @Override
        public IMessage onMessage(ContainerUpdatePacket message, MessageContext ctx)
        {
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;

            serverPlayer.getServerWorld().addScheduledTask(() ->
            {
                if (serverPlayer.openContainer != null && serverPlayer.openContainer.windowId == message.windowID
                        && serverPlayer.openContainer instanceof BuiltContainer)
                    ((BuiltContainer) serverPlayer.openContainer).updateProperty(message.syncableID, message.data);
            });
            return null;
        }
    }
}
