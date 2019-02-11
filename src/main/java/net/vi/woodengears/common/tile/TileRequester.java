package net.vi.woodengears.common.tile;

import fr.ourten.teabeans.value.BaseProperty;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.IItemHandler;
import net.vi.woodengears.common.grid.logistic.node.BaseItemRequester;
import net.vi.woodengears.common.grid.logistic.node.InventoryBuffer;
import net.vi.woodengears.common.grid.logistic.node.RequesterMode;
import net.voxelindustry.steamlayer.container.BuiltContainer;
import net.voxelindustry.steamlayer.container.ContainerBuilder;
import net.voxelindustry.steamlayer.tile.ITileInfoList;

public class TileRequester extends TileLogicisticNode implements ITickable
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
        this.requester.setMode(RequesterMode.CONTINUOUS);

        this.getConnectedInventoryProperty().addListener(obs -> wrappedInventory.setWrapped(getConnectedInventory()));
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;

        if (this.world.getTotalWorldTime() % 32 != ((this.pos.getX() ^ this.pos.getZ()) & 31))
            return;
        this.randomTick();
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        super.addInfo(list);

        list.addText("Buffer:");
        this.buffer.getStacks().forEach(stack ->
        {
            if (!stack.isEmpty())
                list.addItem(stack);
        });
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        this.buffer.writeNBT(tag);

        tag.setInteger("requesterMode", this.requester.getMode().ordinal());
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.buffer.readNBT(tag);

        this.requester.setMode(RequesterMode.values()[tag.getInteger("requesterMode")]);
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

    public void makeOrder(ItemStack stack)
    {
        if (this.getCable() == null || this.getCable().getGrid() == -1 || this.getConnectedInventory() == null)
            return;

        System.out.println("Order made " + stack);
        this.makeOrder(stack, true);
    }

    private void makeOrder(ItemStack stack, boolean addRequest)
    {
        if (addRequest)
            this.getRequester().addRequest(stack);
        this.getCable().getGridObject().getStackNetwork().makeOrder(this.getRequester(), stack);
    }

    public void randomTick()
    {
        if (this.requester.getMode() == RequesterMode.ONCE || !this.requester.getCurrentOrders().isEmpty())
            return;

        IItemHandler inventory = this.getConnectedInventory();
        if (inventory == null)
            return;

        if (this.requester.getMode() == RequesterMode.CONTINUOUS)
        {
            for (ItemStack stack : this.requester.getRequests())
            {
                int mayInsert = this.getRequester().inventoryAccept(stack);

                if (mayInsert == 0)
                    continue;

                ItemStack order = stack.copy();
                order.setCount(Math.min(stack.getMaxStackSize(), mayInsert));
                this.makeOrder(order, false);
            }
        }
        else if (this.requester.getMode() == RequesterMode.KEEP)
        {
            for (ItemStack stack : this.requester.getRequests())
            {
                int contained = this.getRequester().inventoryContains(stack);
                int mayInsert = this.getRequester().inventoryAccept(stack);

                if (contained >= stack.getCount() || mayInsert == 0)
                    continue;

                ItemStack order = stack.copy();
                order.setCount(Math.min(stack.getCount() - contained, mayInsert));
                this.makeOrder(order, false);
            }
        }
    }
}
