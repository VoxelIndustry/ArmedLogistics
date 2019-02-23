package net.vi.woodengears.client.gui.component;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.vi.woodengears.WoodenGears;
import net.voxelindustry.brokkgui.BrokkGuiPlatform;
import net.voxelindustry.brokkgui.data.RectAlignment;
import net.voxelindustry.brokkgui.data.RectOffset;
import net.voxelindustry.brokkgui.element.GuiButton;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.event.HoverEvent;
import net.voxelindustry.brokkgui.event.KeyEvent;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.shape.Rectangle;
import net.voxelindustry.brokkgui.wrapper.elements.ItemStackView;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FilterView extends GuiAbsolutePane implements ICopyPasteHandler<MutableItemStackView>
{
    private final GuiButton       switchButton;
    private       GuiAbsolutePane filtersPanel;

    private ItemStackView[]      filters;
    private MutableItemStackView selected;

    public FilterView(Supplier<Boolean> whitelistGetter, Consumer<Boolean> whitelistSetter)
    {
        this.setSize(164, 38);
        this.setID("filterview");

        this.filtersPanel = new GuiAbsolutePane();
        filtersPanel.setSize(164, 20);
        filtersPanel.setID("filters-panel");
        this.addChild(filtersPanel, 0, 9);

        GuiLabel filterLabel = new GuiLabel(I18n.format(WoodenGears.MODID + ".gui.filter.title"));
        filterLabel.setExpandToText(true);
        filterLabel.setHeight(9);
        filterLabel.setTextAlignment(RectAlignment.LEFT_CENTER);
        filterLabel.setTextPadding(RectOffset.build().left(2).right(2).top(1).create());
        filterLabel.setID("filter-label");
        this.addChild(filterLabel, 1, 1);

        Rectangle invLabelLeftLine = new Rectangle();
        this.addChild(invLabelLeftLine, 0, 1);
        invLabelLeftLine.setWidth(1);
        invLabelLeftLine.setHeight(9);
        invLabelLeftLine.addStyleClass("box-line");

        Rectangle invLabelTopLine = new Rectangle();
        this.addChild(invLabelTopLine, 1, 0);
        invLabelTopLine.setWidth(filterLabel.getWidth());
        invLabelTopLine.setHeight(1);
        invLabelTopLine.addStyleClass("box-line");

        Rectangle invLabelRightLine = new Rectangle();
        this.addChild(invLabelRightLine, filterLabel.getWidth() + 1, 1);
        invLabelRightLine.setWidth(1);
        invLabelRightLine.setHeight(9);
        invLabelRightLine.addStyleClass("box-line");

        this.switchButton = new GuiButton();
        switchButton.setSize(148, 10);
        switchButton.setID("filter-switch-button");
        switchButton.getLabel().setTextPadding(RectOffset.build().top(1).create());
        switchButton.setOnActionEvent(e -> whitelistSetter.accept(!whitelistGetter.get()));
        this.addChild(switchButton, 7, 28);

        this.refreshWhitelist(whitelistGetter.get());

        this.filters = new ItemStackView[9];
        for (int i = 0; i < 9; i++)
        {
            MutableItemStackView view = new MutableItemStackView(ItemStack.EMPTY, false, this);
            view.setSize(18, 18);
            filters[i] = view;
            filtersPanel.addChild(view, i * 18 + 1, 1);
        }

        this.setFocusable(true);
        this.getEventDispatcher().addHandler(HoverEvent.TYPE, this::onHover);
        this.getEventDispatcher().addHandler(KeyEvent.PRESS, this::onKeyPressed);
        this.getEventDispatcher().addHandler(KeyEvent.RELEASE, this::onKeyReleased);


    }

    private void onKeyPressed(KeyEvent.Press event)
    {
        if (BrokkGuiPlatform.getInstance().getKeyboardUtil().isCtrlKeyDown() &&
                !this.getStyleClass().contains("copypasting"))
        {
            this.addStyleClass("copypasting");

            if (selected != null)
                selected.addStyleClass("selected");
        }
    }

    private void onKeyReleased(KeyEvent.Release event)
    {
        if (!BrokkGuiPlatform.getInstance().getKeyboardUtil().isCtrlKeyDown() &&
                this.getStyleClass().contains("copypasting"))
        {
            this.removeStyleClass("copypasting");

            if (selected != null)
                selected.removeStyleClass("selected");
        }
    }

    private void onHover(HoverEvent event)
    {
        if (!BrokkGuiPlatform.getInstance().getKeyboardUtil().isCtrlKeyDown())
            return;

        if (event.isEntering())
        {
            this.addStyleClass("copypasting");

            if (selected != null)
                selected.addStyleClass("selected");
        }
        else if (this.getStyleClass().contains("copypasting"))
        {
            this.removeStyleClass("copypasting");
            if (selected != null)
                selected.removeStyleClass("selected");
        }
    }

    public void refreshWhitelist(boolean whitelist)
    {
        if (whitelist)
        {
            this.addStyleClass("whitelist");
            this.removeStyleClass("blacklist");

            this.switchButton.setText(I18n.format(WoodenGears.MODID + ".gui.filter.whitelist"));
        }
        else
        {
            this.addStyleClass("blacklist");
            this.removeStyleClass("whitelist");

            this.switchButton.setText(I18n.format(WoodenGears.MODID + ".gui.filter.blacklist"));
        }
    }

    @Override
    public void setClipboard(MutableItemStackView value)
    {
        if (value != null)
            this.addStyleClass("copypasting");

        if (value == selected)
            return;

        if (selected != null)
            selected.removeStyleClass("selected");
        selected = value;

        if (selected != null)
            selected.addStyleClass("selected");
    }

    @Override
    public MutableItemStackView getClipboard()
    {
        return selected;
    }
}
