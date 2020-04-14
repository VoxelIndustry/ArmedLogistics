package net.voxelindustry.armedlogistics.common.grid.logistic.node;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.voxelindustry.armedlogistics.common.grid.logistic.ColoredShipment;
import net.voxelindustry.armedlogistics.common.grid.logistic.LogisticNetwork;
import net.voxelindustry.armedlogistics.common.grid.logistic.LogisticOrder;
import net.voxelindustry.armedlogistics.common.grid.logistic.LogisticShipment;
import net.voxelindustry.armedlogistics.common.serializer.LogisticShipmentSerializer;
import net.voxelindustry.armedlogistics.common.tile.TileLogicisticNode;
import net.voxelindustry.steamlayer.common.utils.ItemUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static net.voxelindustry.armedlogistics.common.grid.logistic.node.RequesterMode.KEEP;

public class BaseItemRequester extends BaseLogisticNode<ItemStack> implements Requester<ItemStack>, ColoredLogisticNode<ItemStack>
{
    @Getter(AccessLevel.PROTECTED)
    private IItemHandler handler;

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

    public BaseItemRequester(TileLogicisticNode tile,
                             IItemHandler handler,
                             Supplier<LogisticNetwork<ItemStack>> networkSupplier)
    {
        super(networkSupplier);

        this.tile = tile;
        this.handler = handler;

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
    public int insert(ItemStack stack)
    {
        if (stack.isEmpty())
            return 0;

        sleep();

        ItemStack toInsert = stack.copy();
        for (int slot = 0; slot < getHandler().getSlots(); slot++)
        {
            ItemStack remainder = getHandler().insertItem(slot, toInsert, true);

            if (remainder.getCount() == stack.getCount())
                continue;

            ItemStack insert = toInsert.copy();
            insert.shrink(remainder.getCount());
            getHandler().insertItem(slot, insert, false);

            toInsert.shrink(insert.getCount());

            if (toInsert.isEmpty())
                break;
        }

        return stack.getCount() - toInsert.getCount();
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
        if (quantity == 0 || mode == KEEP)
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

    public int inventoryAccept(ItemStack stack)
    {
        int canInsert = 0;
        for (int i = 0; i < handler.getSlots(); i++)
        {
            canInsert += stack.getCount() - handler.insertItem(i, stack, true).getCount();
            if (canInsert >= stack.getCount())
                return stack.getCount();
        }
        return canInsert;
    }

    public int inventoryContains(ItemStack stack)
    {
        int contained = 0;
        for (int i = 0; i < handler.getSlots(); i++)
        {
            ItemStack inSlot = handler.getStackInSlot(i);
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
    public boolean removeShipment(LogisticShipment<ItemStack> shipment)
    {
        return shipments.remove(shipment);
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
    public boolean removeColoredShipment(ColoredShipment<ItemStack> shipment)
    {
        return coloredShipments.remove(shipment);
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

        if (tile.getCable() == null || tile.getCable().getGrid() == -1)
            return;

        tile.getCable().getGridObject().getStackNetwork().completeShipment(shipment);
    }

    @Override
    public void deliverColoredShipment(ColoredShipment<ItemStack> shipment)
    {
        coloredShipments.remove(shipment);

        if (tile.getCable() == null || tile.getCable().getGrid() == -1)
            return;

        tile.getCable().getGridObject().getStackNetwork().completeColoredShipment(shipment);
    }

    public void makeOrder(ItemStack stack, boolean addRequest)
    {
        if (addRequest)
            addRequest(stack);
        getNetworkSupplier().get().makeOrder(this, stack);
    }

    public CompoundNBT toNBT(CompoundNBT tag)
    {
        int i = 0;
        for (LogisticShipment<ItemStack> shipment : shipments)
        {
            tag.put("shipment" + i, LogisticShipmentSerializer.itemShipmentToNBT(shipment));
            i++;
        }
        tag.putInt("shipmentCount", i);

        i = 0;
        for (ColoredShipment<ItemStack> shipment : coloredShipments)
        {
            tag.put("coloredShipment" + i, LogisticShipmentSerializer.coloredItemShipmentToNBT(shipment));
            i++;
        }
        tag.putInt("coloredShipmentCount", i);

        tag.putInt("requesterMode", getMode().ordinal());

        for (int index = 0; index < getRequests().size(); index++)
            tag.put("request" + index, getRequests().get(index).serializeNBT());
        tag.putInt("requests", getRequests().size());

        return tag;
    }

    public void fromNBT(CompoundNBT tag)
    {
        int count = tag.getInt("shipmentCount");

        for (int i = 0; i < count; i++)
            shipments.add(LogisticShipmentSerializer.itemShipmentFromNBT(tag.getCompound("shipment" + i)));

        count = tag.getInt("coloredShipmentCount");

        for (int i = 0; i < count; i++)
            coloredShipments.add(LogisticShipmentSerializer.coloredItemShipmentFromNBT(tag.getCompound("shipment" + i)));

        setMode(RequesterMode.values()[tag.getInt("requesterMode")]);

        int requestCount = tag.getInt("requests");
        for (int index = 0; index < requestCount; index++)
            addRequest(ItemStack.read(tag.getCompound("request" + index)));
    }
}
