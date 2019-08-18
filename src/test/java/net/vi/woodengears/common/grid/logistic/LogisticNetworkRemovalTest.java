package net.vi.woodengears.common.grid.logistic;

import net.minecraft.item.ItemStack;
import net.vi.woodengears.common.test.ItemStackMatcher;
import net.vi.woodengears.common.test.TestItemProvider;
import net.vi.woodengears.common.test.TestItemStorage;
import net.vi.woodengears.common.test.WGTestExt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static net.minecraft.init.Items.APPLE;
import static net.minecraft.item.ItemStack.EMPTY;
import static net.vi.woodengears.common.grid.logistic.OrderState.*;
import static net.vi.woodengears.common.grid.logistic.ProviderType.ACTIVE_PROVIDER;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(WGTestExt.class)
public class LogisticNetworkRemovalTest
{
    @Test
    void processRemovalOrders_givenOneActiveProviderWithOneItem_withOneStorageWithFreeSpace_thenShouldCreateValidShipment()
    {
        LogisticNetwork<ItemStack> grid = new LogisticNetwork<>(null, ItemStack.class, ItemStackMethods.getInstance());

        ItemStack apple2 = new ItemStack(APPLE, 2);

        TestItemProvider activeProvider = TestItemProvider.build()
                .stacks(apple2)
                .type(ACTIVE_PROVIDER)
                .create();

        TestItemStorage storageProvider = TestItemStorage.build()
                .stacks(EMPTY)
                .create();

        grid.addProvider(activeProvider);
        grid.addProvider(storageProvider);

        LogisticOrder<ItemStack> removalOrder = grid.makeRemovalOrder(activeProvider, apple2);

        assertThat(removalOrder.getState()).isEqualTo(SUBMITTED);

        grid.tick();

        assertThat(removalOrder.getState()).isEqualTo(SHIPPING);
        ItemStackMatcher.assertEqualsStrict(removalOrder.getShippedParts().get(0).getContent(), removalOrder.getContent());
        ItemStackMatcher.assertEqualsStrict(removalOrder.getContent(), apple2);
    }

    @Test
    void processRemovalOrders_givenOneActiveProviderWith64Items_withTwoStoragesWithOneHalfFreeSlotEach_thenShouldCreateTwoValidShipments()
    {
        LogisticNetwork<ItemStack> grid = new LogisticNetwork<>(null, ItemStack.class, ItemStackMethods.getInstance());

        ItemStack apple64 = new ItemStack(APPLE, 64);
        ItemStack apple32 = new ItemStack(APPLE, 32);

        TestItemProvider activeProvider = TestItemProvider.build()
                .stacks(apple64)
                .type(ACTIVE_PROVIDER)
                .create();

        TestItemStorage storageProvider = TestItemStorage.build()
                .stacks(apple32.copy())
                .create();
        TestItemStorage storageProvider2 = TestItemStorage.build()
                .stacks(apple32.copy())
                .create();

        grid.addProvider(activeProvider);
        grid.addProvider(storageProvider);
        grid.addProvider(storageProvider2);

        LogisticOrder<ItemStack> removalOrder = grid.makeRemovalOrder(activeProvider, apple64);

        assertThat(removalOrder.getState()).isEqualTo(SUBMITTED);

        grid.tick();

        assertThat(removalOrder.getState()).isEqualTo(SHIPPING);
        ItemStackMatcher.assertEqualsStrict(removalOrder.getShippedParts().get(0).getContent(), apple32);
        ItemStackMatcher.assertEqualsStrict(removalOrder.getShippedParts().get(1).getContent(), apple32);
        ItemStackMatcher.assertEqualsStrict(removalOrder.getContent(), apple64);
    }

    @Test
    void processRemovalOrders_givenOneActiveProviderWith64Items_withOneStorageWithOneHalfFreeSlot_thenShouldCreateShipmentWith32ItemsAndSetShortage()
    {
        LogisticNetwork<ItemStack> grid = new LogisticNetwork<>(null, ItemStack.class, ItemStackMethods.getInstance());

        ItemStack apple64 = new ItemStack(APPLE, 64);
        ItemStack apple32 = new ItemStack(APPLE, 32);

        TestItemProvider activeProvider = TestItemProvider.build()
                .stacks(apple64)
                .type(ACTIVE_PROVIDER)
                .create();

        TestItemStorage storageProvider = TestItemStorage.build()
                .stacks(apple32.copy())
                .create();

        grid.addProvider(activeProvider);
        grid.addProvider(storageProvider);

        LogisticOrder<ItemStack> removalOrder = grid.makeRemovalOrder(activeProvider, apple64);

        assertThat(removalOrder.getState()).isEqualTo(SUBMITTED);

        grid.tick();

        assertThat(removalOrder.getState()).isEqualTo(SHORTAGE);
        ItemStackMatcher.assertEqualsStrict(removalOrder.getShippedParts().get(0).getContent(), apple32);
        ItemStackMatcher.assertEqualsStrict(removalOrder.getContent(), apple64);
    }
}
