package net.voxelindustry.armedlogistics.common.tile;

import fr.ourten.teabeans.value.BaseProperty;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.IItemHandler;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.common.block.BlockProvider;
import net.voxelindustry.armedlogistics.common.grid.IRailConnectable;
import net.voxelindustry.steamlayer.grid.CableGrid;
import net.voxelindustry.steamlayer.grid.IConnectionAware;
import net.voxelindustry.steamlayer.tile.ILoadable;
import net.voxelindustry.steamlayer.tile.ITileInfoList;
import net.voxelindustry.steamlayer.tile.TileBase;
import net.voxelindustry.steamlayer.tile.event.TileTickHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

@Getter
public abstract class TileLogicisticNode extends TileBase implements INamedContainerProvider, ILoadable,
        IConnectionAware, IRailConnectable, INameable
{
    private BaseProperty<Boolean>      connectedInventoryProperty;
    private BaseProperty<IItemHandler> cachedInventoryProperty;

    private List<Direction> adjacentFacings;

    private EnumMap<Direction, IItemHandler> adjacentInventories;

    private List<IItemHandler> inventories;
    private WrappedInventories wrappedInventories;

    private boolean hasLoaded;

    private TileCable cable;

    private String         name;
    private ITextComponent customName;
    private boolean        hasCustomName;

    public TileLogicisticNode(TileEntityType<? extends TileLogicisticNode> type,
                              String name)
    {
        super(type);
        this.name = name;

        connectedInventoryProperty = new BaseProperty<>(false, "connectedInventoryProperty");
        cachedInventoryProperty = new BaseProperty<>(null, "cachedInventoryProperty");

        adjacentFacings = new ArrayList<>();

        adjacentInventories = new EnumMap<>(Direction.class);
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
    public void connectTrigger(Direction facing, CableGrid grid)
    {
        cable = (TileCable) world.getTileEntity(pos.offset(Direction.UP, 2));
    }

    @Override
    public void disconnectTrigger(Direction facing, CableGrid grid)
    {
        cable = null;
    }

    @Override
    public void load()
    {
        BlockPos railPos = getPos().offset(Direction.UP, 2);

        TileEntity rail = world.getTileEntity(railPos);
        if (rail instanceof TileCable && ((TileCable) rail).hasGrid())
            ((TileCable) rail).connectHandler(Direction.DOWN, this, this);

        hasLoaded = true;
        addFacing(getFacing().getOpposite());
        onAdjacentRefresh();
    }

    public void disconnectGrid()
    {
        if (cable != null)
            cable.disconnectHandler(Direction.DOWN, this);
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
        for (Direction facing : Direction.values())
        {
            tile.getCapability(ITEM_HANDLER_CAPABILITY, facing).ifPresent(itemHandler -> adjacentInventories.put(facing, itemHandler));
        }

        if (!adjacentInventories.isEmpty())
            connectedInventoryProperty.setValue(true);

        inventories.clear();
        for (Direction facing : adjacentFacings)
        {
            if (adjacentInventories.containsKey(facing))
                inventories.add(adjacentInventories.get(facing));
        }
    }

    protected void addFacing(Direction facing)
    {
        if (adjacentFacings.contains(facing))
            return;
        adjacentFacings.add(facing);
        onAdjacentRefresh();
    }

    protected void removeFacing(Direction facing)
    {
        adjacentFacings.remove(facing);
        onAdjacentRefresh();
    }

    protected void setFacing(Direction facing, int index)
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
    public void read(CompoundNBT tag)
    {
        super.read(tag);

        hasCustomName = tag.getBoolean("hasCustomName");

        if (hasCustomName())
            customName = new StringTextComponent(tag.getString("customName"));

        int adjacentFacings = tag.getInt("adjacentFacings");
        for (int i = 0; i < adjacentFacings; i++)
            getAdjacentFacings().add(Direction.byIndex(tag.getInt("adjacentFacing" + i)));

        if (isServer() && hasLoaded)
            onAdjacentRefresh();
    }

    @Override
    public CompoundNBT write(CompoundNBT tag)
    {
        if (customName != null)
            tag.putString("customName", customName.getUnformattedComponentText());
        tag.putBoolean("hasCustomName", hasCustomName);

        for (int i = 0; i < getAdjacentFacings().size(); i++)
            tag.putInt("adjacentFacing" + i, getAdjacentFacings().get(i).getIndex());
        tag.putInt("adjacentFacings", getAdjacentFacings().size());

        return super.write(tag);
    }

    public Direction getFacing()
    {
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() == Blocks.AIR)
            return Direction.DOWN;
        return state.get(BlockProvider.FACING);
    }

    public BlockPos getRailPos()
    {
        return pos.up(2);
    }

    @Override
    public ITextComponent getName()
    {
        if (hasCustomName())
            return getCustomName();
        return new TranslationTextComponent(ArmedLogistics.MODID + ".gui." + name + ".name");
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return getName();
    }

    @Override
    @Nullable
    public ITextComponent getCustomName()
    {
        return customName;
    }

    public void setCustomName(String name)
    {
        hasCustomName = true;
        customName = new StringTextComponent(name);
    }

    @Override
    public boolean hasCustomName()
    {
        return hasCustomName;
    }
}
