package net.opmcorp.woodengears.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.opmcorp.woodengears.common.block.BlockCable;
import net.opmcorp.woodengears.common.container.BuiltContainer;
import net.opmcorp.woodengears.common.container.ContainerBuilder;
import net.opmcorp.woodengears.common.container.IContainerProvider;
import net.opmcorp.woodengears.common.entity.EntityLogisticArm;
import net.opmcorp.woodengears.common.grid.CableGrid;
import net.opmcorp.woodengears.common.grid.IConnectionAware;
import net.opmcorp.woodengears.common.grid.IRailConnectable;
import net.opmcorp.woodengears.common.grid.RailGrid;
import net.opmcorp.woodengears.common.item.ItemLogisticArm;

import java.util.Optional;

public class TileArmReservoir extends TileInventoryBase implements IContainerProvider, IRailConnectable,
        IConnectionAware
{
    private RailGrid grid;

    public TileArmReservoir()
    {
        super("armreservoir", 9);
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("armreservoir", player)
                .player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this)
                .filterSlotLine(0, 17, 26, 8, EnumFacing.Axis.X, stack -> stack.getItem() instanceof ItemLogisticArm)
                .fuelSlot(8, 80, 62)
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
}
