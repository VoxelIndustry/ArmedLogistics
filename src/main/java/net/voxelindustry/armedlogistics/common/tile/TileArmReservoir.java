package net.voxelindustry.armedlogistics.common.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerProvider;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.armedlogistics.common.block.BlockArmReservoir;
import net.voxelindustry.armedlogistics.common.grid.IRailConnectable;
import net.voxelindustry.armedlogistics.common.grid.RailGrid;
import net.voxelindustry.armedlogistics.common.setup.ALContainers;
import net.voxelindustry.armedlogistics.common.setup.ALItems;
import net.voxelindustry.armedlogistics.common.setup.ALTiles;
import net.voxelindustry.steamlayer.container.ContainerBuilder;
import net.voxelindustry.steamlayer.grid.CableGrid;
import net.voxelindustry.steamlayer.grid.IConnectionAware;
import net.voxelindustry.steamlayer.tile.ITileInfoList;

import javax.annotation.Nullable;

public class TileArmReservoir extends TileInventoryBase implements IContainerProvider, IRailConnectable, IConnectionAware, INameable
{
    private RailGrid grid;

    private ITextComponent customName;
    private boolean        hasCustomName;

    public TileArmReservoir()
    {
        super(ALTiles.ARM_RESERVOIR, "armreservoir", 6);
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        super.addInfo(list);

        list.addText("Facing:" + getFacing());
        if (grid != null)
            list.addText("Grid: " + grid.getIdentifier());
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player)
    {
        return new ContainerBuilder(ALContainers.ARM_RESERVOIR, player)
                .player(player).inventory(8, 96).hotbar(8, 154)
                .tile(getInventory())
                .filterSlotLine(0, 54, 52, 4, Direction.Axis.X, stack -> stack.getItem() == ALItems.LOGISTIC_ARM)
                .fuelSlot(4, 72, 74)
                .fuelSlot(5, 90, 74)
                .create(windowId);
    }

    @Override
    public void connectTrigger(Direction facing, CableGrid grid)
    {
        this.grid = (RailGrid) grid;
    }

    @Override
    public void disconnectTrigger(Direction facing, CableGrid grid)
    {
        this.grid = null;
    }

    @Override
    public boolean canConnect(TileCable cable, Direction from)
    {
        return from == getFacing();
    }

    public Direction getFacing()
    {
        return world.getBlockState(pos).get(BlockArmReservoir.FACING);
    }

    @Override
    public ITextComponent getName()
    {
        if (hasCustomName())
            return getCustomName();
        return new TranslationTextComponent(ArmedLogistics.MODID + ".gui.arm_reservoir.name");
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
}
