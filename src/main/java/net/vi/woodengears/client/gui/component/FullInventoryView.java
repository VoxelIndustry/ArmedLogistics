package net.vi.woodengears.client.gui.component;

import net.minecraft.item.ItemStack;
import net.voxelindustry.brokkgui.data.RelativeBindingHelper;
import net.voxelindustry.brokkgui.gui.SubGuiScreen;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.panel.ScrollPane;
import net.voxelindustry.brokkgui.policy.GuiOverflowPolicy;
import net.voxelindustry.brokkgui.policy.GuiScrollbarPolicy;
import net.voxelindustry.brokkgui.wrapper.elements.ItemStackView;

import java.util.ArrayList;
import java.util.List;

public class FullInventoryView extends SubGuiScreen
{
    private List<ItemStackView> views;
    private GuiAbsolutePane     mainPanel;

    public FullInventoryView()
    {
        super(0.5f, 0.45f);
        this.setSize(180, 110);
        this.setzLevel(300);
        this.setID("inv-window");

        views = new ArrayList<>();
        mainPanel = new GuiAbsolutePane();
        mainPanel.setWidth(20 * 8);
        mainPanel.setxTranslate(11);

        ScrollPane scrollPane = new ScrollPane();
        RelativeBindingHelper.bindToPos(scrollPane, this, 1, 1);
        scrollPane.setWidth(178);
        scrollPane.setHeight(108);
        scrollPane.setGuiOverflowPolicy(GuiOverflowPolicy.TRIM);
        scrollPane.setScrollYPolicy(GuiScrollbarPolicy.ALWAYS);
        scrollPane.setChild(mainPanel);
        scrollPane.setGripYWidth(8);
        scrollPane.setGripYHeight(22);
        scrollPane.setzLevel(310);
        this.addChild(scrollPane);

        this.setCloseOnClick(true);
    }

    public void refreshStacks(List<ItemStack> stacks)
    {
        int diff = this.views.size() - stacks.size();
        if (diff > 0)
        {
            for (int slot = 0; slot < diff; slot++)
                mainPanel.removeChild(this.views.remove(this.views.size() - 1 - slot));
        }
        else if (diff < 0)
        {
            for (int slot = 0; slot < -diff; slot++)
            {
                ItemStackView view = new ItemStackView();
                view.setSize(18, 18);
                view.setItemTooltip(true);
                view.setzLevel(301);

                mainPanel.addChild(view, 20 * (views.size() % 8), 20 * (views.size() / 8));
                views.add(view);
            }
        }

        for (int slot = 0; slot < views.size(); slot++)
            views.get(slot).setItemStack(stacks.get(slot));

        mainPanel.setHeight((float) (20 * Math.ceil(stacks.size() / 8f)));
    }
}
