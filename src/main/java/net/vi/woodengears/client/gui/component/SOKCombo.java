package net.vi.woodengears.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.vi.woodengears.common.grid.logistic.node.Requester;
import net.vi.woodengears.common.grid.logistic.node.RequesterMode;
import net.voxelindustry.brokkgui.data.RectAlignment;
import net.voxelindustry.brokkgui.data.RectOffset;
import net.voxelindustry.brokkgui.data.RectSide;
import net.voxelindustry.brokkgui.element.input.GuiRadioButton;
import net.voxelindustry.brokkgui.element.input.GuiToggleGroup;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.shape.Rectangle;

import java.util.function.Consumer;

/**
 * Supply-Once-Keep Combobox
 * used for requesters
 */
public class SOKCombo extends GuiAbsolutePane
{
    private final RectOffset     textPadding = RectOffset.build().top(0.5f).create();
    private final GuiToggleGroup toggleGroup;
    private final GuiRadioButton keepButton;
    private final GuiRadioButton onceButton;
    private final GuiRadioButton supplyButton;

    private String supplyText = I18n.format("woodengears.gui.sok.supply");
    private String onceText   = I18n.format("woodengears.gui.sok.once");
    private String keepText   = I18n.format("woodengears.gui.sok.keep");

    public SOKCombo(RequesterMode currentMode, Consumer<RequesterMode> modeCallback)
    {
        // Longest width + left_offset + button + right_offset + label offset
        this.setSize(getLongestText() + 2 + 9 + 2 + 3, 37);
        this.setID("sokcombo");

        Rectangle sokLines = new Rectangle(1, 15);
        sokLines.setID("soklines");
        this.addChild(sokLines, getWidth() - 6, 11);

        this.supplyButton = new GuiRadioButton(supplyText);
        supplyButton.setExpandToLabel(false);
        supplyButton.getLabel().setTextAlignment(RectAlignment.LEFT_CENTER);
        supplyButton.getLabel().setTextPadding(textPadding);
        supplyButton.setSize(getLongestText() + 3 + 9, 7);
        supplyButton.setButtonSide(RectSide.RIGHT);
        supplyButton.addStyleClass("sok-button");
        this.addChild(supplyButton, 2, 3);

        this.onceButton = new GuiRadioButton(onceText);
        onceButton.setExpandToLabel(false);
        onceButton.getLabel().setTextAlignment(RectAlignment.LEFT_CENTER);
        onceButton.getLabel().setTextPadding(textPadding);
        onceButton.setSize(getLongestText() + 3 + 9, 7);
        onceButton.setButtonSide(RectSide.RIGHT);
        onceButton.addStyleClass("sok-button");
        this.addChild(onceButton, 2, 15);

        this.keepButton = new GuiRadioButton(keepText);
        keepButton.setExpandToLabel(false);
        keepButton.getLabel().setTextAlignment(RectAlignment.LEFT_CENTER);
        keepButton.getLabel().setTextPadding(textPadding);
        keepButton.setSize(getLongestText() + 3 + 9, 7);
        keepButton.setButtonSide(RectSide.RIGHT);
        keepButton.addStyleClass("sok-button");
        this.addChild(keepButton, 2, 27);

        this.toggleGroup = new GuiToggleGroup();
        toggleGroup.setAllowNothing(false);

        supplyButton.setToggleGroup(toggleGroup);
        onceButton.setToggleGroup(toggleGroup);
        keepButton.setToggleGroup(toggleGroup);

        toggleGroup.getSelectedButtonProperty().addListener((obs, oldValue, newValue) ->
        {
            if (newValue == onceButton)
                modeCallback.accept(RequesterMode.ONCE);
            else if (newValue == keepButton)
                modeCallback.accept(RequesterMode.KEEP);
            else
                modeCallback.accept(RequesterMode.CONTINUOUS);
        });
    }

    public void setMode(RequesterMode mode)
    {
        switch (mode)
        {
            case ONCE:
                toggleGroup.setSelectedButton(onceButton);
                break;
            case KEEP:
                toggleGroup.setSelectedButton(keepButton);
                break;
            case CONTINUOUS:
                toggleGroup.setSelectedButton(supplyButton);
                break;
        }
    }

    private float getLongestText()
    {
        return Math.max(Minecraft.getMinecraft().fontRenderer.getStringWidth(supplyText),
                Math.max(Minecraft.getMinecraft().fontRenderer.getStringWidth(onceText),
                        Minecraft.getMinecraft().fontRenderer.getStringWidth(keepText)));
    }
}
