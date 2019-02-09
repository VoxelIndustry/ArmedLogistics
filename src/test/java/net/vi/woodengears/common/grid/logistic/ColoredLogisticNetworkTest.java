package net.vi.woodengears.common.grid.logistic;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.vi.woodengears.common.grid.logistic.node.BaseItemRequester;
import net.vi.woodengears.common.grid.logistic.node.ColoredItemProvider;
import net.vi.woodengears.common.test.ItemStackMatcher;
import net.vi.woodengears.common.test.TestColoredItemProvider;
import net.vi.woodengears.common.test.TestItemRequester;
import net.vi.woodengears.common.test.WGTestExt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(WGTestExt.class)
class ColoredLogisticNetworkTest
{
    @Test
    void simpleShipping()
    {
        LogisticNetwork<ItemStack> grid = new LogisticNetwork<>(null, ItemStack.class, ItemStackMethods.getInstance());

        ItemStack apple2 = new ItemStack(Items.APPLE, 2);

        ColoredItemProvider provider1 = TestColoredItemProvider.build().stacks(apple2)
                .color(EnumDyeColor.RED, new ItemStack(Items.APPLE)).create();
        BaseItemRequester requester = new TestItemRequester();

        grid.addProvider(provider1);

        LogisticOrder<ColoredStack> order = grid.makeOrder(requester, EnumDyeColor.RED, 1);

        assertThat(order.getState()).isEqualTo(OrderState.SUBMITTED);

        grid.tick();

        assertThat(order.getState()).isEqualTo(OrderState.SHIPPING);
        assertThat(order.getShippedParts().get(0)).isInstanceOf(ColoredShipment.class);
        ItemStackMatcher.assertEqualsStrict(((ColoredShipment<ItemStack>) order.getShippedParts().get(0)).getRawContent(),
                new ItemStack(Items.APPLE, 1));
    }

    @Test
    void mixedShipping()
    {
        LogisticNetwork<ItemStack> grid = new LogisticNetwork<>(null, ItemStack.class, ItemStackMethods.getInstance());

        ItemStack apple2 = new ItemStack(Items.APPLE, 2);
        ItemStack potato3 = new ItemStack(Items.POTATO, 3);

        ColoredItemProvider provider1 = TestColoredItemProvider.build().stacks(apple2, potato3)
                .color(EnumDyeColor.RED, new ItemStack(Items.APPLE), new ItemStack(Items.POTATO)).create();
        BaseItemRequester requester = new TestItemRequester();

        grid.addProvider(provider1);

        LogisticOrder<ColoredStack> order = grid.makeOrder(requester, EnumDyeColor.RED, 4);

        assertThat(order.getState()).isEqualTo(OrderState.SUBMITTED);

        grid.tick();

        assertThat(order.getState()).isEqualTo(OrderState.SHIPPING);

        assertThat(order.getShippedParts()).hasSize(2);
        assertThat(order.getShippedParts().get(0)).isInstanceOf(ColoredShipment.class);
        assertThat(order.getShippedParts().get(1)).isInstanceOf(ColoredShipment.class);

        ItemStackMatcher.assertEqualsStrict(((ColoredShipment<ItemStack>) order.getShippedParts().get(0)).getRawContent(),
                new ItemStack(Items.APPLE, 2));
        ItemStackMatcher.assertEqualsStrict(((ColoredShipment<ItemStack>) order.getShippedParts().get(1)).getRawContent(),
                new ItemStack(Items.POTATO, 2));
    }

    @Test
    void mixedShippingMultiProvider()
    {
        LogisticNetwork<ItemStack> grid = new LogisticNetwork<>(null, ItemStack.class, ItemStackMethods.getInstance());

        ItemStack apple2 = new ItemStack(Items.APPLE, 2);
        ItemStack potato3 = new ItemStack(Items.POTATO, 3);

        ColoredItemProvider provider1 = TestColoredItemProvider.build().stacks(apple2, potato3)
                .color(EnumDyeColor.RED, new ItemStack(Items.APPLE), new ItemStack(Items.POTATO)).create();
        ColoredItemProvider provider2 = TestColoredItemProvider.build().stacks(apple2.copy())
                .color(EnumDyeColor.RED, new ItemStack(Items.APPLE)).create();

        BaseItemRequester requester = new TestItemRequester();

        grid.addProvider(provider1);
        grid.addProvider(provider2);

        LogisticOrder<ColoredStack> order = grid.makeOrder(requester, EnumDyeColor.RED, 6);

        assertThat(order.getState()).isEqualTo(OrderState.SUBMITTED);

        grid.tick();

        assertThat(order.getState()).isEqualTo(OrderState.SHIPPING);

        assertThat(order.getShippedParts()).hasSize(3);
        assertThat(order.getShippedParts().get(0)).isInstanceOf(ColoredShipment.class);
        assertThat(order.getShippedParts().get(1)).isInstanceOf(ColoredShipment.class);
        assertThat(order.getShippedParts().get(2)).isInstanceOf(ColoredShipment.class);

        ItemStackMatcher.assertEqualsStrict(((ColoredShipment<ItemStack>) order.getShippedParts().get(0)).getRawContent(),
                new ItemStack(Items.APPLE, 2));
        ItemStackMatcher.assertEqualsStrict(((ColoredShipment<ItemStack>) order.getShippedParts().get(1)).getRawContent(),
                new ItemStack(Items.POTATO, 3));
        ItemStackMatcher.assertEqualsStrict(((ColoredShipment<ItemStack>) order.getShippedParts().get(2)).getRawContent(),
                new ItemStack(Items.APPLE, 1));
    }
}
