package net.vi.woodengears.common.grid.logistic;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.voxelindustry.steamlayer.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemLogisticGrid
{
    private List<Provider<ItemStack>>  providers;
    private List<Requester<ItemStack>> requesters;
    private List<ItemStack>            compressedStacks;

    private List<LogisticOrder<ItemStack>>    stackOrders;
    private List<LogisticOrder<ColoredStack>> coloredOrders;
    private List<LogisticShipment<ItemStack>> shipments;

    @Getter
    @Setter
    private boolean needContentsRefresh;

    public ItemLogisticGrid()
    {
        this.providers = new ArrayList<>();
        this.compressedStacks = NonNullList.create();

        this.stackOrders = new ArrayList<>();
        this.coloredOrders = new ArrayList<>();
    }

    public void tick()
    {
        if(providers.isEmpty())
            return;
    }

    public LogisticOrder<ItemStack> makeOrder(Requester<ItemStack> requester, ItemStack stack)
    {
        LogisticOrder<ItemStack> order = new LogisticOrder<>(stack, requester.getRailPos());
        order.setState(OrderState.SUBMITTED);

        this.stackOrders.add(order);
        return order;
    }

    public LogisticOrder<ColoredStack> makeOrder(Requester<ItemStack> requester, EnumDyeColor color, int quantity)
    {
        LogisticOrder<ColoredStack> order = new LogisticOrder<>(new ColoredStack(color, quantity),
                requester.getRailPos());
        order.setState(OrderState.SUBMITTED);

        this.coloredOrders.add(order);
        return order;
    }

    public List<ItemStack> getCompressedStacks()
    {
        if (this.needContentsRefresh)
        {
            this.compressedStacks = providers.stream().map(Provider::getCompressedContents)
                    .reduce(new ArrayList<>(), (first, second) ->
                    {
                        List<ItemStack> newList = new ArrayList<>(first);

                        first.forEach(stack -> newList.add(stack.copy()));

                        second.forEach(stack ->
                        {
                            Optional<ItemStack> found =
                                    newList.stream().filter(candidate -> ItemUtils.deepEquals(candidate, stack)).findFirst();
                            if (found.isPresent())
                                found.get().grow(stack.getCount());
                            else
                                newList.add(stack);
                        });

                        return newList;
                    });
            this.needContentsRefresh = false;
        }

        return this.compressedStacks;
    }

    public boolean containsProvider(Provider<ItemStack> provider)
    {
        return providers.contains(provider);
    }

    public boolean addProvider(Provider<ItemStack> provider)
    {
        return providers.add(provider);
    }

    public boolean removeProvider(Provider<ItemStack> provider)
    {
        return providers.remove(provider);
    }
}
