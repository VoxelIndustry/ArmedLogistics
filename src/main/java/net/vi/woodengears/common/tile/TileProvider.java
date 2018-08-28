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

public class TileProvider extends TileInventoryBase implements ITickable, IContainerProvider, ILoadable,
        IConnectionAware, IRailConnectable
{
    @Getter
    private BaseProperty<Boolean> connectedInventoryProperty;
    private int                   transferCooldown = -1;

    @Getter
    private BaseProperty<IItemHandler> cachedInventoryProperty;

    private TileCable cable;

    public TileProvider()
    {
        super("provider", 0);

        this.connectedInventoryProperty = new BaseProperty<>(false, "connectedInventoryProperty");
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
    public void connectTrigger(EnumFacing facing, CableGrid grid)
    {
        this.cable = (TileCable) this.world.getTileEntity(pos.offset(EnumFacing.UP, 2));
    }

    @Override
    public void disconnectTrigger(EnumFacing facing, CableGrid grid)
    {
        this.cable = null;
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (!this.world.isRemote && cable == null)
            TileTickHandler.loadables.add(this);
    }

    @Override
    public void load()
    {
        BlockPos railPos = this.getPos().offset(EnumFacing.UP, 2);

        TileEntity rail = this.world.getTileEntity(railPos);
        if (rail instanceof TileCable)
            ((TileCable) rail).connectHandler(EnumFacing.DOWN, this, this);

        checkInventory();
    }

    public void disconnectGrid()
    {
        if (this.cable != null)
            cable.disconnectHandler(EnumFacing.DOWN, this);
    }

    public void checkInventory()
    {
        TileEntity tile = this.world.getTileEntity(this.pos.offset(this.getFacing()));

        if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                this.getFacing().getOpposite()))
            this.connectedInventoryProperty.setValue(true);
        else
            this.connectedInventoryProperty.setValue(false);
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("provider", player)
                .player(player).inventory(8, 103).hotbar(8, 161)
                .addInventory()
                .syncBooleanValue(connectedInventoryProperty::getValue, connectedInventoryProperty::setValue)
                .syncInventory(this::getConnectedInventory, this.cachedInventoryProperty::setValue)
                .create();
    }

    public IItemHandler getConnectedInventory()
    {
        TileEntity tile = this.world.getTileEntity(pos.offset(getFacing()));

        if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                getFacing().getOpposite()))
            return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getFacing().getOpposite());
        return null;
    }

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(pos).getValue(BlockDirectional.FACING);
    }
}
