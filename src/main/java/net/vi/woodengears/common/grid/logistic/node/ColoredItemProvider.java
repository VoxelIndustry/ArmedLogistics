package net.vi.woodengears.common.grid.logistic.node;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.vi.woodengears.common.grid.logistic.ColoredStack;
import net.vi.woodengears.common.tile.TileLogicisticNode;
import net.voxelindustry.steamlayer.inventory.InventoryHandler;
import net.voxelindustry.steamlayer.utils.ItemUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ColoredItemProvider extends BaseItemProvider implements ColoredProvider<ItemStack>
{
    @Getter(AccessLevel.PROTECTED)
    private ListMultimap<EnumDyeColor, ItemStack> colors;

    public ColoredItemProvider(TileLogicisticNode tile, InventoryHandler handler, InventoryBuffer buffer)
    {
        super(tile, handler, buffer);

        this.colors = MultimapBuilder.enumKeys(EnumDyeColor.class).arrayListValues().build();
    }

    @Override
    public boolean hasColor(EnumDyeColor color)
    {
        return colors.containsKey(color);
    }

    @Override
    public List<ItemStack> getValuesFromColor(EnumDyeColor color)
    {
        return colors.get(color);
    }

    @Override
    public boolean contains(ColoredStack coloredStack)
    {
        if (!this.hasColor(coloredStack.getColor()))
            return false;

        this.wake();

        List<ItemStack> values = getValuesFromColor(coloredStack.getColor());

        int contained = 0;
        for (ItemStack stack : this.getCompressedContents())
        {
            if (stack.isEmpty())
                continue;
            if (values.stream().anyMatch(value -> ItemUtils.deepEquals(value, stack)))
                contained += stack.getCount();

            if (contained >= coloredStack.getQuantity())
                return true;
        }
        return false;
    }

    @Override
    public int containedPart(ColoredStack coloredStack)
    {
        if (!this.hasColor(coloredStack.getColor()))
            return 0;

        this.wake();

        List<ItemStack> values = getValuesFromColor(coloredStack.getColor());

        int contained = 0;
        for (ItemStack stack : this.getCompressedContents())
        {
            if (stack.isEmpty())
                continue;
            if (values.stream().anyMatch(value -> ItemUtils.deepEquals(value, stack)))
                contained += stack.getCount();

            if (contained >= coloredStack.getQuantity())
                return coloredStack.getQuantity();
        }

        return contained;
    }

    @Override
    public List<ItemStack> extract(ColoredStack coloredStack)
    {
        if (this.isBufferFull())
            return Collections.emptyList();

        if (this.containedPart(coloredStack) == 0)
            return Collections.emptyList();

        this.sleep();

        List<ItemStack> values = getValuesFromColor(coloredStack.getColor());

        int extracted = 0;
        List<ItemStack> extractedStacks = new ArrayList<>();

        for (int i = 0; i < this.getHandler().getSlots(); i++)
        {
            ItemStack stack = this.getHandler().getStackInSlot(i);

            if (values.stream().anyMatch(value -> ItemUtils.deepEquals(value, stack)))
            {
                int toExtract = Math.min(coloredStack.getQuantity() - extracted, stack.getCount());

                Optional<ItemStack> alreadyExtracted =
                        extractedStacks.stream().filter(candidate -> ItemUtils.deepEquals(candidate, stack)).findFirst();
                if (alreadyExtracted.isPresent())
                    alreadyExtracted.get().grow(toExtract);
                else
                {
                    ItemStack copy = stack.copy();
                    copy.setCount(toExtract);
                    extractedStacks.add(copy);
                }

                stack.shrink(toExtract);
                extracted += toExtract;

                getHandler().setStackInSlot(i, stack);

                if (extracted == coloredStack.getQuantity())
                    break;
            }
        }

        return getBuffer().addAll(extractedStacks);
    }

    @Override
    public boolean isColored()
    {
        return true;
    }
}
