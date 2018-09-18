package net.vi.woodengears.client;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.common.gui.InventoryView;
import net.vi.woodengears.common.tile.TileProvider;
import net.voxelindustry.steamlayer.container.BuiltContainer;
import org.yggard.brokkgui.data.RectOffset;
import org.yggard.brokkgui.element.GuiLabel;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;

public class GuiProvider extends BrokkGuiContainer<BuiltContainer>
{
    private static final Texture BACKGROUND = new Texture(WoodenGears.MODID + ":textures/gui/provider.png",0,0,1, 185 / 192f);

    @Getter
    private final TileProvider        provider;

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

        mainPanel.addChild(new InventoryView(this), 0, 32);

        this.addStylesheet("/assets/woodengears/css/provider.css");
    }

    private void updateStatusStyle(GuiLabel status)
    {
        if (provider.getConnectedInventoryProperty().getValue())
        {
            status.addStyleClass("status-valid");
            status.removeStyleClass("status-invalid");
            status.setText("Inventory at " + TextFormatting.ITALIC + provider.getFacing());
        }
        else
        {
            status.addStyleClass("status-invalid");
            status.removeStyleClass("status-valid");
            status.setText("No inventory at " + TextFormatting.ITALIC + provider.getFacing());
        }
    }
}
