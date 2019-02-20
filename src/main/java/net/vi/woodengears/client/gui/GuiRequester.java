package net.vi.woodengears.client.gui;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.client.gui.component.InventoryView;
import net.vi.woodengears.common.tile.TileRequester;
import net.voxelindustry.brokkgui.paint.Texture;

public class GuiRequester extends GuiLogisticNode<TileRequester>
{
    private static final Texture BACKGROUND = new Texture(WoodenGears.MODID + ":textures/gui/requester.png");

    @Getter(AccessLevel.PROTECTED)
    private final InventoryView inventoryView;

    public GuiRequester(EntityPlayer player, TileRequester requester)
    {
        super(player, requester);

        this.setWidth(176);
        this.setHeight(176);

        mainPanel.setBackgroundTexture(BACKGROUND);

        inventoryView = new InventoryView(this, requester);
        mainPanel.addChild(inventoryView, 6, 17);

        this.updateStatusStyle();
        this.getListeners().attach(requester.getConnectedInventoryProperty(), obs -> updateStatusStyle());

        this.addStylesheet("/assets/" + WoodenGears.MODID + "/css/inventoryview.css");
    }
}
