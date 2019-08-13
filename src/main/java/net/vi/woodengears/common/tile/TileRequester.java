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

        cachedInventoryProperty = new BaseProperty<>(null, "cachedInventoryProperty");

        buffer = new InventoryBuffer(8, 8 * 64);
        wrappedInventory = new WrappedInventory();

        requester = new BaseItemRequester(this, () -> getCable().getGridObject().getStackNetwork(), buffer);
        requester.setMode(RequesterMode.KEEP);

        getConnectedInventoryProperty().addListener(obs -> wrappedInventory.setWrapped(getConnectedInventory()));
    }

    @Override
    public void update()
    {
        if (isClient())
            return;

        if (world.getTotalWorldTime() % 32 != ((pos.getX() ^ pos.getZ()) & 31))
            return;
        randomTick();
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        super.addInfo(list);

        list.addText("Mode: " + getRequester().getMode());
        if (!buffer.isEmpty())
        {
            list.addText("Buffer:");
            buffer.getStacks().forEach(stack ->
            {
                if (!stack.isEmpty())
                    list.addItem(stack);
            });
        }

        if (!getRequester().getRequests().isEmpty())
        {
            list.addText("Requests:");
            getRequester().getRequests().forEach(stack ->
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

        buffer.writeNBT(tag);

        requester.toNBT(tag);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        buffer.readNBT(tag);
        requester.fromNBT(tag);
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("requester", player)
                .player(player).inventory(8, 129).hotbar(8, 187)
                .sync()
                .syncBoolean(getConnectedInventoryProperty()::getValue, getConnectedInventoryProperty()::setValue)
                .syncInventory(this::getConnectedInventory, cachedInventoryProperty::setValue, 10)
                .syncList(getRequester()::getRequests, ItemStack.class, null, "requests")
                .syncEnum(getRequester()::getMode, getRequester()::setMode, RequesterMode.class, "mode")
                .create();
    }

    public void dropBuffer()
    {
        for (ItemStack stack : buffer.getStacks())
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
    }

    public void randomTick()
    {
        if (getCable() == null || getCable().getGridObject() == null)
            return;
        if (!requester.getCurrentOrders().isEmpty())
            return;

        IItemHandler inventory = getConnectedInventory();
        if (inventory == null)
            return;

        if (requester.getMode() == RequesterMode.ONCE || requester.getMode() == RequesterMode.CONTINUOUS)
        {
            for (ItemStack stack : requester.getRequests())
            {
                int mayInsert = getRequester().inventoryAccept(stack);

                if (mayInsert == 0)
                    continue;

                ItemStack order = stack.copy();
                order.setCount(mayInsert);
                requester.makeOrder(order, false);
            }
        }
        else if (requester.getMode() == RequesterMode.KEEP)
        {
            for (ItemStack stack : requester.getRequests())
            {
                int contained = getRequester().inventoryContains(stack);
                int mayInsert = getRequester().inventoryAccept(stack);

                if (contained >= stack.getCount() || mayInsert == 0)
                    continue;

                ItemStack order = stack.copy();
                order.setCount(Math.min(stack.getCount() - contained, mayInsert));
                requester.makeOrder(order, false);
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
                getRequester().addRequest(stack);
            else
                getRequester().getRequests().set(index, stack);
        }
        else if ("MODE_CHANGE".equals(actionID))
            getRequester().setMode(RequesterMode.values()[payload.getInteger("mode")]);
    }
}
