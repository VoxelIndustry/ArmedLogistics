package net.voxelindustry.armedlogistics.common.grid.logistic.node;

import net.minecraft.item.ItemStack;
import net.voxelindustry.armedlogistics.common.test.ItemStackMatcher;
import net.voxelindustry.armedlogistics.common.test.TestItemStorage;
import net.voxelindustry.armedlogistics.common.test.WGTestExt;
import net.voxelindustry.steamlayer.inventory.InventoryHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import static net.minecraft.item.ItemStack.EMPTY;
import static net.minecraft.item.Items.APPLE;
import static net.minecraft.item.Items.COAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(WGTestExt.class)
public class BaseItemStorageTest
{
    @Test
    void insertablePart_givenHalfStackWithSufficientSpace_thenShouldReturnGivenStackCount()
    {
        TestItemStorage storage = TestItemStorage.build()
                .stacks(EMPTY)
                .create();

        ItemStack apple32 = new ItemStack(APPLE, 32);
        assertThat(storage.insertablePart(apple32)).isEqualTo(apple32.getCount());
    }

    @Test
    void insertablePart_givenStackWithNotEnoughSpace_thenShouldReturnOnlyInsertableCount()
    {
        TestItemStorage storage = TestItemStorage.build()
                .stacks(new ItemStack(APPLE, 32), new ItemStack(COAL))
                .create();

        ItemStack apple64 = new ItemStack(APPLE, 64);
        assertThat(storage.insertablePart(apple64)).isEqualTo(32);
    }

    @Test
    void insert_givenStackWithEmptyStorage_thenShouldStoreFullStackAtFirstSlot()
    {
        InventoryHandler inventory = spy(new InventoryHandler(1));
        TestItemStorage storage = TestItemStorage.build()
                .inventory(inventory)
                .create();

        ItemStack apple32 = new ItemStack(APPLE, 32);
        ItemStack insert = storage.insert(apple32);

        ItemStackMatcher.assertEqualsStrict(insert, apple32);
        ItemStackMatcher.assertEqualsStrict(storage.getHandler().getStackInSlot(0), apple32);

        ArgumentCaptor<ItemStack> captor = ArgumentCaptor.forClass(ItemStack.class);
        verify(inventory).insertItem(eq(0), captor.capture(), eq(false));
        ItemStackMatcher.assertEqualsStrict(captor.getValue(), apple32);
    }

    @Test
    void insert_givenStackWithHalfFullStorage_withEnoughSpace_thenShouldStoreFullStackInTwoSlots()
    {
        InventoryHandler inventory = spy(new InventoryHandler(2));
        inventory.setStackInSlot(0, new ItemStack(APPLE, 32));

        TestItemStorage storage = TestItemStorage.build()
                .inventory(inventory)
                .create();

        ItemStack apple64 = new ItemStack(APPLE, 64);
        ItemStack apple32 = new ItemStack(APPLE, 32);
        ItemStack insert = storage.insert(apple64);

        ItemStackMatcher.assertEqualsStrict(insert, apple64);
        ItemStackMatcher.assertEqualsStrict(storage.getHandler().getStackInSlot(0), apple64);
        ItemStackMatcher.assertEqualsStrict(storage.getHandler().getStackInSlot(1), new ItemStack(APPLE, 32));

        ArgumentCaptor<ItemStack> captor = ArgumentCaptor.forClass(ItemStack.class);
        verify(inventory).insertItem(eq(0), captor.capture(), eq(false));
        verify(inventory).insertItem(eq(1), captor.capture(), eq(false));
        ItemStackMatcher.assertEqualsStrict(captor.getAllValues().get(0), apple32);
        ItemStackMatcher.assertEqualsStrict(captor.getAllValues().get(1), apple32);
    }
}
