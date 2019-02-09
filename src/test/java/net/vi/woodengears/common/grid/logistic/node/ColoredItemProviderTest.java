package net.vi.woodengears.common.grid.logistic.node;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.vi.woodengears.common.grid.logistic.ColoredStack;
import net.vi.woodengears.common.test.TestColoredItemProvider;
import net.vi.woodengears.common.test.WGTestExt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(WGTestExt.class)
class ColoredItemProviderTest
{
    @Test
    void containsCheck()
    {
        ItemStack apple4 = new ItemStack(Items.APPLE, 4);
        ItemStack potato2 = new ItemStack(Items.POTATO, 2);

        TestColoredItemProvider provider = TestColoredItemProvider.build()
                .stacks(apple4, potato2)
                .color(EnumDyeColor.RED, apple4, potato2)
                .color(EnumDyeColor.YELLOW, potato2).create();

        assertThat(provider.contains(new ColoredStack(EnumDyeColor.BLACK, 1))).isFalse();

        assertThat(provider.contains(new ColoredStack(EnumDyeColor.RED, 1))).isTrue();
        assertThat(provider.contains(new ColoredStack(EnumDyeColor.YELLOW, 1))).isTrue();

        assertThat(provider.contains(new ColoredStack(EnumDyeColor.RED, 5))).isTrue();
        assertThat(provider.contains(new ColoredStack(EnumDyeColor.RED, 7))).isFalse();
    }
}
