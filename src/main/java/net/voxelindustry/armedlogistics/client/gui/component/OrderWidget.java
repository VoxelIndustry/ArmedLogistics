package net.voxelindustry.armedlogistics.client.gui.component;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.voxelindustry.armedlogistics.common.grid.logistic.LogisticOrder;
import net.voxelindustry.brokkgui.wrapper.elements.ItemStackView;

public class OrderWidget extends ItemStackView
{
    public OrderWidget(LogisticOrder<ItemStack> order)
    {
        setItemStack(order.getContent());
        setItemTooltip(false);

        setStackTooltipModifier(lines -> lines.add(0, new StringTextComponent("State: " + order.getState())));
    }
}
