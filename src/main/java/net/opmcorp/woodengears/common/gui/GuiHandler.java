package net.opmcorp.woodengears.common.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.opmcorp.woodengears.client.GuiArmReservoir;
import net.opmcorp.woodengears.common.container.IContainerProvider;
import net.opmcorp.woodengears.common.tile.TileArmReservoir;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler
{
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (GuiType.values()[ID])
        {
            case ARM_RESERVOIR:
                return ((IContainerProvider) world.getTileEntity(new BlockPos(x, y, z))).createContainer(player);
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (GuiType.values()[ID])
        {
            case ARM_RESERVOIR:
                return new GuiArmReservoir(player, (TileArmReservoir) world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }
}
