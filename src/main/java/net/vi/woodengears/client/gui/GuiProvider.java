package net.vi.woodengears.client.gui;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.client.gui.component.InventoryView;
import net.vi.woodengears.common.tile.TileProvider;
import net.voxelindustry.brokkgui.paint.Texture;

public class GuiProvider extends GuiLogisticNode<TileProvider>
{
    private static final Texture BACKGROUND = new Texture(WoodenGears.MODID + ":textures/gui/provider.png");

    @Getter(AccessLevel.PROTECTED)
    private final InventoryView inventoryView;

    public GuiProvider(EntityPlayer player, TileProvider provider)
    {
        super(player, provider);

        this.setWidth(176);
        this.setHeight(176);

        mainPanel.setBackgroundTexture(BACKGROUND);

        inventoryView = new InventoryView(this, provider);
        mainPanel.addChild(inventoryView, 6, 17);

        this.updateStatusStyle();
        this.getListeners().attach(provider.getConnectedInventoryProperty(), obs -> updateStatusStyle());

        this.addStylesheet("/assets/" + WoodenGears.MODID + "/css/provider.css");
        this.addStylesheet("/assets/" + WoodenGears.MODID + "/css/inventoryview.css");
    }
}
