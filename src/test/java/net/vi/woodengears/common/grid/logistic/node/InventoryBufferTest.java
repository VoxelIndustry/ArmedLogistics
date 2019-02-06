package net.vi.woodengears.common.grid.logistic.node;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.vi.woodengears.common.test.ItemStackMatcher;
import net.vi.woodengears.common.test.WGTestExt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(WGTestExt.class)
public class InventoryBufferTest
{
    @Test
    void addItemsFullCount()
    {
        InventoryBuffer buffer = new InventoryBuffer(2, 128);

        ItemStack apple = new ItemStack(Items.APPLE);

        ItemStackMatcher.assertEqualsStrict(buffer.add(apple), apple);

        ItemStackMatcher.assertEqualsStrict(buffer.add(new ItemStack(Items.APPLE, 128)), new ItemStack(Items.APPLE,
                127));

        assertThat(buffer.isFull()).isTrue();
    }

    @Test
    void addItemsFullType()
    {
        InventoryBuffer buffer = new InventoryBuffer(2, 128);

        ItemStack apple = new ItemStack(Items.APPLE);
        ItemStack potato = new ItemStack(Items.POTATO);
        ItemStack diamond = new ItemStack(Items.DIAMOND);

        buffer.add(apple);
        buffer.add(potato);

        ItemStackMatcher.assertEqualsStrict(buffer.add(diamond), ItemStack.EMPTY);
        ItemStackMatcher.assertEqualsStrict(buffer.add(apple), apple);
    }

    @Test
    void addAllFull()
    {
        InventoryBuffer buffer = new InventoryBuffer(2, 128);

        ItemStack apple = new ItemStack(Items.APPLE);
        ItemStack apple128 = new ItemStack(Items.APPLE,128);

        ItemStackMatcher.assertContainsOnly(buffer.addAll(Arrays.asList(apple, apple128)), apple, new ItemStack(Items.APPLE, 127));

        assertThat(buffer.isFull()).isTrue();
    }
}
