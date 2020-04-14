package net.voxelindustry.armedlogistics.common.test;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.voxelindustry.armedlogistics.common.grid.logistic.ProviderType;
import net.voxelindustry.armedlogistics.common.grid.logistic.node.BaseItemStorage;
import net.voxelindustry.armedlogistics.common.grid.logistic.node.InventoryBuffer;
import net.voxelindustry.steamlayer.inventory.InventoryHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestItemStorage extends BaseItemStorage
{
    private BlockPos pos;

    public TestItemStorage(BlockPos pos, InventoryHandler handler, InventoryBuffer providerBuffer)
    {
        super(null, ProviderType.STORAGE, handler, null, providerBuffer);

        this.pos = pos;

        wake();
        sleep();
    }

    public TestItemStorage(InventoryHandler handler, InventoryBuffer providerBuffer)
    {
        this(BlockPos.ZERO, handler, providerBuffer);
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
        private List<ItemStack>  stacks;
        private BlockPos         pos;
        private InventoryBuffer  buffer;
        private InventoryHandler inventory;

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

        public Builder inventory(InventoryHandler inventoryHandler)
        {
            inventory = inventoryHandler;
            return this;
        }

        public Builder pos(BlockPos pos)
        {
            this.pos = pos;
            return this;
        }

        public Builder buffer(int maxType, int maxCount)
        {
            buffer(new InventoryBuffer(maxType, maxCount));
            return this;
        }

        public Builder buffer(InventoryBuffer buffer)
        {
            this.buffer = buffer;
            return this;
        }

        public TestItemStorage create()
        {
            if (buffer == null)
                buffer = new InventoryBuffer(stacks.size(), stacks.size() * 128);
            if (inventory == null)
                inventory = new InventoryHandler(NonNullList.from(ItemStack.EMPTY,
                        stacks.toArray(new ItemStack[0])));

            return new TestItemStorage(pos, inventory, buffer);
        }
    }
}
