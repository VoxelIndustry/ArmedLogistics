package net.vi.woodengears.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.client.gui.component.InventoryView;
import net.vi.woodengears.common.tile.TileRequester;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.paint.Texture;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.wrapper.container.BrokkGuiContainer;
import net.voxelindustry.steamlayer.container.BuiltContainer;

public class GuiRequester extends BrokkGuiContainer<BuiltContainer>
{
    private static final Texture BACKGROUND = new Texture(WoodenGears.MODID + ":textures/gui/requester.png");

    private final TileRequester requester;
    private final InventoryView inventoryView;

    public GuiRequester(EntityPlayer player, TileRequester requester)
    {
        super(requester.createContainer(player));
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.setWidth(176);
        this.setHeight(176);

        this.requester = requester;

        GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        mainPanel.setBackgroundTexture(BACKGROUND);
        this.setMainPanel(mainPanel);

        GuiLabel title = new GuiLabel(requester.getDisplayName().getFormattedText());
        mainPanel.addChild(title, 6, 6);

        inventoryView = new InventoryView(this, requester);
        mainPanel.addChild(inventoryView, 6, 17);

        this.updateStatusStyle();
        this.getListeners().attach(requester.getConnectedInventoryProperty(), obs -> updateStatusStyle());

        this.addStylesheet("/assets/" + WoodenGears.MODID + "/css/inventoryview.css");
    }

    private void updateStatusStyle()
    {
        if (requester.getConnectedInventoryProperty().getValue())
        {
            TileEntity tile =
                    Minecraft.getMinecraft().world.getTileEntity(requester.getPos().offset(requester.getFacing()));
            String name = tile.getDisplayName() != null ? tile.getDisplayName().getFormattedText() :
                    I18n.format("woodengears.gui.inventory.genericname");

            inventoryView.setInvStatus(I18n.format("woodengears.gui.inventory.where", name,
                    I18n.format("woodengears.gui.facing." + requester.getFacing())));
            inventoryView.setInvValid(true);
        }
        else
        {
            inventoryView.setInvStatus(I18n.format("woodengears.gui.inventory.notwhere",
                    I18n.format("woodengears.gui.facing." + requester.getFacing())));
            inventoryView.setInvValid(false);
        }
    }
}
