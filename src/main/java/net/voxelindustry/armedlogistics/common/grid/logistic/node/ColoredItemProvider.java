package net.voxelindustry.armedlogistics.common.grid.logistic.node;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.voxelindustry.armedlogistics.common.grid.logistic.ColoredShipment;
import net.voxelindustry.armedlogistics.common.grid.logistic.ColoredStack;
import net.voxelindustry.armedlogistics.common.grid.logistic.ItemStackMethods;
import net.voxelindustry.armedlogistics.common.grid.logistic.LogisticNetwork;
import net.voxelindustry.armedlogistics.common.grid.logistic.ProviderType;
import net.voxelindustry.armedlogistics.common.tile.TileLogicisticNode;
import net.voxelindustry.steamlayer.inventory.InventoryHandler;
import net.voxelindustry.steamlayer.utils.ItemUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ColoredItemProvider extends BaseItemProvider implements ColoredProvider<ItemStack>
{
    @Getter(AccessLevel.PROTECTED)
    private ListMultimap<EnumDyeColor, ItemStack> colors;

    private List<ColoredShipment<ItemStack>> coloredShipments;

    public ColoredItemProvider(TileLogicisticNode tile,
                               ProviderType type,
                               InventoryHandler handler,
                               Supplier<LogisticNetwork<ItemStack>> networkSupplier,
                               InventoryBuffer buffer)
    {
        super(tile, type, handler, networkSupplier, buffer);

        colors = MultimapBuilder.enumKeys(EnumDyeColor.class).arrayListValues().build();

        coloredShipments = new ArrayList<>();
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
        if (!hasColor(coloredStack.getColor()))
            return false;

        wake();

        List<ItemStack> values = getValuesFromColor(coloredStack.getColor());

        int contained = 0;
        for (ItemStack stack : getCompressedContents())
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
        if (!hasColor(coloredStack.getColor()))
            return 0;

        wake();

        List<ItemStack> values = getValuesFromColor(coloredStack.getColor());

        int contained = 0;
        for (ItemStack stack : getCompressedContents())
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
        if (isBufferFull())
            return Collections.emptyList();

        if (containedPart(coloredStack) == 0)
            return Collections.emptyList();

        sleep();

        List<ItemStack> values = getValuesFromColor(coloredStack.getColor());

        int extracted = 0;
        List<ItemStack> extractedStacks = new ArrayList<>();

        for (int i = 0; i < getHandler().getSlots(); i++)
        {
            ItemStack stack = getHandler().getStackInSlot(i);

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

    @Override
    public void addColoredShipment(ColoredShipment<ItemStack> shipment)
    {
        coloredShipments.add(shipment);
    }

    @Override
    public boolean removeColoredShipment(ColoredShipment<ItemStack> shipment)
    {
        return coloredShipments.remove(shipment);
    }

    @Override
    public Collection<ColoredShipment<ItemStack>> getColoredShipments()
    {
        return coloredShipments;
    }

    @Override
    public void deliverColoredShipment(ColoredShipment<ItemStack> shipment)
    {

    }
}
