package net.voxelindustry.armedlogistics.common.test;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.voxelindustry.armedlogistics.common.grid.logistic.ProviderType;
import net.voxelindustry.armedlogistics.common.grid.logistic.node.BaseItemProvider;
import net.voxelindustry.armedlogistics.common.grid.logistic.node.InventoryBuffer;
import net.voxelindustry.steamlayer.inventory.InventoryHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestItemProvider extends BaseItemProvider
{
    private BlockPos pos;

    public TestItemProvider(BlockPos pos, ProviderType type, InventoryHandler handler, InventoryBuffer buffer)
    {
        super(null, type, handler, null, buffer);

        this.pos = pos;

        wake();
        sleep();
    }

    public TestItemProvider(InventoryHandler handler, InventoryBuffer buffer)
    {
        this(BlockPos.ZERO, ProviderType.ACTIVE_PROVIDER, handler, buffer);
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
        private InventoryBuffer buffer;
        private ProviderType    type;

        private Builder()
        {
            stacks = new ArrayList<>();
            pos = BlockPos.ZERO;
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

        public Builder type(ProviderType type)
        {
            this.type = type;
            return this;
        }

        public TestItemProvider create()
        {
            if (buffer == null)
                buffer = new InventoryBuffer(stacks.size(), stacks.size() * 128);

            return new TestItemProvider(pos, type, new InventoryHandler(NonNullList.from(ItemStack.EMPTY,
                    stacks.toArray(new ItemStack[0]))), buffer);
        }
    }
}
