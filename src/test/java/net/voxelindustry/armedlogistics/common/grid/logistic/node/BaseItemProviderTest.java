package net.voxelindustry.armedlogistics.common.grid.logistic.node;

import net.minecraft.item.ItemStack;
import net.voxelindustry.armedlogistics.common.test.ItemStackMatcher;
import net.voxelindustry.armedlogistics.common.test.TestItemProvider;
import net.voxelindustry.armedlogistics.common.test.WGTestExt;
import net.voxelindustry.steamlayer.common.utils.ItemUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static net.minecraft.item.Items.*;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(WGTestExt.class)
class BaseItemProviderTest
{
    @Test
    void containsCheck()
    {
        TestItemProvider provider = TestItemProvider.build().stacks(new ItemStack(APPLE, 4)).create();

        assertThat(provider.contains(new ItemStack(APPLE, 2))).isTrue();
        assertThat(provider.containedPart(new ItemStack(APPLE, 2))).isEqualTo(2);

        assertThat(provider.contains(new ItemStack(APPLE, 5))).isFalse();
        assertThat(provider.containedPart(new ItemStack(APPLE, 5))).isEqualTo(4);

        assertThat(provider.contains(new ItemStack(POTATO, 2))).isFalse();
        assertThat(provider.containedPart(new ItemStack(POTATO, 2))).isEqualTo(0);
    }

    @Test
    void extractCheck()
    {
        TestItemProvider provider = TestItemProvider.build().stacks(new ItemStack(APPLE, 4)).create();

        ItemStack toExtract = new ItemStack(APPLE);
        ItemStackMatcher.assertEqualsStrict(provider.extract(toExtract), toExtract);
    }

    @Test
    void extractMixedCheck()
    {
        TestItemProvider provider = TestItemProvider.build().stacks(new ItemStack(APPLE),
                new ItemStack(APPLE, 2)).create();

        ItemStack toExtract = new ItemStack(APPLE, 2);
        assertThat(provider.extract(toExtract))
                .matches(extracted -> ItemUtils.deepEqualsWithAmount(extracted, toExtract));
    }

    @Test
    void matchers()
    {
        TestItemProvider provider = TestItemProvider.build().stacks(new ItemStack(APPLE),
                new ItemStack(APPLE), new ItemStack(POTATO)).create();

        assertThat(provider.anyMatch(stack -> stack.getItem() == APPLE)).isTrue();
        assertThat(provider.anyMatch(stack -> stack.getItem() == DIAMOND)).isFalse();

        ItemStackMatcher.assertEqualsStrict(provider.firstMatching(stack -> stack.getItem() == POTATO),
                new ItemStack(POTATO));
        ItemStackMatcher.assertEqualsStrict(provider.firstMatching(stack -> stack.getItem() == DIAMOND),
                ItemStack.EMPTY);

        assertThat(provider.allMatching(stack -> stack.getItem() == APPLE)).hasSize(1);
        assertThat(provider.allMatching(stack -> true)).hasSize(2);
        assertThat(provider.allMatching(stack -> stack.getItem() == DIAMOND)).isEmpty();
    }

    @Test
    void extractThenBuffer()
    {
        TestItemProvider provider = TestItemProvider.build().stacks(new ItemStack(APPLE),
                new ItemStack(APPLE), new ItemStack(POTATO)).buffer(2, 128).create();

        provider.extract(new ItemStack(APPLE));

        ItemStackMatcher.assertEqualsStrict(provider.fromBuffer(new ItemStack(APPLE)),
                new ItemStack(APPLE));
    }
}
