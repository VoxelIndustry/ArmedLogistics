package net.vi.woodengears.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.vi.woodengears.common.block.BlockArmReservoir;
import net.vi.woodengears.common.block.BlockCable;
import net.vi.woodengears.common.entity.EntityLogisticArm;
import net.vi.woodengears.common.grid.IRailConnectable;
import net.vi.woodengears.common.grid.RailGrid;
import net.vi.woodengears.common.init.WGItems;
import net.voxelindustry.steamlayer.container.BuiltContainer;
import net.voxelindustry.steamlayer.container.ContainerBuilder;
import net.voxelindustry.steamlayer.container.IContainerProvider;
import net.voxelindustry.steamlayer.grid.CableGrid;
import net.voxelindustry.steamlayer.grid.IConnectionAware;
import net.voxelindustry.steamlayer.tile.ITileInfoList;

import java.util.Optional;

public class TileArmReservoir extends TileInventoryBase implements IContainerProvider, IRailConnectable,
        IConnectionAware
{
    private RailGrid grid;

    public TileArmReservoir()
    {
        super("armreservoir", 6);
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        super.addInfo(list);

        if (this.grid != null)
            list.addText("Grid: " + grid.getIdentifier());
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("armreservoir", player)
                .player(player).inventory(8, 96).hotbar(8, 154)
                .addInventory().tile(this.getInventory())
                .filterSlotLine(0, 54, 52, 4, EnumFacing.Axis.X, stack -> stack.getItem() == WGItems.LOGISTIC_ARM)
                .fuelSlot(4, 72, 74)
                .fuelSlot(5, 90, 74)
                .addInventory().create();
    }

    public Optional<EntityLogisticArm> injectArm()
    {
        // TODO : Spawn arm entity on call and return it for futher logic
        if (this.world.getBlockState(this.pos.north()).getBlock() instanceof BlockCable || this.world.getBlockState(this.pos.south()) instanceof BlockCable || this.world.getBlockState(this.pos.east()).getBlock() instanceof BlockCable || this.world.getBlockState(this.pos.west()) instanceof BlockCable)
        {
            if (!world.isRemote)
            {
                EntityLogisticArm logisticArm = new EntityLogisticArm(world, pos);

                world.spawnEntity(logisticArm);
                return Optional.of(logisticArm);
            }

        }
        return Optional.empty();
    }

    @Override
    public void connectTrigger(EnumFacing facing, CableGrid grid)
    {
        this.grid = (RailGrid) grid;
    }

    @Override
    public void disconnectTrigger(EnumFacing facing, CableGrid grid)
    {
        this.grid = null;
    }

    @Override
    public boolean canConnect(TileCable cable, EnumFacing from)
    {
        return from == this.getFacing();
    }

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(pos).getValue(BlockArmReservoir.FACING);
    }
}
