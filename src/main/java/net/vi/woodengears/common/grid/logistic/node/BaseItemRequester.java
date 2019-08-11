package net.vi.woodengears.common.grid.logistic.node;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.vi.woodengears.common.grid.logistic.ColoredShipment;
import net.vi.woodengears.common.grid.logistic.LogisticOrder;
import net.vi.woodengears.common.grid.logistic.LogisticShipment;
import net.vi.woodengears.common.serializer.LogisticShipmentSerializer;
import net.vi.woodengears.common.tile.TileLogicisticNode;
import net.voxelindustry.steamlayer.utils.ItemUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BaseItemRequester extends BaseLogisticNode implements Requester<ItemStack>
{
    @Getter(AccessLevel.PROTECTED)
    private InventoryBuffer buffer;

    private TileLogicisticNode tile;

    @Getter
    private List<ItemStack>                requests;
    @Getter
    private List<LogisticOrder<ItemStack>> currentOrders;

    @Getter
    @Setter
    private RequesterMode mode;

    private List<LogisticShipment<ItemStack>> shipments;
    private List<ColoredShipment<ItemStack>>  coloredShipments;

    public BaseItemRequester(TileLogicisticNode tile, InventoryBuffer buffer)
    {
        this.tile = tile;

        this.buffer = buffer;
        requests = new ArrayList<>();
        currentOrders = new ArrayList<>();

        shipments = new ArrayList<>();
        coloredShipments = new ArrayList<>();
    }

    @Override
    public BlockPos getRailPos()
    {
        return tile.getRailPos();
    }

    @Override
    public int insert(ItemStack value)
    {
        emptyBuffer();
        if (buffer.isFull())
            return 0;

        sleep();

        int added = buffer.add(value).getCount();

        removeRequest(value, added);

        emptyBuffer();
        return added;
    }

    @Override
    public boolean isBufferFull()
    {
        return buffer.isFull();
    }

    @Override
    public void addRequest(ItemStack value)
    {
        requests.add(value);
    }

    @Override
    public void addOrder(LogisticOrder<ItemStack> order)
    {
        currentOrders.add(order);
    }

    @Override
    public void removeOrder(LogisticOrder<ItemStack> order)
    {
        currentOrders.remove(order);
    }

    public void removeRequest(ItemStack value, int quantity)
    {
        if (quantity == 0)
            return;

        if (mode == RequesterMode.KEEP)
            return;

        int index = -1;
        for (ItemStack stack : requests)
        {
            if (ItemUtils.deepEquals(value, stack))
            {
                index = requests.indexOf(stack);
                break;
            }
        }

        if (index == -1)
            return;

        if (mode == RequesterMode.CONTINUOUS)
        {
            index = index == requests.size() - 1 ? 0 : index + 1;
            ItemStack request = requests.get(index);
            int mayInsert = inventoryAccept(request);

            if (mayInsert == 0)
                return;

            ItemStack order = request.copy();
            order.setCount(Math.min(request.getMaxStackSize(), mayInsert));
            tile.getCable().getGridObject().getStackNetwork().makeOrder(this, order);
            return;
        }

        requests.get(index).shrink(quantity);

        if (requests.get(index).isEmpty())
            requests.remove(index);
    }

    public ItemStack getRequest(int index)
    {
        return requests.get(index);
    }

    public void removeRequest(int index)
    {
        requests.remove(index);
    }

    public void emptyBuffer()
    {
        IItemHandler inventory = tile.getConnectedInventory();
        if (inventory != null)
        {
            for (ItemStack stack : buffer.getStacks())
            {
                for (int i = 0; i < inventory.getSlots(); i++)
                {
                    if (stack.isEmpty())
                        break;
                    ItemStack remainder = inventory.insertItem(i, stack, true);

                    if (!remainder.isEmpty() && remainder.getCount() == stack.getCount())
                        continue;

                    remainder = inventory.insertItem(i, stack.copy(), false);

                    ItemStack inserted = stack.copy();
                    if (!remainder.isEmpty())
                        inserted.shrink(remainder.getCount());

                    buffer.remove(stack);
                }
            }
        }
    }

    public int inventoryAccept(ItemStack stack)
    {
        IItemHandler inventory = tile.getConnectedInventory();

        int canInsert = 0;
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            canInsert += stack.getCount() - inventory.insertItem(i, stack, true).getCount();
            if (canInsert >= stack.getCount())
                return stack.getCount();
        }
        return canInsert;
    }

    public int inventoryContains(ItemStack stack)
    {
        IItemHandler inventory = tile.getConnectedInventory();

        int contained = 0;
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            ItemStack inSlot = inventory.getStackInSlot(i);
            if (ItemUtils.deepEquals(stack, inSlot))
                contained += inSlot.getCount();
        }
        return contained;
    }

    @Override
    public void addShipment(LogisticShipment<ItemStack> shipment)
    {
        shipments.add(shipment);
    }

    @Override
    public Collection<LogisticShipment<ItemStack>> getShipments()
    {
        return shipments;
    }

    @Override
    public void addColoredShipment(ColoredShipment<ItemStack> shipment)
    {
        coloredShipments.add(shipment);
    }

    @Override
    public Collection<ColoredShipment<ItemStack>> getColoredShipments()
    {
        return coloredShipments;
    }

    @Override
    public void deliverShipment(LogisticShipment<ItemStack> shipment)
    {
        shipments.remove(shipment);

        if (tile.getCable() == null || tile.getCable().getGrid() == -1 || tile.getConnectedInventory() == null)
            return;

        tile.getCable().getGridObject().getStackNetwork().completeShipment(shipment);
    }

    @Override
    public void deliverShipment(ColoredShipment<ItemStack> shipment)
    {
        coloredShipments.remove(shipment);

        if (tile.getCable() == null || tile.getCable().getGrid() == -1 || tile.getConnectedInventory() == null)
            return;

        tile.getCable().getGridObject().getStackNetwork().completeColoredShipment(shipment);
    }

    public NBTTagCompound toNBT(NBTTagCompound tag)
    {
        int i = 0;
        for (LogisticShipment<ItemStack> shipment : shipments)
        {
            tag.setTag("shipment" + i, LogisticShipmentSerializer.itemShipmentToNBT(shipment));
            i++;
        }
        tag.setInteger("shipmentCount", i);

        i = 0;
        for (ColoredShipment<ItemStack> shipment : coloredShipments)
        {
            tag.setTag("coloredShipment" + i, LogisticShipmentSerializer.coloredItemShipmentToNBT(shipment));
            i++;
        }
        tag.setInteger("coloredShipmentCount", i);

        return tag;
    }

    public void fromNBT(NBTTagCompound tag)
    {
        int count = tag.getInteger("shipmentCount");

        for (int i = 0; i < count; i++)
            shipments.add(LogisticShipmentSerializer.itemShipmentFromNBT(tag.getCompoundTag("shipment" + i)));

        count = tag.getInteger("coloredShipmentCount");

        for (int i = 0; i < count; i++)
            coloredShipments.add(LogisticShipmentSerializer.coloredItemShipmentFromNBT(tag.getCompoundTag("shipment" + i)));
    }
}
