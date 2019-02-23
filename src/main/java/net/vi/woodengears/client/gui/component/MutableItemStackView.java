package net.vi.woodengears.client.gui.component;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.voxelindustry.brokkgui.BrokkGuiPlatform;
import net.voxelindustry.brokkgui.event.ClickEvent;
import net.voxelindustry.brokkgui.skin.GuiSkinBase;
import net.voxelindustry.brokkgui.wrapper.elements.ItemStackView;
import net.voxelindustry.brokkgui.wrapper.elements.ItemStackViewBehavior;
import net.voxelindustry.brokkgui.wrapper.elements.ItemStackViewSkin;
import net.voxelindustry.steamlayer.utils.ItemUtils;

public class MutableItemStackView extends ItemStackView
{
    private boolean                                 useCount;
    @Getter
    private ICopyPasteHandler<MutableItemStackView> copyPasteHandler;

    public MutableItemStackView(ItemStack stack, boolean useCount,
                                ICopyPasteHandler<MutableItemStackView> copyPasteHandler)
    {
        super(stack);

        this.useCount = useCount;
        this.copyPasteHandler = copyPasteHandler;
    }

    @Override
    protected GuiSkinBase<?> makeDefaultSkin()
    {
        return new ItemStackViewSkin(this, new MutableItemStackViewBehavior(this));
    }

    public boolean doesUseCount()
    {
        return useCount;
    }

    private static class MutableItemStackViewBehavior extends ItemStackViewBehavior
    {
        private MutableItemStackView view;

        public MutableItemStackViewBehavior(MutableItemStackView model)
        {
            super(model);

            model.getEventDispatcher().addHandler(ClickEvent.Left.TYPE, this::onLeftClick);
            model.getEventDispatcher().addHandler(ClickEvent.Right.TYPE, this::onRightClick);

            this.view = model;
        }

        private void onLeftClick(ClickEvent.Left event)
        {
            if (BrokkGuiPlatform.getInstance().getKeyboardUtil().isCtrlKeyDown())
            {
                if (view.getCopyPasteHandler().getClipboard() == null || view.getCopyPasteHandler().getClipboard() == view)
                    return;
                view.setItemStack(view.getCopyPasteHandler().getClipboard().getItemStack().copy());
                return;
            }

            // Set to HELD if STORED is empty
            // Grow of 1 if HELD is empty or equals to STORED
            // Set to MAX if SHIFT is down

            if (view.getItemStack().isEmpty() && !getHeldStack().isEmpty())
            {
                ItemStack copy = getHeldStack().copy();

                if (!view.doesUseCount())
                    copy.setCount(1);
                else if (BrokkGuiPlatform.getInstance().getKeyboardUtil().isShiftKeyDown())
                    copy.setCount(copy.getMaxStackSize());
                view.setItemStack(copy);
                return;
            }
            if (!view.doesUseCount())
                return;

            if (!view.getItemStack().isEmpty() &&
                    (getHeldStack().isEmpty() || ItemUtils.deepEquals(view.getItemStack(), getHeldStack())))
            {
                if (BrokkGuiPlatform.getInstance().getKeyboardUtil().isShiftKeyDown())
                    view.getItemStack().setCount(view.getItemStack().getMaxStackSize());
                else
                    view.getItemStack().grow(1);
            }
        }

        private void onRightClick(ClickEvent.Right event)
        {
            if (BrokkGuiPlatform.getInstance().getKeyboardUtil().isCtrlKeyDown())
            {
                view.getCopyPasteHandler().setClipboard(view);
                return;
            }

            if (BrokkGuiPlatform.getInstance().getKeyboardUtil().isShiftKeyDown())
                view.getItemStack().setCount(0);
            else
                view.getItemStack().shrink(1);
        }

        private ItemStack getHeldStack()
        {
            return Minecraft.getMinecraft().player.inventory.getItemStack();
        }
    }
}
