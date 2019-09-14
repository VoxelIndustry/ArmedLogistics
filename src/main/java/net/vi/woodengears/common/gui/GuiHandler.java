package net.vi.woodengears.common.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.client.gui.GuiArmReservoir;
import net.vi.woodengears.client.gui.GuiProvider;
import net.vi.woodengears.client.gui.GuiRequester;
import net.vi.woodengears.common.tile.TileArmReservoir;
import net.vi.woodengears.common.tile.TileProvider;
import net.vi.woodengears.common.tile.TileRequester;
import net.vi.woodengears.common.tile.TileStorage;
import net.voxelindustry.brokkgui.wrapper.impl.BrokkGuiManager;
import net.voxelindustry.steamlayer.container.IContainerProvider;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler
{
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

        if (tile instanceof IContainerProvider)
            return ((IContainerProvider) tile).createContainer(player);
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (GuiType.values()[ID])
        {
            case ARM_RESERVOIR:
                return BrokkGuiManager.getBrokkGuiContainer(WoodenGears.MODID, new GuiArmReservoir(player,
                        (TileArmReservoir) world.getTileEntity(new BlockPos(x, y, z))));
            case PROVIDER:
                return BrokkGuiManager.getBrokkGuiContainer(WoodenGears.MODID, new GuiProvider(player,
                        (TileProvider) world.getTileEntity(new BlockPos(x, y, z))));
            case REQUESTER:
                return BrokkGuiManager.getBrokkGuiContainer(WoodenGears.MODID, new GuiRequester(player,
                        (TileRequester) world.getTileEntity(new BlockPos(x, y, z))));
            case STORAGE:
                return BrokkGuiManager.getBrokkGuiContainer(WoodenGears.MODID, new GuiProvider(player,
                        (TileStorage) world.getTileEntity(new BlockPos(x, y, z))));
        }
        return null;
    }
}
