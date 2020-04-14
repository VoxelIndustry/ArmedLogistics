package net.voxelindustry.armedlogistics.common.test;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.voxelindustry.armedlogistics.common.grid.logistic.node.BaseItemRequester;
import net.voxelindustry.steamlayer.inventory.InventoryHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestItemRequester extends BaseItemRequester
{
    private BlockPos pos;

    public TestItemRequester(InventoryHandler handler)
    {
        this(BlockPos.ZERO, handler);
    }

    public TestItemRequester(BlockPos pos, InventoryHandler handler)
    {
        super(null, handler, null);

        this.pos = pos;
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
        private List<ItemStack> stacks;
        private BlockPos        pos;

        private Builder()
        {
            stacks = new ArrayList<>();
            pos = BlockPos.ZERO;
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

        public TestItemRequester create()
        {
            return new TestItemRequester(pos,
                    new InventoryHandler(NonNullList.from(ItemStack.EMPTY, stacks.toArray(new ItemStack[0]))));
        }
    }
}
