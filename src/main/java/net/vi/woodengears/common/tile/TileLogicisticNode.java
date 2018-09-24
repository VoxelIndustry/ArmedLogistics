package net.vi.woodengears.common.tile;

import fr.ourten.teabeans.value.BaseProperty;
import lombok.Getter;
import net.minecraft.block.BlockDirectional;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.vi.woodengears.common.grid.CableGrid;
import net.vi.woodengears.common.grid.IConnectionAware;
import net.vi.woodengears.common.grid.IRailConnectable;
import net.voxelindustry.steamlayer.container.IContainerProvider;
import net.voxelindustry.steamlayer.tile.ILoadable;
import net.voxelindustry.steamlayer.tile.event.TileTickHandler;

public abstract class TileLogicisticNode extends TileInventoryBase implements IContainerProvider, ILoadable,
        IConnectionAware, IRailConnectable
{
    @Getter
    private BaseProperty<Boolean> connectedInventoryProperty;

    private TileCable cable;

    public TileLogicisticNode(String name, int invSize)
    {
        super(name, invSize);
        this.connectedInventoryProperty = new BaseProperty<>(false, "connectedInventoryProperty");
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
    public void onLoad()
    {
        super.onLoad();
        if (!this.world.isRemote && cable == null)
            TileTickHandler.loadables.add(this);
    }

    public IItemHandler getConnectedInventory()
    {
        TileEntity tile = this.world.getTileEntity(pos.offset(getFacing()));

        if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                getFacing().getOpposite()))
            return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getFacing().getOpposite());
        return null;
    }

    @Override
    public boolean canDropSlot(int slot)
    {
        return slot != 0 && slot != 1 &&super.canDropSlot(slot);
    }

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(pos).getValue(BlockDirectional.FACING);
    }
}
