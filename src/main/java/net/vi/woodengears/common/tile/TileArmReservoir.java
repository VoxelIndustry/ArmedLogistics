package net.vi.woodengears.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.vi.woodengears.common.block.BlockCable;
import net.vi.woodengears.common.entity.EntityLogisticArm;
import net.vi.woodengears.common.grid.CableGrid;
import net.vi.woodengears.common.grid.IConnectionAware;
import net.vi.woodengears.common.grid.IRailConnectable;
import net.vi.woodengears.common.grid.RailGrid;
import net.vi.woodengears.common.item.ItemLogisticArm;
import net.voxelindustry.steamlayer.container.BuiltContainer;
import net.voxelindustry.steamlayer.container.ContainerBuilder;
import net.voxelindustry.steamlayer.container.IContainerProvider;

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
                .player(player).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this.getInventory())
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
