package net.opmcorp.woodengears.common.network;

import com.google.common.base.Predicates;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.opmcorp.woodengears.common.tile.WGTileBase;

public class NetworkHandler
{
    public static void sendTileToPlayer(WGTileBase tile, EntityPlayerMP player)
    {
        if (tile.isServer())
        {
            final SPacketUpdateTileEntity packet = tile.getUpdatePacket();

            if (packet == null)
                return;
            player.connection.sendPacket(packet);
        }
    }

    public static void sendTileToRange(WGTileBase tile)
    {
        if (tile.isServer())
        {
            final SPacketUpdateTileEntity packet = tile.getUpdatePacket();

            if (packet == null)
                return;

            final Chunk chunk = tile.getWorld().getChunkFromBlockCoords(tile.getPos());
            if (((WorldServer) tile.getWorld()).getPlayerChunkMap().contains(chunk.x, chunk.z))
            {
                for (final EntityPlayerMP player : tile.getWorld().getPlayers(EntityPlayerMP.class,
                        Predicates.alwaysTrue()))
                {
                    if (((WorldServer) tile.getWorld()).getPlayerChunkMap().isPlayerWatchingChunk(player, chunk.x,
                            chunk.z))
                        player.connection.sendPacket(packet);
                }
            }
        }
    }
}
