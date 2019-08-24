package net.vi.woodengears.common.tile;

import fr.ourten.teabeans.value.BaseProperty;
import lombok.Getter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorldNameable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.common.block.BlockProvider;
import net.vi.woodengears.common.grid.IRailConnectable;
import net.voxelindustry.steamlayer.container.IContainerProvider;
import net.voxelindustry.steamlayer.grid.CableGrid;
import net.voxelindustry.steamlayer.grid.IConnectionAware;
import net.voxelindustry.steamlayer.tile.ILoadable;
import net.voxelindustry.steamlayer.tile.ITileInfoList;
import net.voxelindustry.steamlayer.tile.TileBase;
import net.voxelindustry.steamlayer.tile.event.TileTickHandler;

public abstract class TileLogicisticNode extends TileBase implements IContainerProvider, ILoadable,
        IConnectionAware, IRailConnectable, IWorldNameable
{
    @Getter
    private BaseProperty<Boolean>      connectedInventoryProperty;
    @Getter
    private BaseProperty<IItemHandler> cachedInventoryProperty;

    @Getter
    private TileCable cable;

    @Getter
    private String  name;
    @Getter
    private String  customName;
    private boolean hasCustomName;

    public TileLogicisticNode(String name)
    {
        this.name = name;

        connectedInventoryProperty = new BaseProperty<>(false, "connectedInventoryProperty");
        cachedInventoryProperty = new BaseProperty<>(null, "cachedInventoryProperty");
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        super.addInfo(list);

        if (cable != null)
            list.addText("Grid: " + cable.getGrid());

        if (connectedInventoryProperty.getValue())
            list.addText("Inventory Connected");
    }

    @Override
    public void connectTrigger(EnumFacing facing, CableGrid grid)
    {
        cable = (TileCable) world.getTileEntity(pos.offset(EnumFacing.UP, 2));
    }

    @Override
    public void disconnectTrigger(EnumFacing facing, CableGrid grid)
    {
        cable = null;
    }

    @Override
    public void load()
    {
        BlockPos railPos = getPos().offset(EnumFacing.UP, 2);

        TileEntity rail = world.getTileEntity(railPos);
        if (rail instanceof TileCable && ((TileCable) rail).hasGrid())
            ((TileCable) rail).connectHandler(EnumFacing.DOWN, this, this);

        checkInventory();
    }

    public void disconnectGrid()
    {
        if (cable != null)
            cable.disconnectHandler(EnumFacing.DOWN, this);
    }

    public void checkInventory()
    {
        TileEntity tile = world.getTileEntity(pos.offset(getFacing()));

        if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                getFacing().getOpposite()))
            connectedInventoryProperty.setValue(true);
        else
            connectedInventoryProperty.setValue(false);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (!world.isRemote && cable == null)
            TileTickHandler.loadables.add(this);
    }

    public IItemHandler getConnectedInventory()
    {
        TileEntity tile = world.getTileEntity(pos.offset(getFacing()));

        if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                getFacing().getOpposite()))
            return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getFacing().getOpposite());
        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        hasCustomName = tag.getBoolean("hasCustomName");

        if (hasCustomName())
            customName = tag.getString("customName");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        if (customName != null)
            tag.setString("customName", customName);
        tag.setBoolean("hasCustomName", hasCustomName);

        return super.writeToNBT(tag);
    }

    public EnumFacing getFacing()
    {
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() == Blocks.AIR)
            return EnumFacing.DOWN;
        return state.getValue(BlockProvider.FACING);
    }

    public BlockPos getRailPos()
    {
        return pos.up(2);
    }

    @Override
    public String getName()
    {
        if (hasCustomName())
            return getCustomName();
        return WoodenGears.MODID + ".gui." + name + ".name";
    }

    public void setCustomName(String name)
    {
        hasCustomName = true;
        customName = name;
    }

    @Override
    public boolean hasCustomName()
    {
        return hasCustomName;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return (hasCustomName() ? new TextComponentString(getName()) :
                new TextComponentTranslation(getName()));
    }
}
