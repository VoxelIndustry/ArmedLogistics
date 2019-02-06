package net.vi.woodengears.common.test;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.vi.woodengears.common.grid.logistic.node.BaseItemProvider;
import net.vi.woodengears.common.grid.logistic.node.InventoryBuffer;
import net.voxelindustry.steamlayer.inventory.InventoryHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestItemProvider extends BaseItemProvider
{
    private BlockPos pos;

    public TestItemProvider(BlockPos pos, InventoryHandler handler, InventoryBuffer buffer)
    {
        super(null, handler, buffer);

        this.pos = pos;

        this.wake();
        this.sleep();
    }

    public TestItemProvider(InventoryHandler handler, InventoryBuffer buffer)
    {
        this(BlockPos.ORIGIN, handler, buffer);
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
        private List<ItemStack> stacks;
        private BlockPos        pos;
        private InventoryBuffer buffer;

        private Builder()
        {
            this.stacks = new ArrayList<>();
            this.pos = BlockPos.ORIGIN;
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

        public TestItemProvider create()
        {
            if (buffer == null)
                this.buffer = new InventoryBuffer(this.stacks.size(), this.stacks.size() * 128);

            return new TestItemProvider(pos, new InventoryHandler(NonNullList.from(ItemStack.EMPTY,
                    stacks.toArray(new ItemStack[0]))), buffer);
        }
    }
}
