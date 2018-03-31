package net.opmcorp.woodengears.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.util.EnumFacing;
import net.opmcorp.woodengears.common.container.BuiltContainer;
import net.opmcorp.woodengears.common.container.ContainerBuilder;
import net.opmcorp.woodengears.common.container.IContainerProvider;

public class TileArmReservoir extends TileInventoryBase implements IContainerProvider
{
    public TileArmReservoir()
    {
        super("armreservoir", 9);
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        // TODO : Replace filter with arm item
        return new ContainerBuilder("armreservoir", player)
                .player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this)
                .filterSlotLine(0, 17, 26, 8, EnumFacing.Axis.X, stack -> stack.getItem() instanceof ItemDye)
                .fuelSlot(8, 80, 62)
                .addInventory().create();
    }
}
