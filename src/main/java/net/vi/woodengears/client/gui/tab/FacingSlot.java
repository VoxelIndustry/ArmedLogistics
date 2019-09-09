package net.vi.woodengears.client.gui.tab;

import lombok.Getter;
import net.minecraft.client.resources.I18n;
import net.vi.woodengears.WoodenGears;
import net.voxelindustry.brokkgui.data.RectAlignment;
import net.voxelindustry.brokkgui.data.RectBox;
import net.voxelindustry.brokkgui.data.RelativeBindingHelper;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.element.input.GuiToggleButton;

import static net.vi.woodengears.client.gui.GuiLogisticNode.FONT_HEIGHT;

public class FacingSlot extends GuiToggleButton
{
    @Getter
    private String name;
    @Getter
    private int    slot;

    public FacingSlot(String name, int slots)
    {
        addStyleClass("facing-slot");

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

        GuiLabel slotsLabel = new GuiLabel(I18n.format(WoodenGears.MODID + ".gui.slot." + (slots < 2 ? "singular" : "plural")));
        slotsLabel.setWidthRatio(1);
        slotsLabel.setHeight(FONT_HEIGHT);
        addChild(slotsLabel);
        RelativeBindingHelper.bindToPos(slotsLabel, this, 0, 26 + FONT_HEIGHT / 2F);
    }
}
