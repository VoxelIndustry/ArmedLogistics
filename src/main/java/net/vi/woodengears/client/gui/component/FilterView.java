package net.vi.woodengears.client.gui.component;

import net.minecraft.client.resources.I18n;
import net.vi.woodengears.WoodenGears;
import net.voxelindustry.brokkgui.data.RectAlignment;
import net.voxelindustry.brokkgui.data.RectOffset;
import net.voxelindustry.brokkgui.element.GuiButton;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.shape.Rectangle;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FilterView extends GuiAbsolutePane
{
    private final GuiButton       switchButton;
    private       GuiAbsolutePane filtersPanel;

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
        switchButton.setOnActionEvent(e -> whitelistSetter.accept(!whitelistGetter.get()));
        this.addChild(switchButton, 7, 28);

        this.refreshWhitelist(whitelistGetter.get());
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
}
