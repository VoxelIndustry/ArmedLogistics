package net.vi.woodengears.common.test;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.vi.woodengears.common.grid.logistic.ProviderType;
import net.vi.woodengears.common.grid.logistic.node.BaseItemStorage;
import net.vi.woodengears.common.grid.logistic.node.InventoryBuffer;
import net.voxelindustry.steamlayer.inventory.InventoryHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestItemStorage extends BaseItemStorage
{
    private BlockPos pos;

    public TestItemStorage(BlockPos pos, InventoryHandler handler, InventoryBuffer providerBuffer, InventoryBuffer storageBuffer)
    {
        super(null, ProviderType.STORAGE, handler, null, providerBuffer, storageBuffer);

        this.pos = pos;

        wake();
        sleep();
    }

    public TestItemStorage(InventoryHandler handler, InventoryBuffer providerBuffer, InventoryBuffer storageBuffer)
    {
        this(BlockPos.ORIGIN, handler, providerBuffer, storageBuffer);
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
        private InventoryBuffer providerBuffer;
        private InventoryBuffer storageBuffer;

        private Builder()
        {
            stacks = new ArrayList<>();
            pos = BlockPos.ORIGIN;
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

        public Builder providerBuffer(int maxType, int maxCount)
        {
            providerBuffer = new InventoryBuffer(maxType, maxCount);
            return this;
        }

        public Builder storageBuffer(int maxType, int maxCount)
        {
            storageBuffer(new InventoryBuffer(maxType, maxCount));
            return this;
        }

        public Builder storageBuffer(InventoryBuffer buffer)
        {
            storageBuffer = buffer;
            return this;
        }

        public TestItemStorage create()
        {
            if (providerBuffer == null)
                providerBuffer = new InventoryBuffer(stacks.size(), stacks.size() * 128);
            if (storageBuffer == null)
                storageBuffer = new InventoryBuffer(stacks.size(), stacks.size() * 128);

            return new TestItemStorage(pos, new InventoryHandler(NonNullList.from(ItemStack.EMPTY,
                    stacks.toArray(new ItemStack[0]))), providerBuffer, storageBuffer);
        }
    }
}
