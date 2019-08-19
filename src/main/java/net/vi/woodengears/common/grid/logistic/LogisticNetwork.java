package net.vi.woodengears.common.grid.logistic;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.vi.woodengears.common.grid.RailGrid;
import net.vi.woodengears.common.grid.logistic.node.ColoredLogisticNode;
import net.vi.woodengears.common.grid.logistic.node.ColoredProvider;
import net.vi.woodengears.common.grid.logistic.node.Provider;
import net.vi.woodengears.common.grid.logistic.node.Requester;
import net.vi.woodengears.common.grid.logistic.node.Storage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import static net.vi.woodengears.common.grid.logistic.OrderState.*;
import static net.vi.woodengears.common.grid.logistic.ProviderType.STORAGE;
import static net.vi.woodengears.common.grid.logistic.ProviderType.VALUES;

public class LogisticNetwork<T>
{
    private ListMultimap<ProviderType, Provider<T>> providers;
    private List<Requester<T>>                      requesters;
    private List<T>                                 compressedStacks;

    private PriorityQueue<LogisticOrder<T>> stackOrders;
    private PriorityQueue<ColoredOrder<T>>  coloredOrders;

    private PriorityQueue<LogisticOrder<T>> removalOrders;
    private PriorityQueue<ColoredOrder<T>>  coloredRemovalOrders;

    @Getter
    private List<LogisticShipment<T>> shipments;
    @Getter
    private List<ColoredShipment<T>>  coloredShipments;

    @Getter
    @Setter
    private boolean needContentsRefresh;

    @Getter
    private Class<T>                 typeClass;
    private LogisticGridFunctions<T> functions;

    @Getter
    @Setter
    private RailGrid grid;

    public LogisticNetwork(RailGrid grid, Class<T> typeClass, LogisticGridFunctions<T> functions)
    {
        this.grid = grid;

        this.typeClass = typeClass;
        this.functions = functions;

        providers = MultimapBuilder.enumKeys(ProviderType.class).arrayListValues().build();
        requesters = new ArrayList<>();

        compressedStacks = NonNullList.create();

        stackOrders = new PriorityQueue<>(Comparator.comparingInt(order -> order.getState().ordinal()));
        coloredOrders = new PriorityQueue<>(Comparator.comparingInt(order -> order.getState().ordinal()));
        removalOrders = new PriorityQueue<>(Comparator.comparingInt(order -> order.getState().ordinal()));
        coloredRemovalOrders = new PriorityQueue<>(Comparator.comparingInt(order -> order.getState().ordinal()));

        shipments = new ArrayList<>();
        coloredShipments = new ArrayList<>();
    }

    public void tick()
    {
        if (providers.isEmpty())
            return;

        providers.values().forEach(Provider::networkTick);

        processOrders();
        processColoredOrders();

        processRemovalOrders();
        processColoredRemovalOrders();
    }

    private List<Provider<T>> getSortedProviders(ProviderType type, BlockPos destination)
    {
        if (getGrid() == null)
            return providers.get(type);
        else
            return providers.get(type).stream()
                    .filter(provider -> !provider.isBufferFull())
                    .sorted(Comparator.comparingInt(provider -> grid.getDistanceBetween(provider.getRailPos(),
                            destination))).collect(Collectors.toList());
    }

    private void processOrders()
    {
        stackOrders.removeIf(order -> order.getState() == COMPLETED);

        for (LogisticOrder<T> stackOrder : stackOrders)
        {
            if (stackOrder.getState() != SUBMITTED && stackOrder.getState() != SHORTAGE)
                continue;

            int toExtract = functions.getQuantity(stackOrder.getContent());

            for (ProviderType type : VALUES)
            {
                toExtract = getToExtract(stackOrder, toExtract, type);
                if (toExtract == 0)
                    break;
            }

            if (toExtract == 0)
                stackOrder.setState(SHIPPING);

            if (toExtract > 0 && stackOrder.getState() == SUBMITTED)
                stackOrder.setState(SHORTAGE);
        }
    }

