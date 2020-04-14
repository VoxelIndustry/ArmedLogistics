package net.voxelindustry.armedlogistics.common.grid.logistic;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.voxelindustry.armedlogistics.common.grid.logistic.node.BaseItemProvider;
import net.voxelindustry.armedlogistics.common.test.ItemStackMatcher;
import net.voxelindustry.armedlogistics.common.test.TestItemProvider;
import net.voxelindustry.armedlogistics.common.test.WGTestExt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(WGTestExt.class)
class LogisticNetworkTest
{
    @Test
    void containsProvider()
    {
        LogisticNetwork<ItemStack> grid = new LogisticNetwork<>(null, ItemStack.class, ItemStackMethods.getInstance());
        BaseItemProvider provider = TestItemProvider.build().create();

        grid.addProvider(provider);

        assertThat(grid.containsProvider(provider)).isTrue();

        grid.removeProvider(provider);

        assertThat(grid.containsProvider(provider)).isFalse();
    }

    @Test
    void compressedStacks()
    {
        LogisticNetwork<ItemStack> grid = new LogisticNetwork<>(null, ItemStack.class, ItemStackMethods.getInstance());

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


}
