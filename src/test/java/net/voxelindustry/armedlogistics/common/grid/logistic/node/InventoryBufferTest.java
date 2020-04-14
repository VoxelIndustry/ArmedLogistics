package net.voxelindustry.armedlogistics.common.grid.logistic.node;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.voxelindustry.armedlogistics.common.test.ItemStackMatcher;
import net.voxelindustry.armedlogistics.common.test.WGTestExt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;

import static net.minecraft.item.Items.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(WGTestExt.class)
class InventoryBufferTest
{
    @Test
    void addItemsFullCount()
    {
        InventoryBuffer buffer = new InventoryBuffer(2, 128);

        ItemStack apple = new ItemStack(APPLE);

        ItemStackMatcher.assertEqualsStrict(buffer.add(apple), apple);

        ItemStackMatcher.assertEqualsStrict(buffer.add(new ItemStack(APPLE, 128)), new ItemStack(APPLE,
                127));

        assertThat(buffer.isFull()).isTrue();

        ItemStackMatcher.assertEqualsStrict(buffer.add(new ItemStack(APPLE, 128)), ItemStack.EMPTY);
    }

    @Test
    void addItemsFullType()
    {
        InventoryBuffer buffer = new InventoryBuffer(2, 128);

        ItemStack apple = new ItemStack(APPLE);
        ItemStack potato = new ItemStack(POTATO);
        ItemStack diamond = new ItemStack(DIAMOND);

        buffer.add(apple);
        buffer.add(potato);

        ItemStackMatcher.assertEqualsStrict(buffer.add(diamond), ItemStack.EMPTY);
        ItemStackMatcher.assertEqualsStrict(buffer.add(apple), apple);
    }

    @Test
    void addAllFull()
    {
        InventoryBuffer buffer = new InventoryBuffer(2, 128);

        ItemStack apple = new ItemStack(APPLE);
        ItemStack apple128 = new ItemStack(APPLE, 128);

        ItemStackMatcher.assertContainsOnly(buffer.addAll(Arrays.asList(apple, apple128)), apple,
                new ItemStack(APPLE, 127));

        assertThat(buffer.isFull()).isTrue();
    }

    @Test
    void remove()
    {
        InventoryBuffer buffer = new InventoryBuffer(2, 128);

        ItemStack apple = new ItemStack(APPLE);

        buffer.add(apple);

        ItemStackMatcher.assertEqualsStrict(buffer.getStacks().get(0), apple);

        buffer.remove(apple);

        ItemStackMatcher.assertEqualsStrict(buffer.getStacks().get(0), ItemStack.EMPTY);

        assertThrows(RuntimeException.class, () -> buffer.remove(apple));
    }

    @Test
    void partialRemove()
    {
        InventoryBuffer buffer = new InventoryBuffer(2, 128);

        ItemStack apple64 = new ItemStack(APPLE,64);

        buffer.add(apple64);

        buffer.remove(new ItemStack(APPLE));

        ItemStackMatcher.assertEqualsStrict(buffer.getStacks().get(0), new ItemStack(APPLE, 63));
    }

    @Test
    void removeAll()
    {
        InventoryBuffer buffer = new InventoryBuffer(2, 128);

        ItemStack apple = new ItemStack(APPLE);
        ItemStack potato = new ItemStack(POTATO);

        buffer.add(apple);
        buffer.add(potato);

        buffer.removeAll(Arrays.asList(apple, potato));

        ItemStackMatcher.assertEqualsStrict(buffer.getStacks().get(0), ItemStack.EMPTY);
        ItemStackMatcher.assertEqualsStrict(buffer.getStacks().get(1), ItemStack.EMPTY);
    }

    @Test
    void nbtCheck()
    {
        InventoryBuffer buffer = new InventoryBuffer(2, 128);

        ItemStack apple = new ItemStack(APPLE, 127);
        ItemStack potato = new ItemStack(POTATO);

        buffer.add(apple);
        buffer.add(potato);

        CompoundNBT tag = buffer.writeNBT(new CompoundNBT());

        InventoryBuffer newBuffer = new InventoryBuffer(2,128);
        newBuffer.readNBT(tag);

        assertThat(newBuffer.isFull()).isEqualTo(buffer.isFull()).isTrue();
        assertThat(newBuffer.getCurrentCount()).isEqualTo(buffer.getCurrentCount()).isEqualTo(128);

        ItemStackMatcher.assertEqualsStrict(buffer.getStacks().get(0), newBuffer.getStacks().get(0));
        ItemStackMatcher.assertEqualsStrict(buffer.getStacks().get(0), apple);

        ItemStackMatcher.assertEqualsStrict(buffer.getStacks().get(1), newBuffer.getStacks().get(1));
        ItemStackMatcher.assertEqualsStrict(buffer.getStacks().get(1), potato);
    }
}
