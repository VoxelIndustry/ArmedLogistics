package net.voxelindustry.armedlogistics.common.grid.logistic.node;

import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.voxelindustry.armedlogistics.common.grid.logistic.ColoredStack;
import net.voxelindustry.armedlogistics.common.test.TestColoredItemProvider;
import net.voxelindustry.armedlogistics.common.test.WGTestExt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static net.minecraft.item.Items.APPLE;
import static net.minecraft.item.Items.POTATO;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(WGTestExt.class)
class ColoredItemProviderTest
{
    @Test
    void containsCheck()
    {
        ItemStack apple4 = new ItemStack(APPLE, 4);
        ItemStack potato2 = new ItemStack(POTATO, 2);

        TestColoredItemProvider provider = TestColoredItemProvider.build()
                .stacks(apple4, potato2)
                .color(DyeColor.RED, apple4, potato2)
                .color(DyeColor.YELLOW, potato2).create();

        assertThat(provider.contains(new ColoredStack(DyeColor.BLACK, 1))).isFalse();

        assertThat(provider.contains(new ColoredStack(DyeColor.RED, 1))).isTrue();
        assertThat(provider.contains(new ColoredStack(DyeColor.YELLOW, 1))).isTrue();

        assertThat(provider.contains(new ColoredStack(DyeColor.RED, 5))).isTrue();
        assertThat(provider.contains(new ColoredStack(DyeColor.RED, 7))).isFalse();
    }
}
