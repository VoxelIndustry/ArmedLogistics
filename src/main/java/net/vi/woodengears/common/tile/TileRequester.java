package net.vi.woodengears.common.tile;

import fr.ourten.teabeans.value.BaseProperty;
import lombok.Getter;
import lombok.Setter;
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
import net.voxelindustry.steamlayer.network.action.ActionSender;
import net.voxelindustry.steamlayer.network.action.IActionReceiver;
import net.voxelindustry.steamlayer.tile.ITileInfoList;

import java.util.List;

public class TileRequester extends TileLogicisticNode implements ITickable, IActionReceiver
{
    @Getter
    private BaseItemRequester requester;

    @Getter
    private BaseProperty<IItemHandler> cachedInventoryProperty;

    private WrappedInventory wrappedInventory;
    private InventoryBuffer  buffer;

    @Getter
    @Setter
    private List<ItemStack> clientCachedRequests;

    public TileRequester()
    {
        super("requester");

        this.cachedInventoryProperty = new BaseProperty<>(null, "cachedInventoryProperty");

        this.buffer = new InventoryBuffer(8, 8 * 64);
        this.wrappedInventory = new WrappedInventory();

        this.requester = new BaseItemRequester(this, this.buffer);
        this.requester.setMode(RequesterMode.KEEP);

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

        list.addText("Mode: " + this.getRequester().getMode());
        if (!this.buffer.isEmpty())
        {
            list.addText("Buffer:");
            this.buffer.getStacks().forEach(stack ->
            {
                if (!stack.isEmpty())
                    list.addItem(stack);
            });
        }

        if (!this.getRequester().getRequests().isEmpty())
        {
            list.addText("Requests:");
            this.getRequester().getRequests().forEach(stack ->
            {
                if (!stack.isEmpty())
                    list.addItem(stack);
            });
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        this.buffer.writeNBT(tag);

        tag.setInteger("requesterMode", this.requester.getMode().ordinal());

        for (int index = 0; index < this.requester.getRequests().size(); index++)
            tag.setTag("request" + index, this.requester.getRequests().get(index).writeToNBT(new NBTTagCompound()));
        tag.setInteger("requests", this.requester.getRequests().size());

        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.buffer.readNBT(tag);

        this.requester.setMode(RequesterMode.values()[tag.getInteger("requesterMode")]);

        int requestCount = tag.getInteger("requests");
        for (int index = 0; index < requestCount; index++)
            this.requester.addRequest(new ItemStack(tag.getCompoundTag("request" + index)));
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("requester", player)
                .player(player).inventory(8, 129).hotbar(8, 187)
                .sync()
                .syncBoolean(getConnectedInventoryProperty()::getValue, getConnectedInventoryProperty()::setValue)
                .syncInventory(this::getConnectedInventory, cachedInventoryProperty::setValue, 10)
                .syncList(this.getRequester()::getRequests, ItemStack.class, null, "requests")
                .syncEnum(this.getRequester()::getMode, this.getRequester()::setMode, RequesterMode.class, "mode")
                .create();
    }

    public void dropBuffer()
    {
        for (ItemStack stack : this.buffer.getStacks())
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
    }

    private void makeOrder(ItemStack stack)
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
        if (!this.requester.getCurrentOrders().isEmpty())
            return;

        IItemHandler inventory = this.getConnectedInventory();
        if (inventory == null)
            return;

        if (this.requester.getMode() == RequesterMode.ONCE || this.requester.getMode() == RequesterMode.CONTINUOUS)
        {
            for (ItemStack stack : this.requester.getRequests())
            {
                int mayInsert = this.getRequester().inventoryAccept(stack);

                if (mayInsert == 0)
                    continue;

                ItemStack order = stack.copy();
                order.setCount(mayInsert);
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

    @Override
    public void handle(ActionSender sender, String actionID, NBTTagCompound payload)
    {
        if ("REQUEST_CHANGE".equals(actionID))
        {
            int index = payload.getInteger("index");
            ItemStack stack = new ItemStack(payload.getCompoundTag("stack"));

            if (index >= getRequester().getRequests().size())
                this.getRequester().addRequest(stack);
            else
                this.getRequester().getRequests().set(index, stack);
        }
        else if ("MODE_CHANGE".equals(actionID))
            this.getRequester().setMode(RequesterMode.values()[payload.getInteger("mode")]);
    }
}
