package net.vi.woodengears.common.tile;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.vi.woodengears.common.grid.logistic.ProviderType;
import net.vi.woodengears.common.grid.logistic.node.BaseItemProvider;
import net.vi.woodengears.common.grid.logistic.node.BaseItemStorage;
import net.vi.woodengears.common.grid.logistic.node.InventoryBuffer;
import net.voxelindustry.steamlayer.container.BuiltContainer;
import net.voxelindustry.steamlayer.container.ContainerBuilder;
import net.voxelindustry.steamlayer.tile.ITileInfoList;

public class TileStorage extends TileLogicisticNode
{
    @Getter
    private BaseItemProvider provider;

    private WrappedInventory wrappedInventory;
    private InventoryBuffer  buffer;

    public TileStorage()
    {
        super("storage");

        buffer = new InventoryBuffer(8, 8 * 64);
        wrappedInventory = new WrappedInventory();

        provider = new BaseItemStorage(this, ProviderType.STORAGE, wrappedInventory,
                () -> getCable().getGridObject().getStackNetwork(), buffer);

        getConnectedInventoryProperty().addListener(obs -> wrappedInventory.setWrapped(getConnectedInventory()));
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        super.addInfo(list);

        if (!buffer.isEmpty())
        {
            list.addText("Buffer:");
            buffer.getStacks().forEach(stack ->
            {
                if (!stack.isEmpty())
                    list.addItem(stack);
            });
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        buffer.writeNBT(tag);

        provider.toNBT(tag);
        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        buffer.readNBT(tag);
        provider.fromNBT(tag);
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("storage", player)
                .player(player).inventory(8, 103).hotbar(8, 161)
                .sync()
                .syncBoolean(getConnectedInventoryProperty()::getValue, getConnectedInventoryProperty()::setValue)
                .syncInventory(this::getConnectedInventory, getCachedInventoryProperty()::setValue, 10)
                .create();
    }

    public void dropBuffer()
    {
        for (ItemStack stack : buffer.getStacks())
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
    }
}
