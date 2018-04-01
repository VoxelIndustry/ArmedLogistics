package net.opmcorp.woodengears.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.opmcorp.woodengears.common.network.NetworkHandler;
import net.opmcorp.woodengears.common.network.TileSyncRequestPacket;
import net.opmcorp.woodengears.common.network.WGPacketHandler;

public class WGTileBase extends TileEntity
{
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        final NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(this.pos, 1, nbtTag);
    }

    @Override
    public void onDataPacket(final NetworkManager net, final SPacketUpdateTileEntity packet)
    {
        this.readFromNBT(packet.getNbtCompound());
    }

    public void sync()
    {
        if (this.world != null)
            NetworkHandler.sendTileToRange(this);
    }

    public void forceSync()
    {
        WGPacketHandler.INSTANCE.sendToServer(new TileSyncRequestPacket(this.world.provider.getDimension(),
                this.getPos()));
    }

    public boolean isServer()
    {
        if (this.world != null)
            return !this.world.isRemote;
        return FMLCommonHandler.instance().getEffectiveSide().isServer();
    }

    public boolean isClient()
    {
        if (this.world != null)
            return this.world.isRemote;
        return FMLCommonHandler.instance().getEffectiveSide().isClient();
    }
}
