package net.vi.woodengears.client.gui;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.vi.woodengears.WoodenGears;
import net.vi.woodengears.client.gui.component.InventoryView;
import net.vi.woodengears.client.gui.component.RequestView;
import net.vi.woodengears.client.gui.component.SOKCombo;
import net.vi.woodengears.common.grid.logistic.node.RequesterMode;
import net.vi.woodengears.common.tile.TileRequester;
import net.voxelindustry.brokkgui.component.GuiNode;
import net.voxelindustry.brokkgui.paint.Texture;
import net.voxelindustry.steamlayer.container.sync.SyncedValue;
import net.voxelindustry.steamlayer.network.action.ServerActionBuilder;
import net.voxelindustry.steamlayer.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public class GuiRequester extends GuiLogisticNode<TileRequester>
{
    private static final Texture BACKGROUND = new Texture(WoodenGears.MODID + ":textures/gui/requester.png");

    @Getter(AccessLevel.PROTECTED)
    private final InventoryView inventoryView;

    private final RequestView requestView;
    private final SOKCombo    sokCombo;
    private final int         sokOffset;

    private final List<GuiNode> elements = new ArrayList<>();

    public GuiRequester(EntityPlayer player, TileRequester requester)
    {
        super(player, requester);

        sokCombo = new SOKCombo(requester.getRequester().getMode(), this::onModeChange);
        getContainer().addSyncCallback("mode", this::onModeSync);
        getMainPanel().addChild(sokCombo, 0, 4 + TAB_HEIGHT);

        sokOffset = (int) sokCombo.getWidth() + 4;
        getContainer().inventorySlots.forEach(slot -> slot.xPos += sokOffset / 2 + 1);

        inventoryView = new InventoryView(this, requester);
        body.addChild(inventoryView, 6, 52);

        updateStatusStyle();
        getListeners().attach(requester.getConnectedInventoryProperty(), obs -> updateStatusStyle());

        requestView = new RequestView(requester.getRequester().getRequests(), this::onRequestChange);
        getContainer().addSyncCallback("requests", this::onRequestSync);
        body.addChild(requestView, 6, 20);

        elements.add(inventoryView);
        elements.add(requestView);
        elements.add(sokCombo);

        addStylesheet("/assets/" + WoodenGears.MODID + "/css/inventoryview.css");
        addStylesheet("/assets/" + WoodenGears.MODID + "/css/requestview.css");
        addStylesheet("/assets/" + WoodenGears.MODID + "/css/sokcombo.css");
        addStylesheet("/assets/" + WoodenGears.MODID + "/css/tabheader.css");
        addStylesheet("/assets/" + WoodenGears.MODID + "/css/facingtab.css");
    }

    @Override
    public float getTabOffsetX()
    {
        return sokOffset;
    }

    @Override
    protected Texture getBackgroundTexture()
    {
        return BACKGROUND;
    }

    @Override
    protected int getSurvivalInventoryOffset()
    {
        return 88;
    }

    private void onModeChange(RequesterMode mode)
    {
        if (mode != getTile().getRequester().getMode())
            new ServerActionBuilder("MODE_CHANGE").withInt("mode", mode.ordinal()).toTile(getTile()).send();
    }

    private void onModeSync(SyncedValue value)
    {
        sokCombo.setMode(getTile().getRequester().getMode());
    }

    private void onRequestChange(int index, ItemStack value)
    {
        if (index >= getTile().getRequester().getRequests().size() ||
                !ItemUtils.deepEqualsWithAmount(value, getTile().getRequester().getRequest(index)))
            new ServerActionBuilder("REQUEST_CHANGE").withInt("index", index).withItemStack("stack", value).toTile(getTile()).send();
    }

    private void onRequestSync(SyncedValue value)
    {
        for (int i = 0; i < 9; i++)
        {
            if (i >= getTile().getRequester().getRequests().size())
                requestView.setRequestStack(i, ItemStack.EMPTY);
            else
                requestView.setRequestStack(i, getTile().getRequester().getRequests().get(i).copy());
        }
    }

    @Override
    public List<GuiNode> getElements()
    {
        return elements;
    }
}
