package net.vi.woodengears.client.gui;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.common.tile.TileProvider;
import net.voxelindustry.brokkgui.data.RectOffset;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.paint.Texture;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.wrapper.container.BrokkGuiContainer;
import net.voxelindustry.steamlayer.container.BuiltContainer;

public class GuiProvider extends BrokkGuiContainer<BuiltContainer>
{
    private static final Texture BACKGROUND = new Texture(WoodenGears.MODID + ":textures/gui/provider.png");

    @Getter
    private final TileProvider  provider;
    private final InventoryView inventoryView;

    public GuiProvider(EntityPlayer player, TileProvider provider)
    {
        super(provider.createContainer(player));
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.setWidth(176);
        this.setHeight(176);

        this.provider = provider;

        GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        mainPanel.setBackgroundTexture(BACKGROUND);
        this.setMainPanel(mainPanel);

        GuiLabel title = new GuiLabel(provider.getDisplayName().getFormattedText());
        mainPanel.addChild(title, 6, 6);

        inventoryView = new InventoryView(this);
        mainPanel.addChild(inventoryView, 6, 17);

        this.updateStatusStyle();
        this.getListeners().attach(provider.getConnectedInventoryProperty(), obs -> updateStatusStyle());

        this.addStylesheet("/assets/" + WoodenGears.MODID + "/css/provider.css");
        this.addStylesheet("/assets/" + WoodenGears.MODID + "/css/inventoryview.css");
    }

    private void updateStatusStyle()
    {
        if (provider.getConnectedInventoryProperty().getValue())
        {
            TileEntity tile =
                    Minecraft.getMinecraft().world.getTileEntity(provider.getPos().offset(provider.getFacing()));
            String name = tile.getDisplayName() != null ? tile.getDisplayName().getFormattedText() :
                    I18n.format("woodengears.gui.inventory.genericname");

            inventoryView.setInvStatus(I18n.format("woodengears.gui.inventory.where", name,
                    I18n.format("woodengears.gui.facing." + provider.getFacing())));
            inventoryView.setInvValid(true);
        }
        else
        {
            inventoryView.setInvStatus(I18n.format("woodengears.gui.inventory.notwhere",
                    I18n.format("woodengears.gui.facing." + provider.getFacing())));
            inventoryView.setInvValid(false);
        }
    }
}
