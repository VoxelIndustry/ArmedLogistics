package net.vi.woodengears.client.gui.component;

import net.minecraft.client.resources.I18n;
import net.voxelindustry.brokkgui.data.RectSide;
import net.voxelindustry.brokkgui.element.input.GuiRadioButton;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.shape.Rectangle;

/**
 * Supply-Once-Keep Combobox
 * used for requesters
 */
public class SOKCombo extends GuiAbsolutePane
{
    public SOKCombo()
    {
        this.setSize(100, 35);
        this.setID("sokcombo");

        Rectangle background = new Rectangle(11, 35);
        background.setID("sok-button-background");

        this.addChild(background, 89, 0);

        GuiRadioButton supplyButton = new GuiRadioButton(I18n.format("woodengears.gui.sok.supply"));
        supplyButton.setHeight(9);
        supplyButton.setExpandToLabel(true);
        supplyButton.setButtonSide(RectSide.RIGHT);
        supplyButton.addStyleClass("sok-button");
        this.addChild(supplyButton, 0, 2);

        GuiRadioButton onceButton = new GuiRadioButton(I18n.format("woodengears.gui.sok.once"));
        onceButton.setHeight(9);
        onceButton.setExpandToLabel(true);
        onceButton.setButtonSide(RectSide.RIGHT);
        onceButton.addStyleClass("sok-button");
        this.addChild(onceButton, 0, 14);

        GuiRadioButton keepButton = new GuiRadioButton(I18n.format("woodengears.gui.sok.keep"));
        keepButton.setHeight(9);
        keepButton.setExpandToLabel(true);
        keepButton.setButtonSide(RectSide.RIGHT);
        keepButton.addStyleClass("sok-button");
        this.addChild(keepButton, 0, 26);
    }
}
