package net.vi.woodengears.common.test;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.vi.woodengears.common.grid.logistic.ProviderType;
import net.vi.woodengears.common.grid.logistic.node.ColoredItemProvider;
import net.vi.woodengears.common.grid.logistic.node.InventoryBuffer;
import net.voxelindustry.steamlayer.inventory.InventoryHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestColoredItemProvider extends ColoredItemProvider
{
    private BlockPos pos;

    public TestColoredItemProvider(BlockPos pos, ProviderType type, InventoryHandler handler, InventoryBuffer buffer,
                                   ListMultimap<EnumDyeColor, ItemStack> colors)
    {
        super(null, type, handler, null, buffer);

        this.pos = pos;

        getColors().putAll(colors);

        wake();
        sleep();
    }

    public TestColoredItemProvider(InventoryHandler handler, InventoryBuffer buffer,
                                   ListMultimap<EnumDyeColor, ItemStack> colors)
    {
        this(BlockPos.ORIGIN, ProviderType.ACTIVE_PROVIDER, handler, buffer, colors);
    }

    @Override
    public BlockPos getRailPos()
    {
        return pos;
    }

    public static Builder build()
    {
        return new Builder();
    }

    public static class Builder
    {
        private List<ItemStack>                       stacks;
        private BlockPos                              pos;
        private InventoryBuffer                       buffer;
        private ListMultimap<EnumDyeColor, ItemStack> colors;
        private ProviderType                          type;

        private Builder()
        {
            stacks = new ArrayList<>();
            pos = BlockPos.ORIGIN;
            colors = MultimapBuilder.enumKeys(EnumDyeColor.class).arrayListValues().build();
            type = ProviderType.ACTIVE_PROVIDER;
        }

        public Builder stacks(ItemStack... stacks)
        {
            this.stacks.addAll(Arrays.asList(stacks));
            return this;
        }

        public Builder pos(BlockPos pos)
        {
            this.pos = pos;
            return this;
        }

        public Builder buffer(int maxType, int maxCount)
        {
            buffer = new InventoryBuffer(maxType, maxCount);
            return this;
        }

        public Builder color(EnumDyeColor color, ItemStack... stacks)
        {
            colors.putAll(color, Arrays.asList(stacks));
            return this;
        }

        public Builder type(ProviderType type)
        {
            this.type = type;
            return this;
        }

        public TestColoredItemProvider create()
        {
            if (buffer == null)
                buffer = new InventoryBuffer(stacks.size(), stacks.size() * 128);

            return new TestColoredItemProvider(pos, type, new InventoryHandler(NonNullList.from(ItemStack.EMPTY,
                    stacks.toArray(new ItemStack[0]))), buffer, colors);
        }
    }
}
