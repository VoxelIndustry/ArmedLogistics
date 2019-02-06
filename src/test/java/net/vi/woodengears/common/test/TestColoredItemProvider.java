package net.vi.woodengears.common.test;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.vi.woodengears.common.grid.logistic.node.ColoredItemProvider;
import net.vi.woodengears.common.grid.logistic.node.InventoryBuffer;
import net.voxelindustry.steamlayer.inventory.InventoryHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestColoredItemProvider extends ColoredItemProvider
{
    private BlockPos pos;

    public TestColoredItemProvider(BlockPos pos, InventoryHandler handler, InventoryBuffer buffer,
                                   ListMultimap<EnumDyeColor, ItemStack> colors)
    {
        super(null, handler, buffer);

        this.pos = pos;

        this.getColors().putAll(colors);

        this.wake();
        this.sleep();
    }

    public TestColoredItemProvider(InventoryHandler handler, InventoryBuffer buffer,
                                   ListMultimap<EnumDyeColor, ItemStack> colors)
    {
        this(BlockPos.ORIGIN, handler, buffer, colors);
    }

    @Override
    public BlockPos getRailPos()
    {
        return this.pos;
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

        private Builder()
        {
            this.stacks = new ArrayList<>();
            this.pos = BlockPos.ORIGIN;
            this.colors = MultimapBuilder.enumKeys(EnumDyeColor.class).arrayListValues().build();
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
            this.buffer = new InventoryBuffer(maxType, maxCount);
            return this;
        }

        public Builder color(EnumDyeColor color, ItemStack... stacks)
        {
            this.colors.putAll(color, Arrays.asList(stacks));
            return this;
        }

        public TestColoredItemProvider create()
        {
            if (buffer == null)
                this.buffer = new InventoryBuffer(this.stacks.size(), this.stacks.size() * 128);

            return new TestColoredItemProvider(pos, new InventoryHandler(NonNullList.from(ItemStack.EMPTY,
                    stacks.toArray(new ItemStack[0]))), buffer, colors);
        }
    }
}
