package net.vi.woodengears.client.gui;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.vi.woodengears.client.gui.component.EditableName;
import net.vi.woodengears.client.gui.component.InventoryView;
import net.vi.woodengears.common.tile.TileLogicisticNode;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.wrapper.container.BrokkGuiContainer;
import net.voxelindustry.steamlayer.container.BuiltContainer;

public abstract class GuiLogisticNode<T extends TileLogicisticNode> extends BrokkGuiContainer<BuiltContainer>
{
    @Getter
    private final T node;

    protected GuiAbsolutePane mainPanel;

    public GuiLogisticNode(EntityPlayer player, T node)
    {
        super(node.createContainer(player));
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.node = node;

        mainPanel = new GuiAbsolutePane();
        this.setMainPanel(mainPanel);

        EditableName title = new EditableName(node.getDisplayName()::getFormattedText, node::setCustomName);
        mainPanel.addChild(title, 6, 6);
    }

    protected void updateStatusStyle()
    {
        if (node.getConnectedInventoryProperty().getValue())
        {
            TileEntity tile =
                    Minecraft.getMinecraft().world.getTileEntity(node.getPos().offset(node.getFacing()));
            String name = tile.getDisplayName() != null ? tile.getDisplayName().getFormattedText() :
                    I18n.format("woodengears.gui.inventory.genericname");

            getInventoryView().setInvStatus(I18n.format("woodengears.gui.inventory.where", name,
                    I18n.format("woodengears.gui.facing." + node.getFacing())));
            getInventoryView().setInvValid(true);
        }
        else
        {
            getInventoryView().setInvStatus(I18n.format("woodengears.gui.inventory.notwhere",
                    I18n.format("woodengears.gui.facing." + node.getFacing())));
            getInventoryView().setInvValid(false);
        }
    }

    protected abstract InventoryView getInventoryView();
}
