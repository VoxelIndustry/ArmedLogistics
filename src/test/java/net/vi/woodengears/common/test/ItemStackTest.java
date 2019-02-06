package net.vi.woodengears.common.test;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(WGTestExt.class)
class ItemStackTest
{
    @Test
    void checkRegistryValidity()
    {
        ItemStack apple = new ItemStack(Items.APPLE, 1);

        assertThat(apple.getItem()).isEqualTo(Items.APPLE).isNotNull();

        assertThat(apple.toString()).startsWith("1xitem.apple");
    }
}
