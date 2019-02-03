package net.vi.woodengears.common.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.client.GuiArmReservoir;
import net.vi.woodengears.client.GuiProvider;
import net.vi.woodengears.common.tile.TileArmReservoir;
import net.vi.woodengears.common.tile.TileProvider;
import net.voxelindustry.brokkgui.wrapper.impl.BrokkGuiManager;
import net.voxelindustry.steamlayer.container.IContainerProvider;

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
            case PROVIDER:
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
            case PROVIDER:
                return BrokkGuiManager.getBrokkGuiContainer(WoodenGears.MODID, new GuiProvider(player,
                        (TileProvider) world.getTileEntity(new BlockPos(x, y, z))));
        }
        return null;
    }
}
