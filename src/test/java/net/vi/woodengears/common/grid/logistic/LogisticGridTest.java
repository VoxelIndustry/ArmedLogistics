package net.vi.woodengears.common.grid.logistic;

import net.minecraft.init.Items;
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

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(WGTestExt.class)
class LogisticGridTest
{
    @Test
    void compressedStacks()
    {
        LogisticGrid<ItemStack> grid = new LogisticGrid<>(ItemStack.class, ItemStackMethods.getInstance());

        ItemStack apple2 = new ItemStack(Items.APPLE, 2);
        ItemStack potato1 = new ItemStack(Items.POTATO, 1);
        ItemStack apple4 = new ItemStack(Items.APPLE, 4);

        BaseItemProvider provider1 = TestItemProvider.build().stacks(apple2, potato1).create();
        BaseItemProvider provider2 = TestItemProvider.build().stacks(apple4, potato1).create();

        grid.addProvider(provider1);
        grid.addProvider(provider2);
        grid.setNeedContentsRefresh(true);

        ItemStackMatcher.assertContainsOnly(grid.getCompressedStacks(),
                new ItemStack(Items.APPLE, 6), new ItemStack(Items.POTATO, 2));
    }

    @Test
    void simpleShipping()
    {
        LogisticGrid<ItemStack> grid = new LogisticGrid<>(ItemStack.class, ItemStackMethods.getInstance());

        ItemStack apple2 = new ItemStack(Items.APPLE, 2);

        BaseItemProvider provider1 = TestItemProvider.build().stacks(apple2).create();
        BaseItemRequester requester = new TestItemRequester();

        grid.addProvider(provider1);

        LogisticOrder<ItemStack> order = grid.makeOrder(requester, new ItemStack(Items.APPLE, 1));

        assertThat(order.getState()).isEqualTo(OrderState.SUBMITTED);

        grid.tick();

        assertThat(order.getState()).isEqualTo(OrderState.SHIPPING);
        ItemStackMatcher.assertEqualsStrict(order.getShippedParts().get(0).getContent(), order.getOrdered());
    }

    @Test
    void multiProviderShipping()
    {
        LogisticGrid<ItemStack> grid = new LogisticGrid<>(ItemStack.class, ItemStackMethods.getInstance());

        ItemStack apple2 = new ItemStack(Items.APPLE, 2);
        ItemStack apple4 = new ItemStack(Items.APPLE, 4);

        BaseItemProvider provider1 = TestItemProvider.build().stacks(apple2).create();
        BaseItemProvider provider2 = TestItemProvider.build().stacks(apple4).create();

        BaseItemRequester requester = new TestItemRequester();

        grid.addProvider(provider1);
        grid.addProvider(provider2);

        LogisticOrder<ItemStack> order = grid.makeOrder(requester, new ItemStack(Items.APPLE, 5));

        assertThat(order.getState()).isEqualTo(OrderState.SUBMITTED);

        grid.tick();

        assertThat(order.getState()).isEqualTo(OrderState.SHIPPING);

        assertThat(order.getShippedParts()).hasSize(2);
        assertThat(order.getShippedParts().get(0).getContent().getCount()).isEqualTo(2);
        assertThat(order.getShippedParts().get(1).getContent().getCount()).isEqualTo(3);
    }

    @Test
    void shortageShipping()
    {
        LogisticGrid<ItemStack> grid = new LogisticGrid<>(ItemStack.class, ItemStackMethods.getInstance());

        ItemStack apple2 = new ItemStack(Items.APPLE, 2);
        ItemStack potato1 = new ItemStack(Items.POTATO, 1);
        ItemStack apple4 = new ItemStack(Items.APPLE, 4);

        BaseItemProvider provider1 = TestItemProvider.build().stacks(apple2, potato1).create();
        BaseItemProvider provider2 = TestItemProvider.build().stacks(apple4, potato1.copy()).create();

        BaseItemRequester requester = new TestItemRequester();

        grid.addProvider(provider1);
        grid.addProvider(provider2);

        LogisticOrder<ItemStack> order = grid.makeOrder(requester, new ItemStack(Items.POTATO, 3));

        assertThat(order.getState()).isEqualTo(OrderState.SUBMITTED);

        grid.tick();

        assertThat(order.getState()).isEqualTo(OrderState.SHORTAGE);
    }
}
