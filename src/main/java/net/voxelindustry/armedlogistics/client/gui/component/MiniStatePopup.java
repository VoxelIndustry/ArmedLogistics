package net.voxelindustry.armedlogistics.client.gui.component;

import net.voxelindustry.brokkgui.animation.transition.SequentialTransition;
import net.voxelindustry.brokkgui.animation.transition.TranslateTransition;
import net.voxelindustry.brokkgui.animation.transition.WaitTransition;
import net.voxelindustry.brokkgui.component.GuiNode;
import net.voxelindustry.brokkgui.component.IGuiPopup;
import net.voxelindustry.brokkgui.data.RectBox;
import net.voxelindustry.brokkgui.data.RelativeBindingHelper;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.internal.PopupHandler;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.shape.ScissorBox;

import java.util.concurrent.TimeUnit;

public class MiniStatePopup extends GuiAbsolutePane implements IGuiPopup
{
    public MiniStatePopup(GuiNode parent, String text)
    {
        this.setID("mini-state-popup");

        RelativeBindingHelper.bindToPos(this, parent, 0, parent.getHeight());
        this.setyTranslate(-10);
        this.setHeight(10);

        GuiLabel label = new GuiLabel(text);
        label.setExpandToText(true);
        label.setHeight(10);
        label.setTextPadding(RectBox.build().left(2).top(1).create());
        this.addChild(label, 1, 1);

        this.setScissorBox(ScissorBox.withRegion(getxPos(), getyPos(), getxPos() + 200, getyPos() + 20));

        TranslateTransition translation = new TranslateTransition(this, 500, TimeUnit.MILLISECONDS);
        translation.setEndY(0);

        SequentialTransition anim = new SequentialTransition(this, translation,
                new WaitTransition(this, 1200, TimeUnit.MILLISECONDS));

        anim.setOnFinishEvent(e -> PopupHandler.getInstance(parent.getWindow()).removePopup(this));
        anim.start();
    }
}
