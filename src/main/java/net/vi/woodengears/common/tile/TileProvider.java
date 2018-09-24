package net.vi.woodengears.common.tile;

import fr.ourten.teabeans.value.BaseProperty;
import lombok.Getter;
import net.minecraft.block.BlockDirectional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.vi.woodengears.common.grid.CableGrid;
import net.vi.woodengears.common.grid.IConnectionAware;
import net.vi.woodengears.common.grid.IRailConnectable;
import net.voxelindustry.steamlayer.container.BuiltContainer;
import net.voxelindustry.steamlayer.container.ContainerBuilder;
import net.voxelindustry.steamlayer.container.IContainerProvider;
import net.voxelindustry.steamlayer.tile.ILoadable;
import net.voxelindustry.steamlayer.tile.event.TileTickHandler;

public class TileProvider extends TileLogicisticNode implements ITickable
{
    private int transferCooldown = -1;

    @Getter
    private BaseProperty<IItemHandler> cachedInventoryProperty;

    public TileProvider()
    {
        super("provider", 0);

        this.cachedInventoryProperty = new BaseProperty<>(null, "cachedInventoryProperty");
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;

        --this.transferCooldown;

        if (!this.isOnTransferCooldown())
        {
            this.setTransferCooldown(0);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setInteger("transferCooldown", this.transferCooldown);

        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.transferCooldown = tag.getInteger("transferCooldown");
    }

    private boolean isOnTransferCooldown()
    {
        return this.transferCooldown > 0;
    }

    public void setTransferCooldown(int ticks)
    {
        this.transferCooldown = ticks;
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("provider", player)
                .player(player).inventory(8, 103).hotbar(8, 161)
                .addInventory()
                .syncBooleanValue(getConnectedInventoryProperty()::getValue, getConnectedInventoryProperty()::setValue)
                .syncInventory(this::getConnectedInventory, cachedInventoryProperty::setValue, 10)
                .create();
    }
}
