package net.vi.woodengears.common.grid.logistic;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.vi.woodengears.common.grid.logistic.node.ColoredProvider;
import net.vi.woodengears.common.grid.logistic.node.Provider;
import net.vi.woodengears.common.grid.logistic.node.Requester;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class LogisticGrid<T>
{
    private List<Provider<T>>  providers;
    private List<Requester<T>> requesters;
    private List<T>            compressedStacks;

    private PriorityQueue<LogisticOrder<T>>            stackOrders;
    private PriorityQueue<LogisticOrder<ColoredStack>> coloredOrders;
    private List<LogisticShipment<T>>                  shipments;
    private List<ColoredShipment<T>>                   coloredShipments;

    @Getter
    @Setter
    private boolean needContentsRefresh;

    @Getter
    private Class<T>                 typeClass;
    private LogisticGridFunctions<T> functions;

    public LogisticGrid(Class<T> typeClass, LogisticGridFunctions<T> functions)
    {
        this.typeClass = typeClass;
        this.functions = functions;

        this.providers = new ArrayList<>();
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

        for (LogisticOrder<T> stackOrder : stackOrders)
        {
            if (stackOrder.getState() != OrderState.SUBMITTED && stackOrder.getState() != OrderState.SHORTAGE)
                continue;

            int toExtract = functions.getQuantity(stackOrder.getOrdered());

            for (Provider<T> provider : this.providers)
            {
                if (provider.isBufferFull())
                    continue;

                int part = provider.containedPart(stackOrder.getOrdered());

                if (part == 0)
                    continue;

                T extracted = provider.extract(functions.changeQuantity(stackOrder.getOrdered(), toExtract));

                stackOrder.getShippedParts().add(createShipment(extracted, provider.getRailPos(),
                        stackOrder.getDestination()));
                toExtract -= functions.getQuantity(extracted);

                if (toExtract == 0)
                    break;
            }

            if (toExtract == 0)
                stackOrder.setState(OrderState.SHIPPING);

            if (toExtract > 0 && stackOrder.getState() == OrderState.SUBMITTED)
                stackOrder.setState(OrderState.SHORTAGE);
        }

        for (LogisticOrder<ColoredStack> coloredOrder : coloredOrders)
        {
            if (coloredOrder.getState() != OrderState.SUBMITTED && coloredOrder.getState() != OrderState.SHORTAGE)
                continue;

            int toExtract = coloredOrder.getOrdered().getQuantity();

            for (Provider<T> provider : this.providers)
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
                            provider.getRailPos(), coloredOrder.getDestination()));
                }
                toExtract -= extracted.stream().mapToInt(functions::getQuantity).sum();

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
        LogisticOrder<T> order = new LogisticOrder<>(stack, requester.getRailPos());
        order.setState(OrderState.SUBMITTED);

        this.stackOrders.add(order);
        return order;
    }

    public LogisticOrder<ColoredStack> makeOrder(Requester<T> requester, EnumDyeColor color, int quantity)
    {
        LogisticOrder<ColoredStack> order = new LogisticOrder<>(new ColoredStack(color, quantity),
                requester.getRailPos());
        order.setState(OrderState.SUBMITTED);

        this.coloredOrders.add(order);
        return order;
    }

    public List<T> getCompressedStacks()
    {
        if (this.needContentsRefresh)
        {
            this.compressedStacks = providers.stream().map(Provider::getCompressedContents)
                    .reduce(new ArrayList<>(), functions::accumulateList);
            this.needContentsRefresh = false;
        }

        return this.compressedStacks;
    }

    public boolean containsProvider(Provider<T> provider)
    {
        return providers.contains(provider);
    }

    public boolean addProvider(Provider<T> provider)
    {
        return providers.add(provider);
    }

    public boolean removeProvider(Provider<T> provider)
    {
        return providers.remove(provider);
    }
}