    private int getToExtract(LogisticOrder<T> stackOrder, int toExtract, ProviderType type)
    {
        for (Provider<T> provider : getSortedProviders(type, stackOrder.getSource().getRailPos()))
        {
            if (provider.isBufferFull())
                continue;

            int part = provider.containedPart(stackOrder.getContent());

            if (part == 0)
                continue;

            T extracted = provider.extract(functions.changeQuantity(stackOrder.getContent(), toExtract));

            LogisticShipment<T> shipment = createShipment(extracted, provider.getRailPos(),
                    stackOrder.getSource().getRailPos());

            provider.addShipment(shipment);
            stackOrder.getSource().addShipment(shipment);

            stackOrder.getShippedParts().add(shipment);
            toExtract -= functions.getQuantity(extracted);

            if (toExtract == 0)
                break;
        }
        return toExtract;
    }

    private void processColoredOrders()
    {
        coloredOrders.removeIf(order -> order.getState() == COMPLETED);

        for (ColoredOrder<T> coloredOrder : coloredOrders)
        {
            if (coloredOrder.getState() != SUBMITTED && coloredOrder.getState() != SHORTAGE)
                continue;

            int toExtract = coloredOrder.getContent().getQuantity();

            for (ProviderType type : VALUES)
            {
                toExtract = getColoredToExtract(coloredOrder, toExtract, type);
                if (toExtract == 0)
                    break;
            }

            if (toExtract == 0)
                coloredOrder.setState(SHIPPING);

            if (toExtract > 0 && coloredOrder.getState() == SUBMITTED)
                coloredOrder.setState(SHORTAGE);
        }
    }

    private int getColoredToExtract(ColoredOrder<T> coloredOrder, int toExtract, ProviderType type)
    {
        for (Provider<T> provider : getSortedProviders(type, coloredOrder.getSource().getRailPos()))
        {
            if (!provider.isColored() || provider.isBufferFull())
                continue;

            ColoredProvider<T> coloredProvider = (ColoredProvider<T>) provider;
            int part = coloredProvider.containedPart(coloredOrder.getContent());

            if (part == 0)
                continue;

            ColoredStack stack = new ColoredStack(coloredOrder.getContent().getColor(), toExtract);
            List<T> extracted = coloredProvider.extract(stack);

            for (T value : extracted)
            {
                ColoredShipment<T> shipment = createColoredShipment(value, coloredOrder.getContent(),
                        provider.getRailPos(), coloredOrder.getSource().getRailPos());

                coloredProvider.addColoredShipment(shipment);
                ((ColoredLogisticNode<T>) coloredOrder.getSource()).addColoredShipment(shipment);

                coloredOrder.getShippedParts().add(shipment);
            }
            toExtract -= extracted.stream().mapToInt(functions::getQuantity).sum();

            if (toExtract == 0)
                break;
        }
        return toExtract;
    }

    private void processRemovalOrders()
    {
        removalOrders.removeIf(order -> order.getState() == COMPLETED);

        for (LogisticOrder<T> removalOrder : removalOrders)
        {
            if (removalOrder.getState() != SUBMITTED && removalOrder.getState() != SHORTAGE)
                continue;

            int toInsert = functions.getQuantity(removalOrder.getContent());

            for (Provider<T> provider : getSortedProviders(STORAGE, removalOrder.getSource().getRailPos()))
            {
                Storage<T> storage = (Storage<T>) provider;

                int part = storage.insertablePart(removalOrder.getContent());

                if (part == 0)
                    continue;

                T inserted = storage.insert(functions.changeQuantity(removalOrder.getContent(), part));

                LogisticShipment<T> shipment = createShipment(inserted, provider.getRailPos(),
                        removalOrder.getSource().getRailPos());

                provider.addShipment(shipment);
                removalOrder.getSource().addShipment(shipment);

                removalOrder.getShippedParts().add(shipment);
                toInsert -= functions.getQuantity(inserted);

                if (toInsert == 0)
                    break;
            }

            if (toInsert == 0)
                removalOrder.setState(SHIPPING);

            if (toInsert > 0 && removalOrder.getState() == SUBMITTED)
                removalOrder.setState(SHORTAGE);
        }
    }

