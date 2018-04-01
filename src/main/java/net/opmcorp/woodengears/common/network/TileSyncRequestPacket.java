package net.opmcorp.woodengears.common.network;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.opmcorp.woodengears.common.tile.WGTileBase;

@AllArgsConstructor
@NoArgsConstructor
public class TileSyncRequestPacket implements IMessage
{
    private int      dimensionID;
    private BlockPos pos;

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(pos.toLong());
        buf.writeInt(dimensionID);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.pos = BlockPos.fromLong(buf.readLong());
        this.dimensionID = buf.readInt();
    }

    public static class TileSyncRequestPacketHandler implements IMessageHandler<TileSyncRequestPacket, IMessage>
    {
        public TileSyncRequestPacketHandler()
        {

        }

        @Override
        public IMessage onMessage(TileSyncRequestPacket message, MessageContext ctx)
        {
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;

            serverPlayer.getServerWorld().addScheduledTask(() ->
            {
                if (serverPlayer.getEntityWorld().provider.getDimension() == message.dimensionID
                        && serverPlayer.getEntityWorld().isBlockLoaded(message.pos) && serverPlayer.getEntityWorld()
                        .getTileEntity(message.pos) != null
                        && serverPlayer.getEntityWorld().getTileEntity(message.pos) instanceof WGTileBase)
                    NetworkHandler.sendTileToPlayer(
                            (WGTileBase) serverPlayer.getEntityWorld().getTileEntity(message.pos), serverPlayer);
            });
            return null;
        }
    }
}
