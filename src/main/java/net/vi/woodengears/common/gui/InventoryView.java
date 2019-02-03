package net.vi.woodengears.common.gui;

import fr.ourten.teabeans.binding.BaseExpression;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.client.GuiProvider;
import net.voxelindustry.brokkgui.data.RelativeBindingHelper;
import net.voxelindustry.brokkgui.element.GuiButton;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.gui.SubGuiScreen;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.panel.GuiRelativePane;
import net.voxelindustry.brokkgui.panel.ScrollPane;
import net.voxelindustry.brokkgui.policy.GuiOverflowPolicy;
import net.voxelindustry.brokkgui.policy.GuiScrollbarPolicy;
import net.voxelindustry.brokkgui.shape.Rectangle;
import net.voxelindustry.brokkgui.wrapper.elements.ItemStackView;

import java.util.ArrayList;
import java.util.List;

public class InventoryView extends GuiRelativePane
{
    private final List<ItemStack>     rawStacks;
    private final List<ItemStackView> stacks;
    private final GuiAbsolutePane     stacksPane;
    private final GuiButton           moreButton;
    private final GuiLabel            emptyLabel;

    private final FullStacksView fullStackView;

    public InventoryView(GuiProvider guiProvider)
    {
        this.setHeight(67);
        this.setWidthRatio(1);
        this.rawStacks = new ArrayList<>();
        this.stacks = new ArrayList<>();
        this.stacksPane = new GuiAbsolutePane();
        this.fullStackView = new FullStacksView();
        stacksPane.setID("stacks-panel");
        this.addChild(stacksPane, 0.5f, 0f);

        Rectangle topLine = new Rectangle();
        this.addChild(topLine, 0, 0);
        RelativeBindingHelper.bindToPos(topLine, stacksPane, 0, -1);
        topLine.getWidthProperty().bind(stacksPane.getWidthProperty());
        topLine.setHeight(1);
        topLine.addStyleClass("box-line-top");

        Rectangle bottomLine = new Rectangle();
        this.addChild(bottomLine, 0, 0);
        RelativeBindingHelper.bindToPos(bottomLine, stacksPane, null, stacksPane.getHeightProperty());
        bottomLine.getWidthProperty().bind(stacksPane.getWidthProperty());
        bottomLine.setHeight(1);
        bottomLine.addStyleClass("box-line-bottom");

        Rectangle leftLine = new Rectangle();
        this.addChild(leftLine, 0, 0);
        RelativeBindingHelper.bindToPos(leftLine, stacksPane, -1, 0);
        leftLine.setWidth(1);
        leftLine.getHeightProperty().bind(stacksPane.getHeightProperty());
        leftLine.addStyleClass("box-line-top");

        Rectangle rightLine = new Rectangle();
        this.addChild(rightLine, 0, 0);
        RelativeBindingHelper.bindToPos(rightLine, stacksPane, stacksPane.getWidthProperty(), null);
        rightLine.setWidth(1);
        rightLine.getHeightProperty().bind(stacksPane.getHeightProperty());
        rightLine.addStyleClass("box-line-bottom");

        this.moreButton = new GuiButton();
        moreButton.setSize(148, 11);
        moreButton.setVisible(false);
        moreButton.setID("more-button");
        this.addChild(moreButton);
        moreButton.setxTranslate(7);
        RelativeBindingHelper.bindToPos(moreButton, stacksPane, null,
                BaseExpression.transform(stacksPane.getHeightProperty(), height -> height + 1));

        moreButton.setOnActionEvent(e ->
        {
            guiProvider.addSubGui(fullStackView);
            fullStackView.refreshStacks(this.rawStacks);
        });

        this.emptyLabel = new GuiLabel(I18n.format(WoodenGears.MODID + ".gui.inventory.empty"));
        emptyLabel.setSize(148, 11);
        emptyLabel.setVisible(false);
        emptyLabel.setID("empty-label");
        this.addChild(emptyLabel);
        emptyLabel.setxTranslate(7);
        RelativeBindingHelper.bindToPos(emptyLabel, stacksPane, null,
                BaseExpression.transform(stacksPane.getHeightProperty(), height -> height + 1));

        guiProvider.getListeners().attach(guiProvider.getProvider().getCachedInventoryProperty(),
                obs -> refreshStacks(guiProvider.getProvider().getCachedInventoryProperty().getValue()));
        this.refreshStacks(guiProvider.getProvider().getCachedInventoryProperty().getValue());
    }

    private void refreshStacks(IItemHandler inventory)
    {
        if (inventory == null)
            return;

        List<ItemStack> rawStacks = new ArrayList<>(inventory.getSlots());
        for (int slot = 0; slot < inventory.getSlots(); slot++)
        {
            ItemStack inSlot = inventory.getStackInSlot(slot);

            if (!inSlot.isEmpty())
                rawStacks.add(inSlot);
        }

        int diff = this.stacks.size() - rawStacks.size();
        if (diff > 0)
        {
            for (int slot = 0; slot < diff; slot++)
            {
                if (this.stacks.size() - 1 - slot < 0)
                    break;
                stacksPane.removeChild(this.stacks.remove(this.stacks.size() - 1 - slot));
            }
        }
        else if (diff < 0 && stacks.size() < 27)
        {
            diff = -diff;
            for (int slot = 0; slot < diff; slot++)
            {
                if (stacks.size() == 27)
                    break;

                ItemStackView view = new ItemStackView();
                view.setSize(18, 18);
                view.setItemTooltip(true);

                stacksPane.addChild(view, 18 * (stacks.size() % 9), 18 * (stacks.size() / 9));
                stacks.add(view);
            }
        }

        this.moreButton.setVisible(false);
        this.emptyLabel.setVisible(false);
        if (this.stacks.isEmpty())
        {
            this.stacksPane.setWidth(18 * 9);
            this.stacksPane.setHeight(1);
            this.emptyLabel.setVisible(true);
        }
        else if (this.stacks.size() < 9)
        {
            this.stacksPane.setWidth(18 * stacks.size());
            this.stacksPane.setHeight(18);
            this.stacksPane.setyTranslate(9);
        }
        else
        {
            this.stacksPane.setWidth(18 * 9);
            this.stacksPane.setHeight((float) (18 * Math.ceil(stacks.size() / 9f)));
            this.stacksPane.setyTranslate((float) (9 * Math.ceil(stacks.size() / 9f)));

            if (rawStacks.size() > 27)
            {
                this.rawStacks.clear();
                this.rawStacks.addAll(rawStacks);
                this.moreButton.setVisible(true);
                this.moreButton.setText(I18n.format(WoodenGears.MODID + ".gui.inventory.more", rawStacks.size() - 27));
            }
        }

        for (int slot = 0; slot < Math.min(stacks.size(), 27); slot++)
            stacks.get(slot).setItemStack(rawStacks.get(slot));
    }

    private static class FullStacksView extends SubGuiScreen
    {
        private List<ItemStackView> views;
        private GuiAbsolutePane     mainPanel;

        public FullStacksView()
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
}
