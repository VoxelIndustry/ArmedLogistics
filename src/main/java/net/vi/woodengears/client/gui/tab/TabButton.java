package net.vi.woodengears.client.gui.tab;

import net.minecraft.item.ItemStack;
import net.voxelindustry.brokkgui.data.RelativeBindingHelper;
import net.voxelindustry.brokkgui.element.input.GuiToggleButton;
import net.voxelindustry.brokkgui.wrapper.elements.ItemStackView;

public class TabButton extends GuiToggleButton
{
    private ItemStackView iconView;

    public TabButton(ItemStack icon)
    {
        addStyleClass("tab");
        setSize(28, 32);

        iconView = new ItemStackView(icon);
        iconView.setSize(18, 18);

        addChild(iconView);
        RelativeBindingHelper.bindToPos(iconView, this, 5, 7);
    }

    public void setIconStack(ItemStack iconStack)
    {
        iconView.setItemStack(iconStack);
    }
}
