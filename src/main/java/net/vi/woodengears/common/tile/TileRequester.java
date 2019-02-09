package net.vi.woodengears.common.tile;

import fr.ourten.teabeans.value.BaseProperty;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import net.vi.woodengears.common.grid.logistic.node.BaseItemRequester;
import net.vi.woodengears.common.grid.logistic.node.InventoryBuffer;
import net.voxelindustry.steamlayer.container.BuiltContainer;
import net.voxelindustry.steamlayer.container.ContainerBuilder;

public class TileRequester extends TileLogicisticNode
{
    @Getter
    private BaseItemRequester requester;

    @Getter
    private BaseProperty<IItemHandler> cachedInventoryProperty;

    private WrappedInventory wrappedInventory;
    private InventoryBuffer  buffer;

    public TileRequester()
    {
        super("requester");

        this.cachedInventoryProperty = new BaseProperty<>(null, "cachedInventoryProperty");

        this.buffer = new InventoryBuffer(8, 8 * 64);
        this.wrappedInventory = new WrappedInventory();

        this.requester = new BaseItemRequester(this, this.buffer);

        this.getConnectedInventoryProperty().addListener(obs -> wrappedInventory.setWrapped(getConnectedInventory()));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        this.buffer.writeNBT(tag);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.buffer.readNBT(tag);
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("requester", player)
                .player(player).inventory(8, 103).hotbar(8, 161)
                .addInventory()
                .syncBooleanValue(getConnectedInventoryProperty()::getValue, getConnectedInventoryProperty()::setValue)
                .syncInventory(this::getConnectedInventory, cachedInventoryProperty::setValue, 10)
                .create();
    }

    public void dropBuffer()
    {
        for (ItemStack stack : this.buffer.getStacks())
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
    }
}
