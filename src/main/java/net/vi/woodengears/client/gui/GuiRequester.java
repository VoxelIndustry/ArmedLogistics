package net.vi.woodengears.client.gui;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.client.gui.component.InventoryView;
import net.vi.woodengears.client.gui.component.RequestView;
import net.vi.woodengears.client.gui.component.SOKCombo;
import net.vi.woodengears.common.tile.TileRequester;
import net.voxelindustry.brokkgui.paint.Texture;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.steamlayer.container.sync.SyncedValue;
import net.voxelindustry.steamlayer.network.action.ServerActionBuilder;
import net.voxelindustry.steamlayer.utils.ItemUtils;

public class GuiRequester extends GuiLogisticNode<TileRequester>
{
    private static final Texture BACKGROUND = new Texture(WoodenGears.MODID + ":textures/gui/requester.png");

    @Getter(AccessLevel.PROTECTED)
    private final InventoryView inventoryView;

    private final RequestView requestView;

    public GuiRequester(EntityPlayer player, TileRequester requester)
    {
        super(player, requester);

        this.setSize(276, 216);
        this.setxOffset(-50);

        super.title.setxTranslate(100);

        SOKCombo sokCombo = new SOKCombo();
        mainPanel.addChild(sokCombo, 0, 0);

        GuiAbsolutePane inventory = new GuiAbsolutePane();
        inventory.setSize(176, 216);
        inventory.setBackgroundTexture(BACKGROUND);
        mainPanel.addChild(inventory, 100, 0);

        inventoryView = new InventoryView(this, requester);
        inventory.addChild(inventoryView, 6, 52);

        this.updateStatusStyle();
        this.getListeners().attach(requester.getConnectedInventoryProperty(), obs -> updateStatusStyle());

        this.requestView = new RequestView(requester.getRequester().getRequests(), this::onRequestChange);
        this.getContainer().addSyncCallback("requests", this::onRequestSync);
        inventory.addChild(requestView, 6, 20);

        this.addStylesheet("/assets/" + WoodenGears.MODID + "/css/inventoryview.css");
        this.addStylesheet("/assets/" + WoodenGears.MODID + "/css/requestview.css");
        this.addStylesheet("/assets/" + WoodenGears.MODID + "/css/sokcombo.css");
    }

    private void onRequestChange(int index, ItemStack value)
    {
        if (index >= this.getTile().getRequester().getRequests().size() ||
                !ItemUtils.deepEqualsWithAmount(value, this.getTile().getRequester().getRequest(index)))
            new ServerActionBuilder("REQUEST_CHANGE").withInt("index", index).withItemStack("stack", value).toTile(this.getTile()).send();
    }

    private void onRequestSync(SyncedValue value)
    {
        for (int i = 0; i < this.getTile().getRequester().getRequests().size(); i++)
            requestView.setRequestStack(i, this.getTile().getRequester().getRequests().get(i).copy());
    }
}
