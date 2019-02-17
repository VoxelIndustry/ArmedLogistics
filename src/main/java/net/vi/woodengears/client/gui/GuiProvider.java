package net.vi.woodengears.client.gui;

import lombok.Getter;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
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
    private static final Texture BACKGROUND = new Texture(WoodenGears.MODID + ":textures/gui/provider.png",
            0, 0, 1, 185 / 192f);

    @Getter
    private final TileProvider provider;

    public GuiProvider(EntityPlayer player, TileProvider provider)
    {
        super(provider.createContainer(player));
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.setWidth(176);
        this.setHeight(185);

        this.provider = provider;

        GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        mainPanel.setBackgroundTexture(BACKGROUND);
        this.setMainPanel(mainPanel);

        GuiLabel title = new GuiLabel(provider.getDisplayName().getFormattedText());
        mainPanel.addChild(title, 6, 6);

        GuiLabel status = new GuiLabel();
        status.setTextPadding(new RectOffset(1, 0, 0, 0));
        status.setSize(110, 11);
        status.setID("status-label");
        this.updateStatusStyle(status);
        this.getListeners().attach(provider.getConnectedInventoryProperty(), obs -> updateStatusStyle(status));

        mainPanel.addChild(status, 88 - 55, 16);

        mainPanel.addChild(new InventoryView(this), 6, 32);

        this.addStylesheet("/assets/" + WoodenGears.MODID + "/css/provider.css");
        this.addStylesheet("/assets/" + WoodenGears.MODID + "/css/inventoryview.css");
    }

    private void updateStatusStyle(GuiLabel status)
    {
        if (provider.getConnectedInventoryProperty().getValue())
        {
            status.addStyleClass("status-valid");
            status.removeStyleClass("status-invalid");
            status.setText(I18n.format("woodengears.gui.inventory.where",
                    I18n.format("woodengears.gui.facing." + provider.getFacing())));
        }
        else
        {
            status.addStyleClass("status-invalid");
            status.removeStyleClass("status-valid");
            status.setText(I18n.format("woodengears.gui.inventory.notwhere",
                    I18n.format("woodengears.gui.facing." + provider.getFacing())));        }
    }
}
