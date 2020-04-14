package net.voxelindustry.armedlogistics.client.gui.tab;

import lombok.Getter;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Direction;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.brokkgui.data.RectAlignment;
import net.voxelindustry.brokkgui.data.RectBox;
import net.voxelindustry.brokkgui.data.RelativeBindingHelper;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.element.input.GuiToggleButton;

import static net.voxelindustry.armedlogistics.client.gui.GuiLogisticNode.FONT_HEIGHT;

public class FacingSlot extends GuiToggleButton
{
    @Getter
    private String name;
    @Getter
    private int    slot;

    public FacingSlot(Direction facing, String name, int slots)
    {
        addStyleClass("facing-slot");
        addStyleClass("facing-" + facing.getName2());

        setText(name);
        getLabel().setTextPadding(RectBox.build().top(3).create());
        getLabel().setTextAlignment(RectAlignment.MIDDLE_UP);

        setSize(52, 52);
        setExpandToLabel(false);

        GuiLabel slotCount = new GuiLabel(String.valueOf(slots));
        slotCount.setWidthRatio(1);
        slotCount.setHeight(FONT_HEIGHT);
        addChild(slotCount);
        RelativeBindingHelper.bindToPos(slotCount, this, 0, 26 - FONT_HEIGHT / 2F);

        GuiLabel slotsLabel = new GuiLabel(I18n.format(ArmedLogistics.MODID + ".gui.slot." + (slots < 2 ? "singular" : "plural")));
        slotsLabel.setWidthRatio(1);
        slotsLabel.setHeight(FONT_HEIGHT);
        addChild(slotsLabel);
        RelativeBindingHelper.bindToPos(slotsLabel, this, 0, 26 + FONT_HEIGHT / 2F);
    }
}
