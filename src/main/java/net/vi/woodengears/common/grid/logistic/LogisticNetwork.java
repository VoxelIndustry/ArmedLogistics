package net.vi.woodengears.common.grid.logistic;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.vi.woodengears.common.grid.RailGrid;
import net.vi.woodengears.common.grid.logistic.node.ColoredProvider;
import net.vi.woodengears.common.grid.logistic.node.Provider;
import net.vi.woodengears.common.grid.logistic.node.Requester;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class LogisticNetwork<T>
{
    private ListMultimap<ProviderType, Provider<T>> providers;
    private List<Requester<T>>                      requesters;
    private List<T>                                 compressedStacks;

    private PriorityQueue<LogisticOrder<T>> stackOrders;
    private PriorityQueue<ColoredOrder<T>>  coloredOrders;
    @Getter
    private List<LogisticShipment<T>>       shipments;
    @Getter
    private List<ColoredShipment<T>>        coloredShipments;

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

        this.providers = MultimapBuilder.enumKeys(ProviderType.class).arrayListValues().build();
        this.requesters = new ArrayList<>();

        this.compressedStacks = NonNullList.create();

        this.stackOrders = new PriorityQueue<>(Comparator.comparingInt(order -> order.getState().ordinal()));
        this.coloredOrders = new PriorityQueue<>(Comparator.comparingInt(order -> order.getState().ordinal()));

        this.shipments = new ArrayList<>();
        this.coloredShipments = new ArrayList<>();
    }

    public void tick()
    {
        if (providers.isEmpty())
            return;

        this.providers.values().forEach(Provider::networkTick);
        
        this.processOrders();
        this.processColoredOrders();
    }

    private List<Provider<T>> getSortedProviders(ProviderType type, BlockPos destination)
    {
        if (this.getGrid() == null)
            return this.providers.get(type);
        else
            return this.providers.get(type).stream()
                    .filter(provider -> !provider.isBufferFull())
                    .sorted(Comparator.comparingInt(provider -> grid.getDistanceBetween(provider.getRailPos(),
                            destination))).collect(Collectors.toList());
    }

    private void processOrders()
    {
        stackOrders.removeIf(order -> order.getState() == OrderState.COMPLETED);

        for (LogisticOrder<T> stackOrder : stackOrders)
        {
            if (stackOrder.getState() != OrderState.SUBMITTED && stackOrder.getState() != OrderState.SHORTAGE)
                continue;

            int toExtract = functions.getQuantity(stackOrder.getOrdered());

            for (ProviderType type : ProviderType.VALUES)
            {
                for (Provider<T> provider : getSortedProviders(type, stackOrder.getDestination().getRailPos()))
                {
                    if (provider.isBufferFull())
                        continue;

                    int part = provider.containedPart(stackOrder.getOrdered());

                    if (part == 0)
                        continue;

                    T extracted = provider.extract(functions.changeQuantity(stackOrder.getOrdered(), toExtract));

                    stackOrder.getShippedParts().add(createShipment(extracted, provider.getRailPos(),
                            stackOrder.getDestination().getRailPos()));
                    toExtract -= functions.getQuantity(extracted);

                    if (toExtract == 0)
                        break;
                }
                if (toExtract == 0)
                    break;
            }

            if (toExtract == 0)
                stackOrder.setState(OrderState.SHIPPING);

            if (toExtract > 0 && stackOrder.getState() == OrderState.SUBMITTED)
                stackOrder.setState(OrderState.SHORTAGE);
        }
    }

    private void processColoredOrders()
    {
        coloredOrders.removeIf(order -> order.getState() == OrderState.COMPLETED);

        for (ColoredOrder<T> coloredOrder : coloredOrders)
        {
            if (coloredOrder.getState() != OrderState.SUBMITTED && coloredOrder.getState() != OrderState.SHORTAGE)
                continue;

            int toExtract = coloredOrder.getOrdered().getQuantity();

            for (ProviderType type : ProviderType.VALUES)
            {
                for (Provider<T> provider : getSortedProviders(type, coloredOrder.getDestination().getRailPos()))
                {
                    if (!provider.isColored() || provider.isBufferFull())
                        continue;

                    ColoredProvider<T> coloredProvider = (ColoredProvider<T>) provider;
                    int part = coloredProvider.containedPart(coloredOrder.getOrdered());

                    if (part == 0)
                        continue;

                    ColoredStack stack = new ColoredStack(coloredOrder.getOrdered().getColor(), toExtract);
                    List<T> extracted = coloredProvider.extract(stack);

                    for (T value : extracted)
                    {
                        coloredOrder.getShippedParts().add(createColoredShipment(value, coloredOrder.getOrdered(),
                                provider.getRailPos(), coloredOrder.getDestination().getRailPos()));
                    }
                    toExtract -= extracted.stream().mapToInt(functions::getQuantity).sum();

                    if (toExtract == 0)
                        break;
                }
                if (toExtract == 0)
                    break;
            }

            if (toExtract == 0)
                coloredOrder.setState(OrderState.SHIPPING);

            if (toExtract > 0 && coloredOrder.getState() == OrderState.SUBMITTED)
                coloredOrder.setState(OrderState.SHORTAGE);
        }
    }

    private LogisticShipment<T> createShipment(T toShip, BlockPos from, BlockPos to)
    {
        LogisticShipment<T> shipment = new LogisticShipment<>(from, to, toShip);

        this.shipments.add(shipment);
        return shipment;
    }

    private ColoredShipment<T> createColoredShipment(T toShip, ColoredStack coloredStack, BlockPos from, BlockPos to)
    {
        ColoredShipment<T> shipment = new ColoredShipment<>(from, to, coloredStack, toShip);

        this.coloredShipments.add(shipment);
        return shipment;
    }

    public LogisticOrder<T> makeOrder(Requester<T> requester, T stack)
    {
        LogisticOrder<T> order = new LogisticOrder<>(stack, requester);
        order.setState(OrderState.SUBMITTED);
        order.getDestination().addOrder(order);

        this.stackOrders.add(order);
        return order;
    }

    public ColoredOrder<T> makeOrder(Requester<T> requester, EnumDyeColor color, int quantity)
    {
        ColoredOrder<T> order = new ColoredOrder<>(new ColoredStack(color, quantity),
                requester);
        order.setState(OrderState.SUBMITTED);
        // TODO : Colored requesters
        //order.getDestination().addOrder(order);

        this.coloredOrders.add(order);
        return order;
    }

    public List<T> getCompressedStacks()
    {
        if (this.needContentsRefresh)
        {
            this.compressedStacks = providers.values().stream().map(Provider::getCompressedContents)
                    .reduce(new ArrayList<>(), functions::accumulateList);
            this.needContentsRefresh = false;
        }

        return this.compressedStacks;
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
        this.shipments.remove(shipment);

        this.stackOrders.forEach(order ->
        {
            order.getShippedParts().remove(shipment);

            if (order.getShippedParts().isEmpty() && order.getState() == OrderState.SHIPPING)
            {
                order.setState(OrderState.COMPLETED);
                order.getDestination().removeOrder(order);
            }
        });
    }
}
