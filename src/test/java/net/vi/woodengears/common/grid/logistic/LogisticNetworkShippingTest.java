package net.vi.woodengears.common.grid.logistic;

import net.minecraft.item.ItemStack;
import net.vi.woodengears.common.grid.logistic.node.BaseItemProvider;
import net.vi.woodengears.common.grid.logistic.node.BaseItemRequester;
import net.vi.woodengears.common.test.ItemStackMatcher;
import net.vi.woodengears.common.test.TestItemProvider;
import net.vi.woodengears.common.test.TestItemRequester;
import net.vi.woodengears.common.test.WGTestExt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static net.minecraft.init.Items.APPLE;
import static net.minecraft.init.Items.POTATO;
import static net.minecraft.item.ItemStack.EMPTY;
import static net.vi.woodengears.common.grid.logistic.OrderState.*;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(WGTestExt.class)
public class LogisticNetworkShippingTest
{
    @Test
    void simpleShipping()
    {
        LogisticNetwork<ItemStack> grid = new LogisticNetwork<>(null, ItemStack.class, ItemStackMethods.getInstance());

        ItemStack apple2 = new ItemStack(APPLE, 2);

        BaseItemProvider provider1 = TestItemProvider.build().stacks(apple2).create();
        BaseItemRequester requester = TestItemRequester.build()
                .stacks(EMPTY)
                .create();

        grid.addProvider(provider1);

        LogisticOrder<ItemStack> order = grid.makeOrder(requester, new ItemStack(APPLE));

        assertThat(order.getState()).isEqualTo(SUBMITTED);

        grid.tick();

        assertThat(order.getState()).isEqualTo(SHIPPING);
        ItemStackMatcher.assertEqualsStrict(order.getShippedParts().get(0).getContent(), order.getContent());
        ItemStackMatcher.assertEqualsStrict(order.getContent(), new ItemStack(APPLE));
    }

    @Test
    void multiProviderShipping()
    {
        LogisticNetwork<ItemStack> grid = new LogisticNetwork<>(null, ItemStack.class, ItemStackMethods.getInstance());

        ItemStack apple2 = new ItemStack(APPLE, 2);
        ItemStack apple4 = new ItemStack(APPLE, 4);

        BaseItemProvider provider1 = TestItemProvider.build().stacks(apple2).create();
        BaseItemProvider provider2 = TestItemProvider.build().stacks(apple4).create();

        BaseItemRequester requester = TestItemRequester.build()
                .stacks(EMPTY)
                .create();

        grid.addProvider(provider1);
        grid.addProvider(provider2);

        LogisticOrder<ItemStack> order = grid.makeOrder(requester, new ItemStack(APPLE, 5));

        assertThat(order.getState()).isEqualTo(SUBMITTED);

        grid.tick();

        assertThat(order.getState()).isEqualTo(SHIPPING);

        assertThat(order.getShippedParts()).hasSize(2);
        assertThat(order.getShippedParts().get(0).getContent().getCount()).isEqualTo(2);
        assertThat(order.getShippedParts().get(1).getContent().getCount()).isEqualTo(3);
    }

    @Test
    void shortageShipping()
    {
        LogisticNetwork<ItemStack> grid = new LogisticNetwork<>(null, ItemStack.class, ItemStackMethods.getInstance());

        ItemStack apple2 = new ItemStack(APPLE, 2);
        ItemStack potato2 = new ItemStack(POTATO, 2);
        ItemStack apple4 = new ItemStack(APPLE, 4);

        BaseItemProvider provider1 = TestItemProvider.build().stacks(apple2, potato2).create();
        BaseItemProvider provider2 = TestItemProvider.build().stacks(apple4).create();

        BaseItemRequester requester = TestItemRequester.build()
                .stacks(EMPTY)
                .create();

        grid.addProvider(provider1);
        grid.addProvider(provider2);

        LogisticOrder<ItemStack> order = grid.makeOrder(requester, new ItemStack(POTATO, 3));

        assertThat(order.getState()).isEqualTo(SUBMITTED);

        grid.tick();

        assertThat(order.getState()).isEqualTo(SHORTAGE);
    }

    @Test
    void bufferFullShipping()
    {
        LogisticNetwork<ItemStack> grid = new LogisticNetwork<>(null, ItemStack.class, ItemStackMethods.getInstance());

        ItemStack apple4 = new ItemStack(APPLE, 4);

        BaseItemProvider provider = TestItemProvider.build().stacks(apple4).buffer(1, 1).create();
        grid.addProvider(provider);

        BaseItemRequester requester = TestItemRequester.build()
                .stacks(EMPTY)
                .create();
        LogisticOrder<ItemStack> order = grid.makeOrder(requester, new ItemStack(APPLE));
        grid.tick();

        assertThat(order.getState()).isEqualTo(SHIPPING);

        LogisticOrder<ItemStack> order2 = grid.makeOrder(requester, new ItemStack(APPLE));
        grid.tick();

        assertThat(order2.getState()).isEqualTo(SHORTAGE);
    }
}
