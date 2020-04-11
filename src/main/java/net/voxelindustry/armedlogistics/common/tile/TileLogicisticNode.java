package net.voxelindustry.armedlogistics.common.tile;

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
import net.minecraftforge.items.IItemHandler;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.common.block.BlockProvider;
import net.voxelindustry.armedlogistics.common.grid.IRailConnectable;
import net.voxelindustry.steamlayer.container.IContainerProvider;
import net.voxelindustry.steamlayer.grid.CableGrid;
import net.voxelindustry.steamlayer.grid.IConnectionAware;
import net.voxelindustry.steamlayer.tile.ILoadable;
import net.voxelindustry.steamlayer.tile.ITileInfoList;
import net.voxelindustry.steamlayer.tile.TileBase;
import net.voxelindustry.steamlayer.tile.event.TileTickHandler;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

@Getter
public abstract class TileLogicisticNode extends TileBase implements IContainerProvider, ILoadable,
        IConnectionAware, IRailConnectable, IWorldNameable
{
    private BaseProperty<Boolean>      connectedInventoryProperty;
    private BaseProperty<IItemHandler> cachedInventoryProperty;

    private List<EnumFacing> adjacentFacings;

    private EnumMap<EnumFacing, IItemHandler> adjacentInventories;

    private List<IItemHandler> inventories;
    private WrappedInventories wrappedInventories;

    private boolean hasLoaded;

    private TileCable cable;

    private String  name;
    private String  customName;
    private boolean hasCustomName;

    public TileLogicisticNode(String name)
    {
        this.name = name;

        connectedInventoryProperty = new BaseProperty<>(false, "connectedInventoryProperty");
        cachedInventoryProperty = new BaseProperty<>(null, "cachedInventoryProperty");

        adjacentFacings = new ArrayList<>();

        adjacentInventories = new EnumMap<>(EnumFacing.class);
        inventories = new ArrayList<>(6);

        wrappedInventories = new WrappedInventories();
        wrappedInventories.setWrappeds(getInventories());
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

        hasLoaded = true;
        addFacing(getFacing().getOpposite());
    }

    public void disconnectGrid()
    {
        if (cable != null)
            cable.disconnectHandler(EnumFacing.DOWN, this);
    }

    public void onAdjacentRefresh()
    {
        TileEntity tile = world.getTileEntity(pos.offset(getFacing()));

        if (tile == null)
        {
            connectedInventoryProperty.setValue(false);
            return;
        }

        adjacentInventories.clear();
        for (EnumFacing facing : EnumFacing.values())
        {
            if (tile.hasCapability(ITEM_HANDLER_CAPABILITY, facing))
                adjacentInventories.put(facing, tile.getCapability(ITEM_HANDLER_CAPABILITY, facing));
        }

        if (!adjacentInventories.isEmpty())
            connectedInventoryProperty.setValue(true);

        inventories.clear();
        for (EnumFacing facing : adjacentFacings)
        {
            if (adjacentInventories.containsKey(facing))
                inventories.add(adjacentInventories.get(facing));
        }
    }

    protected void addFacing(EnumFacing facing)
    {
        if (adjacentFacings.contains(facing))
            return;
        adjacentFacings.add(facing);
        onAdjacentRefresh();
    }

    protected void removeFacing(EnumFacing facing)
    {
        adjacentFacings.remove(facing);
        onAdjacentRefresh();
    }

    protected void setFacing(EnumFacing facing, int index)
    {
        if (adjacentFacings.contains(facing))
            return;
        adjacentFacings.set(index, facing);
        onAdjacentRefresh();
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (!world.isRemote && cable == null)
            TileTickHandler.loadables.add(this);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        hasCustomName = tag.getBoolean("hasCustomName");

        if (hasCustomName())
            customName = tag.getString("customName");

        int adjacentFacings = tag.getInteger("adjacentFacings");
        for (int i = 0; i < adjacentFacings; i++)
            getAdjacentFacings().add(EnumFacing.byIndex(tag.getInteger("adjacentFacing" + i)));

        if (isServer() && hasLoaded)
            onAdjacentRefresh();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        if (customName != null)
            tag.setString("customName", customName);
        tag.setBoolean("hasCustomName", hasCustomName);

        for (int i = 0; i < getAdjacentFacings().size(); i++)
            tag.setInteger("adjacentFacing" + i, getAdjacentFacings().get(i).getIndex());
        tag.setInteger("adjacentFacings", getAdjacentFacings().size());

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
        return ArmedLogistics.MODID + ".gui." + name + ".name";
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
