package net.vi.woodengears.common.tile;

import fr.ourten.teabeans.value.BaseProperty;
import lombok.Getter;
import net.minecraft.block.BlockDirectional;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.common.grid.CableGrid;
import net.vi.woodengears.common.grid.IConnectionAware;
import net.vi.woodengears.common.grid.IRailConnectable;
import net.voxelindustry.steamlayer.container.IContainerProvider;
import net.voxelindustry.steamlayer.tile.ILoadable;
import net.voxelindustry.steamlayer.tile.ITileInfoList;
import net.voxelindustry.steamlayer.tile.TileBase;
import net.voxelindustry.steamlayer.tile.event.TileTickHandler;

public abstract class TileLogicisticNode extends TileBase implements IContainerProvider, ILoadable,
        IConnectionAware, IRailConnectable
{
    @Getter
    private BaseProperty<Boolean> connectedInventoryProperty;

    @Getter
    private TileCable cable;

    @Getter
    private String name;

    public TileLogicisticNode(String name)
    {
        this.name = name;

        this.connectedInventoryProperty = new BaseProperty<>(false, "connectedInventoryProperty");
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        super.addInfo(list);

        if (this.cable != null)
            list.addText("Grid: " + cable.getGrid());

        if (this.connectedInventoryProperty.getValue())
            list.addText("Inventory Connected");
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

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(pos).getValue(BlockDirectional.FACING);
    }

    public BlockPos getRailPos()
    {
        return pos.up(2);
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new TextComponentTranslation(WoodenGears.MODID + ".gui." + this.getName() + ".name");
    }
}
