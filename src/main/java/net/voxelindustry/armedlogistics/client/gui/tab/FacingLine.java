package net.voxelindustry.armedlogistics.client.gui.tab;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumFacing;
import net.voxelindustry.armedlogistics.ArmedLogistics;
import net.voxelindustry.brokkgui.data.RectBox;
import net.voxelindustry.brokkgui.data.RelativeBindingHelper;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.shape.Rectangle;

public class FacingLine extends GuiLabel
{
    private static final RectBox TEXT_PADDING = RectBox.build().top(1).create();

    public FacingLine(FacingTab facingTab, EnumFacing facing)
    {
        super(I18n.format(ArmedLogistics.MODID + ".gui.facinglist.cardinal." + facing.name().toLowerCase()));

        addStyleClass("facing-line");
        addStyleClass("facing-" + facing.getName2());

        setSize(52, 11);
        setTextPadding(TEXT_PADDING);

        Rectangle closeIcon = new Rectangle();
        closeIcon.setSize(9, 9);
        closeIcon.addStyleClass("close-icon");
        addChild(closeIcon);
        closeIcon.setTranslate(1, 1);
        RelativeBindingHelper.bindToPos(closeIcon, this);

        closeIcon.setOnClickEvent(e -> facingTab.removeFacing(facing));
    }
}
