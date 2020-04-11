package net.voxelindustry.armedlogistics.client.gui.component;

import net.minecraft.item.ItemStack;
import net.voxelindustry.armedlogistics.common.grid.logistic.LogisticOrder;
import net.voxelindustry.brokkgui.wrapper.elements.ItemStackView;

public class OrderWidget extends ItemStackView
{
    public OrderWidget(LogisticOrder<ItemStack> order)
    {
        setItemStack(order.getContent());
        setItemTooltip(false);

        setStackTooltipModifier(lines -> lines.add(0, "State: " + order.getState()));
    }
}
