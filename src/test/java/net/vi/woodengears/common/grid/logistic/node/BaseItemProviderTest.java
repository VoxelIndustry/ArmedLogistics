package net.vi.woodengears.common.grid.logistic.node;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.vi.woodengears.common.test.ItemStackMatcher;
import net.vi.woodengears.common.test.TestItemProvider;
import net.vi.woodengears.common.test.WGTestExt;
import net.voxelindustry.steamlayer.utils.ItemUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(WGTestExt.class)
class BaseItemProviderTest
{
    @Test
    void containsCheck()
    {
        TestItemProvider provider = TestItemProvider.build().stacks(new ItemStack(Items.APPLE, 4)).create();

        assertThat(provider.contains(new ItemStack(Items.APPLE, 2))).isTrue();
        assertThat(provider.containedPart(new ItemStack(Items.APPLE, 2))).isEqualTo(2);

        assertThat(provider.contains(new ItemStack(Items.APPLE, 5))).isFalse();
        assertThat(provider.containedPart(new ItemStack(Items.APPLE, 5))).isEqualTo(4);

        assertThat(provider.contains(new ItemStack(Items.POTATO, 2))).isFalse();
        assertThat(provider.containedPart(new ItemStack(Items.POTATO, 2))).isEqualTo(0);
    }

    @Test
    void extractCheck()
    {
        TestItemProvider provider = TestItemProvider.build().stacks(new ItemStack(Items.APPLE, 4)).create();

        ItemStack toExtract = new ItemStack(Items.APPLE);
        ItemStackMatcher.assertEqualsStrict(provider.extract(toExtract), toExtract);
    }

    @Test
    void extractMixedCheck()
    {
        TestItemProvider provider = TestItemProvider.build().stacks(new ItemStack(Items.APPLE),
                new ItemStack(Items.APPLE, 2)).create();

        ItemStack toExtract = new ItemStack(Items.APPLE, 2);
        assertThat(provider.extract(toExtract))
                .matches(extracted -> ItemUtils.deepEqualsWithAmount(extracted, toExtract));
    }

    @Test
    void matchers()
    {
        TestItemProvider provider = TestItemProvider.build().stacks(new ItemStack(Items.APPLE),
                new ItemStack(Items.APPLE), new ItemStack(Items.POTATO)).create();

        assertThat(provider.anyMatch(stack -> stack.getItem() == Items.APPLE)).isTrue();
        assertThat(provider.anyMatch(stack -> stack.getItem() == Items.DIAMOND)).isFalse();

        ItemStackMatcher.assertEqualsStrict(provider.firstMatching(stack -> stack.getItem() == Items.POTATO),
                new ItemStack(Items.POTATO));
        ItemStackMatcher.assertEqualsStrict(provider.firstMatching(stack -> stack.getItem() == Items.DIAMOND),
                ItemStack.EMPTY);

        assertThat(provider.allMatching(stack -> stack.getItem() == Items.APPLE)).hasSize(1);
        assertThat(provider.allMatching(stack -> true)).hasSize(2);
        assertThat(provider.allMatching(stack -> stack.getItem() == Items.DIAMOND)).isEmpty();
    }

    @Test
    void extractThenBuffer()
    {
        TestItemProvider provider = TestItemProvider.build().stacks(new ItemStack(Items.APPLE),
                new ItemStack(Items.APPLE), new ItemStack(Items.POTATO)).buffer(2, 128).create();

        provider.extract(new ItemStack(Items.APPLE));

        ItemStackMatcher.assertEqualsStrict(provider.fromBuffer(new ItemStack(Items.APPLE)),
                new ItemStack(Items.APPLE));
    }
}
