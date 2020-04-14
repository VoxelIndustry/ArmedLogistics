package net.voxelindustry.armedlogistics.common.tile;

import fr.ourten.teabeans.value.BaseProperty;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.items.IItemHandler;
import net.voxelindustry.armedlogistics.common.grid.logistic.node.BaseItemRequester;
import net.voxelindustry.armedlogistics.common.grid.logistic.node.InventoryBuffer;
import net.voxelindustry.armedlogistics.common.grid.logistic.node.RequesterMode;
import net.voxelindustry.armedlogistics.common.setup.ALContainers;
import net.voxelindustry.armedlogistics.common.setup.ALTiles;
import net.voxelindustry.steamlayer.container.ContainerBuilder;
import net.voxelindustry.steamlayer.network.action.ActionSender;
import net.voxelindustry.steamlayer.network.action.IActionReceiver;
import net.voxelindustry.steamlayer.tile.ITileInfoList;

import java.util.List;

public class TileRequester extends TileLogicisticNode implements ITickableTileEntity, IActionReceiver
{
    @Getter
    private BaseItemRequester requester;

    @Getter
    private BaseProperty<IItemHandler> cachedInventoryProperty;

    private InventoryBuffer buffer;

    @Getter
    @Setter
    private List<ItemStack> clientCachedRequests;

    public TileRequester()
    {
        super(ALTiles.REQUESTER, "requester");

        cachedInventoryProperty = new BaseProperty<>(null, "cachedInventoryProperty");

        buffer = new InventoryBuffer(8, 8 * 64);

        requester = new BaseItemRequester(this, getWrappedInventories(), () -> getCable().getGridObject().getStackNetwork());
        requester.setMode(RequesterMode.KEEP);
    }

    @Override
    public void tick()
    {
        if (isClient())
            return;

        if (world.getGameTime() % 32 != ((pos.getX() ^ pos.getZ()) & 31))
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
    public CompoundNBT write(CompoundNBT tag)
    {
        super.write(tag);

        buffer.writeNBT(tag);

        requester.toNBT(tag);
        return tag;
    }

    @Override
    public void read(CompoundNBT tag)
    {
        super.read(tag);

        buffer.readNBT(tag);
        requester.fromNBT(tag);
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player)
    {
        return new ContainerBuilder(ALContainers.REQUESTER, player)
                .player(player).inventory(8, 161).hotbar(8, 219)
                .emptyTile(this)
                .sync()
                .syncBoolean(getConnectedInventoryProperty()::getValue, getConnectedInventoryProperty()::setValue)
                .syncInventory(this::getWrappedInventories, getCachedInventoryProperty()::setValue, 10)
                .syncList(getRequester()::getRequests, ItemStack.class, null, "requests")
                .syncEnum(getRequester()::getMode, getRequester()::setMode, RequesterMode.class, "mode")
                .syncEnumList(this::getAdjacentFacings, Direction.class, null, "facings")
                .create(windowId);
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

        if (!getConnectedInventoryProperty().getValue())
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
    public void handle(ActionSender sender, String actionID, CompoundNBT payload)
    {
        if ("REQUEST_CHANGE".equals(actionID))
        {
            int index = payload.getInt("index");
            ItemStack stack = ItemStack.read(payload.getCompound("stack"));

            if (index >= getRequester().getRequests().size())
                getRequester().addRequest(stack);
            else
                getRequester().getRequests().set(index, stack);
        }
        else if ("MODE_CHANGE".equals(actionID))
            getRequester().setMode(RequesterMode.values()[payload.getInt("mode")]);
        else if ("FACING_ADD".equals(actionID))
        {
            Direction facing = Direction.byIndex(payload.getInt("facing"));
            if (!getAdjacentFacings().contains(facing))
                getAdjacentFacings().add(facing);
            markDirty();
        }
        else if ("FACING_REMOVE".equals(actionID))
        {
            getAdjacentFacings().remove(Direction.byIndex(payload.getInt("facing")));
            markDirty();
        }
    }
}
