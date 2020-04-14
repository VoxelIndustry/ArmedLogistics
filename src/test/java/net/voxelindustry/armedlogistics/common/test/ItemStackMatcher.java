package net.voxelindustry.armedlogistics.common.test;

import net.minecraft.item.ItemStack;
import net.voxelindustry.steamlayer.common.utils.ItemUtils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemStackMatcher
{
    private static Predicate<? super List<? extends ItemStack>> containsOnly(ItemStack... stacks)
    {
        return list ->
                Stream.of(stacks).allMatch(stack ->
                        list.stream().anyMatch(contained -> ItemUtils.deepEqualsWithAmount(stack, contained)))
                        && list.size() == stacks.length;
    }

    public static void assertContainsOnly(List<ItemStack> stacks, ItemStack... containeds)
    {
        StringBuilder msg = new StringBuilder("must contains only");

        for (ItemStack stack : containeds)
            msg.append(" ").append(stack.toString());

        assertThat(stacks).matches(containsOnly(containeds), msg.toString());
    }

    public static void assertEquals(ItemStack value, ItemStack expected)
    {
        assertThat(value).matches(stack -> ItemUtils.deepEquals(value, expected),
                "must be equals to " + expected.toString());
    }

    public static void assertEqualsStrict(ItemStack value, ItemStack expected)
    {
        assertThat(value).matches(stack -> ItemUtils.deepEqualsWithAmount(value, expected),
                "must be equals with count to " + expected.toString());
    }
}
