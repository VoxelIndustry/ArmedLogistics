package net.vi.woodengears.common.grid.logistic.node;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.vi.woodengears.common.grid.logistic.ColoredStack;
import net.vi.woodengears.common.grid.logistic.ItemStackMethods;
import net.vi.woodengears.common.grid.logistic.ProviderType;
import net.vi.woodengears.common.tile.TileLogicisticNode;
import net.voxelindustry.steamlayer.inventory.InventoryHandler;
import net.voxelindustry.steamlayer.utils.ItemUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ColoredItemProvider extends BaseItemProvider implements ColoredProvider<ItemStack>
{
    @Getter(AccessLevel.PROTECTED)
    private ListMultimap<EnumDyeColor, ItemStack> colors;

    public ColoredItemProvider(TileLogicisticNode tile, ProviderType type, InventoryHandler handler,
                               InventoryBuffer buffer)
    {
        super(tile, type, handler, buffer);

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
                ItemStack extractedStack = stack.copy();
                extractedStack.setCount(coloredStack.getQuantity() - extracted);

                extractedStack = getHandler().extractItem(i, extractedStack.getCount(), true);

                extracted += extractedStack.getCount();

                ItemStackMethods.getInstance().pushStackToList(extractedStacks, stack, extractedStack);

                getHandler().extractItem(i, extractedStack.getCount(), false);

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
