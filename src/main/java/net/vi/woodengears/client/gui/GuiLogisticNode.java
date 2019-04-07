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
    private final T tile;

    protected GuiAbsolutePane mainPanel;

    protected EditableName title;

    public GuiLogisticNode(EntityPlayer player, T tile)
    {
        super(tile.createContainer(player));

        this.tile = tile;

        mainPanel = new GuiAbsolutePane();
        this.setMainPanel(mainPanel);

        this.title = new EditableName(tile.getDisplayName()::getFormattedText, tile::setCustomName);
        mainPanel.addChild(title, 6, 6);
    }

    protected void updateStatusStyle()
    {
        if (tile.getConnectedInventoryProperty().getValue())
        {
            TileEntity tile =
                    Minecraft.getMinecraft().world.getTileEntity(this.tile.getPos().offset(this.tile.getFacing()));
            String name = tile.getDisplayName() != null ? tile.getDisplayName().getFormattedText() :
                    I18n.format("woodengears.gui.inventory.genericname");

            getInventoryView().setInvStatus(I18n.format("woodengears.gui.inventory.where", name,
                    I18n.format("woodengears.gui.facing." + this.tile.getFacing())));
            getInventoryView().setInvValid(true);
        }
        else
        {
            getInventoryView().setInvStatus(I18n.format("woodengears.gui.inventory.notwhere",
                    I18n.format("woodengears.gui.facing." + tile.getFacing())));
            getInventoryView().setInvValid(false);
        }
    }

    protected abstract InventoryView getInventoryView();
}
