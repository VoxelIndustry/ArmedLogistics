package net.vi.woodengears.client.gui.component;

import net.voxelindustry.brokkgui.animation.transition.SequentialTransition;
import net.voxelindustry.brokkgui.animation.transition.TranslateTransition;
import net.voxelindustry.brokkgui.animation.transition.WaitTransition;
import net.voxelindustry.brokkgui.component.GuiNode;
import net.voxelindustry.brokkgui.component.IGuiPopup;
import net.voxelindustry.brokkgui.data.RectOffset;
import net.voxelindustry.brokkgui.data.RelativeBindingHelper;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.internal.PopupHandler;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.shape.Rectangle;
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
        label.setTextPadding(RectOffset.build().left(2).top(1).create());
        this.addChild(label, 1, 1);

        Rectangle leftLine = new Rectangle();
        this.addChild(leftLine, 0, 1);
        leftLine.setWidth(1);
        leftLine.setHeight(10);
        leftLine.addStyleClass("mini-state-popup-line");

        Rectangle topLine = new Rectangle();
        this.addChild(topLine, 1, 0);
        topLine.setWidth(label.getWidth());
        topLine.setHeight(1);
        topLine.addStyleClass("mini-state-popup-line");

        Rectangle rightLine = new Rectangle();
        this.addChild(rightLine, label.getWidth() + 1, 1);
        rightLine.setWidth(1);
        rightLine.setHeight(10);
        rightLine.addStyleClass("mini-state-popup-line");

        Rectangle bottomLine = new Rectangle();
        this.addChild(bottomLine, 1, label.getHeight() + 1);
        bottomLine.setWidth(label.getWidth());
        bottomLine.setHeight(1);
        bottomLine.addStyleClass("mini-state-popup-line");

        this.setScissorBox(ScissorBox.withRegion(getxPos(), getyPos(), getxPos() + 200, getyPos() + 20));

        TranslateTransition translation = new TranslateTransition(this, 500, TimeUnit.MILLISECONDS);
        translation.setEndY(0);

        SequentialTransition anim = new SequentialTransition(this, translation,
                new WaitTransition(this, 1200, TimeUnit.MILLISECONDS));

        anim.setOnFinishEvent(e -> PopupHandler.getInstance().removePopup(this));

        anim.start();
    }
}