    private void processColoredRemovalOrders()
    {

    }

    private LogisticShipment<T> createShipment(T toShip, BlockPos from, BlockPos to)
    {
        LogisticShipment<T> shipment = new LogisticShipment<>(from, to, toShip);

        shipments.add(shipment);
        return shipment;
    }

    private ColoredShipment<T> createColoredShipment(T toShip, ColoredStack coloredStack, BlockPos from, BlockPos to)
    {
        ColoredShipment<T> shipment = new ColoredShipment<>(from, to, coloredStack, toShip);

        coloredShipments.add(shipment);
        return shipment;
    }

    public LogisticOrder<T> makeOrder(Requester<T> requester, T stack)
    {
        LogisticOrder<T> order = new LogisticOrder<>(stack, requester);
        order.setState(SUBMITTED);
        requester.addOrder(order);

        stackOrders.add(order);
        return order;
    }

    public ColoredOrder<T> makeOrder(Requester<T> requester, EnumDyeColor color, int quantity)
    {
        ColoredOrder<T> order = new ColoredOrder<>(new ColoredStack(color, quantity), requester);
        order.setState(SUBMITTED);
        // TODO : Colored requesters
        //order.getDestination().addOrder(order);

        coloredOrders.add(order);
        return order;
    }

    public LogisticOrder<T> makeRemovalOrder(Provider<T> provider, T stack)
    {
        LogisticOrder<T> order = new LogisticOrder<>(stack, provider);
        order.setState(SUBMITTED);
        provider.addOrder(order);
        provider.extract(stack);

        removalOrders.add(order);
        return order;
    }

    public ColoredOrder<T> makeRemovalOrder(ColoredProvider<T> provider, EnumDyeColor color, int quantity)
    {
        ColoredOrder<T> order = new ColoredOrder<>(new ColoredStack(color, quantity), provider);
        order.setState(SUBMITTED);

        provider.extract(order.getContent());

        coloredRemovalOrders.add(order);
        return order;
    }

    public List<T> getCompressedStacks()
    {
        if (needContentsRefresh)
        {
            compressedStacks = providers.values().stream().map(Provider::getCompressedContents)
                    .reduce(new ArrayList<>(), functions::accumulateList);
            needContentsRefresh = false;
        }

        return compressedStacks;
    }

    public boolean containsProvider(Provider<T> provider)
    {
        if (provider == null)
            return false;

        return providers.get(provider.getProviderType()).contains(provider);
    }

    public boolean addProvider(Provider<T> provider)
    {
        return providers.put(provider.getProviderType(), provider);
    }

    public boolean removeProvider(Provider<T> provider)
    {
        return providers.remove(provider.getProviderType(), provider);
    }

    public void completeShipment(LogisticShipment<T> shipment)
    {
        shipments.remove(shipment);

        stackOrders.forEach(order ->
        {
            order.getShippedParts().remove(shipment);

            if (order.getShippedParts().isEmpty() && order.getState() == SHIPPING)
            {
                order.setState(COMPLETED);
                order.getSource().removeOrder(order);
            }
        });
    }

    public void completeColoredShipment(ColoredShipment<T> shipment)
    {
      /*  coloredShipments.remove(shipment);

        coloredOrders.forEach(order ->
        {
            order.getShippedParts().remove(shipment);

            if (order.getShippedParts().isEmpty() && order.getState() == OrderState.SHIPPING)
            {
                order.setState(OrderState.COMPLETED);
                order.getDestination().removeOrder(order);
            }
        });*/
    }
}
